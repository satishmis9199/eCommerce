package com.e_commerce.eCommerce.service;

import com.e_commerce.eCommerce.dto.request.ActiveStatusRequest;
import com.e_commerce.eCommerce.dto.request.PlanRequest;
import com.e_commerce.eCommerce.dto.response.PlanResponse;
import com.e_commerce.eCommerce.dto.response.PublicPlansResponseDTO;
import com.e_commerce.eCommerce.entity.Feature;
import com.e_commerce.eCommerce.entity.Plan;
import com.e_commerce.eCommerce.entity.PlanFeature;
import com.e_commerce.eCommerce.repository.FeatureRepository;
import com.e_commerce.eCommerce.repository.PlanFeatureRepository;
import com.e_commerce.eCommerce.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanService {

    private final PlanRepository planRepository;
    private final PlanFeatureRepository planFeatureRepository;
    private final FeatureRepository featureRepository;


    public List<PlanResponse> getAllPlans() {
        long totalFeatures = featureRepository.countByActive(true);

        return planRepository.findAllByOrderByIdAsc()
                .stream()
                .map(plan -> toResponse(plan, totalFeatures))
                .toList();
    }

    @Transactional
    public PlanResponse createPlan(
            PlanRequest request,
            CustomUserDetail admin
    ) {
        String code = normalizeCode(request.getCode());

        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Plan code is required");
        }

        if (planRepository.existsByCode(code)) {
            throw new IllegalArgumentException(
                    "A plan with code '" + code + "' already exists"
            );
        }



        Plan plan = Plan.builder()
                .code(code)
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .active(request.getActive() == null || request.getActive())
                .build();

        Plan saved = planRepository.save(plan);
        List<Feature> coreFeatures=featureRepository.findAllByCore(true);
        List<PlanFeature> planFeatures=new ArrayList<>();
        for (Feature feature : coreFeatures) {

            PlanFeature planFeature = PlanFeature.builder()
                    .plan(plan)
                    .feature(feature)
                    .enabled(true)
                    .build();
            planFeatures.add(planFeature);


        }
        planFeatureRepository.saveAll(planFeatures);
        return toResponse(
                saved,
                featureRepository.count()
        );
    }

    @Transactional
    public PlanResponse updatePlan(
            Long id,
            PlanRequest request,
            CustomUserDetail admin
    ) {
        Plan plan = planRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Plan not found: " + id
                        )
                );

        String code = normalizeCode(request.getCode());

        if (code != null
                && !code.isBlank()
                && planRepository.existsByCodeAndIdNot(code, id)) {

            throw new IllegalArgumentException(
                    "A plan with code '" + code + "' already exists"
            );
        }

        if (code != null && !code.isBlank()) {
            plan.setCode(code);
        }

        if (request.getName() != null) {
            plan.setName(request.getName());
        }

        if (request.getDescription() != null) {
            plan.setDescription(request.getDescription());
        }

        if (request.getPrice() != null) {
            plan.setPrice(request.getPrice());
        }

        if (request.getActive() != null) {
            plan.setActive(request.getActive());
        }

        Plan saved = planRepository.save(plan);

        return toResponse(
                saved,
                featureRepository.count()
        );
    }

    @Transactional
    public PlanResponse setActive(
            ActiveStatusRequest request,
            CustomUserDetail admin
    ) {
        Plan plan = planRepository.findById(request.getId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Plan not found: " + request.getId()
                        )
                );

        boolean newValue =
                request.getActive() != null && request.getActive();

        plan.setActive(newValue);

        Plan saved = planRepository.save(plan);

        return toResponse(
                saved,
                featureRepository.count()
        );
    }

    private PlanResponse toResponse(
            Plan plan,
            long totalFeatureCount
    ) {
        long enabledCount =
                planFeatureRepository.countEnabledFeaturesByPlanId(plan.getId());
        List<String> activeFeatureInPlan=planRepository.findActiveFeatureNamesForPlan(plan.getId());
        return PlanResponse.builder()
                .id(plan.getId())
                .code(plan.getCode())
                .name(plan.getName())
                .description(plan.getDescription())
                .price(plan.getPrice())
                .active(plan.getActive())
                .enabledFeatureCount(enabledCount)
                .totalFeatureCount(totalFeatureCount)
                .createdAt(plan.getCreatedAt())
                .updatedAt(plan.getUpdatedAt())
                .features(activeFeatureInPlan)
                .build();
    }

    private String normalizeCode(String code) {
        return code == null
                ? null
                : code.trim()
                .toUpperCase()
                .replace(" ", "_");
    }

    @Transactional(readOnly = true)
    public List<PublicPlansResponseDTO> getAvailablePlans() {

        List<Plan> plans = planRepository.findAllByOrderByIdAsc();
        List<PublicPlansResponseDTO> publicPlansResponseDTOS=new ArrayList<>();
        for(Plan plan:plans){
            List<String> featureName=featureRepository.findAllFeatureNameWithPlanId(plan.getId());
            PublicPlansResponseDTO publicPlansResponseDTO= PublicPlansResponseDTO.builder()
                    .id(plan.getId())
                    .name(plan.getName())
                    .price(plan.getPrice())
                    .description(plan.getDescription())
                    .billingCycle("MONTHLY")
                    .features(featureName)
                    .build();
            publicPlansResponseDTOS.add(publicPlansResponseDTO);
        }
        return publicPlansResponseDTOS;


    }
}