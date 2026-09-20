package com.e_commerce.eCommerce.service;

import com.e_commerce.eCommerce.dto.request.PlanFeatureMappingRequest;
import com.e_commerce.eCommerce.dto.request.PlanFeatureMappingRequest.FeatureToggle;
import com.e_commerce.eCommerce.dto.response.PlanFeatureMappingResponse;
import com.e_commerce.eCommerce.dto.response.PlanFeatureMappingResponse.FeatureMapping;
import com.e_commerce.eCommerce.entity.Feature;
import com.e_commerce.eCommerce.entity.Plan;
import com.e_commerce.eCommerce.entity.PlanFeature;
import com.e_commerce.eCommerce.entity.Vendor;
import com.e_commerce.eCommerce.repository.FeatureRepository;
import com.e_commerce.eCommerce.repository.PlanFeatureRepository;
import com.e_commerce.eCommerce.repository.PlanRepository;
import com.e_commerce.eCommerce.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlanFeatureService {

    private final PlanRepository planRepository;
    private final FeatureRepository featureRepository;
    private final PlanFeatureRepository planFeatureRepository;
    private final VendorRepository vendorRepository;

    @Transactional(readOnly = true)
    public PlanFeatureMappingResponse getMapping(Long planId) {
        return buildResponse(findPlan(planId));
    }

    @Transactional
    public PlanFeatureMappingResponse saveMapping(
            PlanFeatureMappingRequest request,
            CustomUserDetail admin
    ) {
        if (request == null || request.getPlanId() == null) {
            throw new IllegalArgumentException("Plan id is required");
        }

        if (request.getFeatures() == null) {
            throw new IllegalArgumentException("Features list is required");
        }

        Plan plan = findPlan(request.getPlanId());

        Map<Long, Boolean> requested = new LinkedHashMap<>();

        for (FeatureToggle item : request.getFeatures()) {

            if (item == null || item.getFeatureId() == null) {
                throw new IllegalArgumentException(
                        "Feature id is required for every item"
                );
            }

            if (item.getEnabled() == null) {
                throw new IllegalArgumentException(
                        "Enabled flag is required for feature "
                                + item.getFeatureId()
                );
            }

            if (requested.put(item.getFeatureId(), item.getEnabled()) != null) {
                throw new IllegalArgumentException(
                        "Duplicate feature id: " + item.getFeatureId()
                );
            }
        }

        Map<Long, Feature> features =
                featureRepository.findAllById(requested.keySet())
                        .stream()
                        .collect(Collectors.toMap(
                                Feature::getId,
                                feature -> feature
                        ));

        List<Long> missing =
                requested.keySet()
                        .stream()
                        .filter(id -> !features.containsKey(id))
                        .toList();

        if (!missing.isEmpty()) {
            throw new IllegalArgumentException(
                    "Feature(s) not found: " + missing
            );
        }

        List<PlanFeature> existingMappings =
                planFeatureRepository.findByPlanId(plan.getId());

        Map<Long, PlanFeature> existing =
                existingMappings.stream()
                        .collect(Collectors.toMap(
                                pf -> pf.getFeature().getId(),
                                pf -> pf
                        ));

        List<PlanFeature> toSave = new ArrayList<>();

        for (Map.Entry<Long, Boolean> entry : requested.entrySet()) {

            Long featureId = entry.getKey();
            Boolean enabled = entry.getValue();

            PlanFeature mapping = existing.get(featureId);

            if (mapping == null) {

                mapping = PlanFeature.builder()
                        .plan(plan)
                        .feature(features.get(featureId))
                        .enabled(enabled)
                        .build();

                toSave.add(mapping);

            } else if (!Objects.equals(mapping.getEnabled(), enabled)) {

                mapping.setEnabled(enabled);
                toSave.add(mapping);
            }
        }

        planFeatureRepository.saveAll(toSave);


        return buildResponse(plan);
    }

    private Plan findPlan(Long planId) {
        return planRepository.findById(planId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Plan not found with id: " + planId
                        )
                );
    }

    private PlanFeatureMappingResponse buildResponse(Plan plan) {

        Set<Long> enabledIds =
                planFeatureRepository.findByPlanId(plan.getId())
                        .stream()
                        .filter(pf -> Boolean.TRUE.equals(pf.getEnabled()))
                        .map(pf -> pf.getFeature().getId())
                        .collect(Collectors.toSet());

        List<FeatureMapping> items =
                featureRepository.findAll()
                        .stream()
                        .sorted(Comparator.comparing(Feature::getId))
                        .map(feature ->
                                new FeatureMapping(
                                        feature.getId(),
                                        feature.getCode(),
                                        feature.getName(),
                                        enabledIds.contains(feature.getId())
                                )
                        )
                        .toList();

        return new PlanFeatureMappingResponse(
                plan.getId(),
                plan.getName(),
                items
        );
    }
    public boolean hasFeature(Long planId, String featureCode) {

        if (planId == null || featureCode == null || featureCode.isBlank()) {
            return false;
        }

        return planFeatureRepository.hasFeature(
                planId,
                featureCode
        );
    }
    @Transactional(readOnly = true)
    public List<String> getEnabledFeatureCodes(
            CustomUserDetail userDetail,
            String tenantId
    ) {

        if (tenantId == null || tenantId.isBlank()) {
            throw new IllegalArgumentException("Tenant ID is required");
        }

        Optional<Vendor> vendorOptional =
                vendorRepository.findByTenantId(tenantId);

        if (vendorOptional.isEmpty()) {
            throw new IllegalArgumentException("Invalid vendor");
        }

        Vendor vendor = vendorOptional.get();

        Long planId = vendor.getPlanId();

        if (planId == null) {
            return List.of();
        }

        List<PlanFeature> planFeatures =
                planFeatureRepository.findByPlanId(planId);

        if (planFeatures == null || planFeatures.isEmpty()) {
            return List.of();
        }

        return planFeatures.stream()
                .filter(Objects::nonNull)
                .filter(pf -> Boolean.TRUE.equals(pf.getEnabled()))
                .map(PlanFeature::getFeature)
                .filter(Objects::nonNull)
                .filter(feature -> Boolean.TRUE.equals(feature.getActive()))
                .map(Feature::getCode)
                .filter(code -> code != null && !code.isBlank())
                .map(String::trim)
                .toList();
    }
}