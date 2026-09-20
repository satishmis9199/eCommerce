package com.e_commerce.eCommerce.repository;

import com.e_commerce.eCommerce.entity.Feature;
import com.e_commerce.eCommerce.entity.Plan;
import com.e_commerce.eCommerce.entity.PlanFeature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FeatureRepository extends JpaRepository<Feature,Long> {
    List<Feature> findAllByOrderByIdAsc();

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, Long id);

    long countByActive(boolean b);

    List<Feature> findAllByCore(boolean b);

    @Query("""
    SELECT f.name
    FROM Feature f
    JOIN f.planFeatures pf
    JOIN pf.plan p
    WHERE p.id = :planId
      AND pf.enabled = true
""")
    List<String> findAllFeatureNameWithPlanId(@Param("planId") Long planId);
}
