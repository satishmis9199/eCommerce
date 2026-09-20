package com.e_commerce.eCommerce.repository;

import com.e_commerce.eCommerce.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface PlanRepository extends JpaRepository<Plan,Long> {
    List<Plan> findAllByOrderByIdAsc();

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, Long id);

    @Query("""
    SELECT f.name
    FROM PlanFeature pf
    JOIN pf.feature f
    WHERE pf.plan.id = :planId
      AND pf.enabled = true
      AND f.active = true
""")
    List<String> findActiveFeatureNamesForPlan(@Param("planId") Long planId);
}
