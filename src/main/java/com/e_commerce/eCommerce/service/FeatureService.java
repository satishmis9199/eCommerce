package com.e_commerce.eCommerce.service;

import com.e_commerce.eCommerce.dto.request.ActiveStatusRequest;
import com.e_commerce.eCommerce.dto.request.FeatureRequest;
import com.e_commerce.eCommerce.dto.response.FeatureResponse;
import com.e_commerce.eCommerce.entity.Feature;
import com.e_commerce.eCommerce.repository.FeatureRepository;
import com.e_commerce.eCommerce.repository.PlanFeatureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeatureService {

    private final FeatureRepository featureRepository;
    private final PlanFeatureRepository planFeatureRepository;


    public List<FeatureResponse> getAllFeatures() {
        return featureRepository.findAllByOrderByIdAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public FeatureResponse createFeature(FeatureRequest request, CustomUserDetail admin) {
        String code = normalizeCode(request.getCode());
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Feature code is required");
        }
        if (featureRepository.existsByCode(code)) {
            throw new IllegalArgumentException("A feature with code '" + code + "' already exists");
        }

        Feature feature = Feature.builder()
                .code(code)
                .name(request.getName())
                .description(request.getDescription())
                .active(request.getActive() == null || request.getActive())
                .build();

        Feature saved = featureRepository.save(feature);
//        auditLogService.record(admin, "FEATURE_CREATED", null, saved.getCode(), null, saved.getName());
        // No plan currently references this new feature yet, so no cache eviction is needed.
        return toResponse(saved);
    }

    @Transactional
    public FeatureResponse updateFeature(Long id, FeatureRequest request, CustomUserDetail admin) {
        Feature feature = featureRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Feature not found: " + id));

        String code = normalizeCode(request.getCode());
        if (code != null && !code.isBlank() && featureRepository.existsByCodeAndIdNot(code, id)) {
            throw new IllegalArgumentException("A feature with code '" + code + "' already exists");
        }

        String oldName = feature.getName();
        if (code != null && !code.isBlank()) {
            feature.setCode(code);
        }
        if (request.getName() != null) {
            feature.setName(request.getName());
        }
        if (request.getDescription() != null) {
            feature.setDescription(request.getDescription());
        }
        if (request.getActive() != null) {
            feature.setActive(request.getActive());
        }

        Feature saved = featureRepository.save(feature);
//        auditLogService.record(admin, "FEATURE_UPDATED", null, saved.getCode(), oldName, saved.getName());
//        tenantFeatureService.evictAllTenantFeatureCache();
        return toResponse(saved);
    }

    @Transactional
    public FeatureResponse setActive(ActiveStatusRequest request, CustomUserDetail admin) {
        Feature feature = featureRepository.findById(request.getId())
                .orElseThrow(() -> new IllegalArgumentException("Feature not found: " + request.getId()));

        boolean oldValue = feature.getActive();
        boolean newValue = request.getActive() != null && request.getActive();
        feature.setActive(newValue);
        Feature saved = featureRepository.save(feature);
        return toResponse(saved);
    }

    private FeatureResponse toResponse(Feature feature) {
        long assignedPlanCount = planFeatureRepository.findByFeatureIdWithPlan(feature.getId())
                .stream()
                .filter(pf -> pf.getEnabled() && pf.getPlan().getActive())
                .count();

        return FeatureResponse.builder()
                .id(feature.getId())
                .code(feature.getCode())
                .name(feature.getName())
                .description(feature.getDescription())
                .active(feature.getActive())
                .assignedPlanCount(assignedPlanCount)
                .createdAt(feature.getCreatedAt())
                .updatedAt(feature.getUpdatedAt())
                .build();
    }

    private String normalizeCode(String code) {
        return code == null ? null : code.trim().toUpperCase().replace(" ", "_");
    }
}