package com.e_commerce.eCommerce.repository;

import com.e_commerce.eCommerce.entity.PlanFeature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PlanFeatureRepository extends JpaRepository<PlanFeature, Long> {

    List<PlanFeature> findByPlanId(Long planId);

    @Query("""
        SELECT COUNT(pf)
        FROM PlanFeature pf
        JOIN pf.feature f
        WHERE pf.plan.id = :planId
          AND pf.enabled = true
          AND f.active = true
    """)
    long countEnabledFeaturesByPlanId(@Param("planId") Long planId);

    @Query("""
        SELECT pf
        FROM PlanFeature pf
        JOIN FETCH pf.plan
        JOIN FETCH pf.feature
        WHERE pf.feature.id = :featureId
    """)
    List<PlanFeature> findByFeatureIdWithPlan(
            @Param("featureId") Long featureId
    );

    @Query("""
        SELECT pf
        FROM PlanFeature pf
        JOIN FETCH pf.plan
        JOIN FETCH pf.feature
        WHERE pf.feature.id = :featureId
          AND pf.plan.id = :planId
    """)
    Optional<PlanFeature> findByFeatureIdAndPlanId(
            @Param("featureId") Long featureId,
            @Param("planId") Long planId
    );

    @Query("""
        SELECT COUNT(pf) > 0
        FROM PlanFeature pf
        JOIN pf.feature f
        WHERE pf.plan.id = :planId
          AND f.code = :featureCode
          AND pf.enabled = true
          AND f.active = true
    """)
    boolean hasFeature(
            @Param("planId") Long planId,
            @Param("featureCode") String featureCode
    );
}