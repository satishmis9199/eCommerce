package com.e_commerce.eCommerce.config;

import com.e_commerce.eCommerce.CustomAnnotation.AditLogAnnotate;
import com.e_commerce.eCommerce.CustomAnnotation.RequiresFeature;
import com.e_commerce.eCommerce.entity.EvaluationAuditTrail;
import com.e_commerce.eCommerce.repository.AuditLogRepository;
import com.e_commerce.eCommerce.service.AuditLogService;
import com.e_commerce.eCommerce.service.PlanFeatureService;
import com.e_commerce.eCommerce.service.VendorService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
@RequiredArgsConstructor
public class FeatureAccessAspect {

    private final PlanFeatureService planFeatureService;
    private final VendorService vendorService;
    private final AuditLogService auditLogService;

    @Around("@annotation(requiresFeature)")
    public Object checkFeatureAccess(
            ProceedingJoinPoint joinPoint,
            RequiresFeature requiresFeature
    ) throws Throwable {

        String featureCode = requiresFeature.value();
        String tenantId = TenantContext.getTenantId();
        if (tenantId == null || tenantId.isBlank()) {
            throw new AccessDeniedException(
                    "Tenant not found"
            );
        }
        Long planId =
                vendorService.getPlanIdByTenantId(tenantId);

        if (planId == null) {
            throw new AccessDeniedException(
                    "No plan assigned to this vendor"
            );
        }

        boolean allowed =
                planFeatureService.hasFeature(
                        planId,
                        featureCode
                );

        if (!allowed) {
            throw new AccessDeniedException(
                    "Feature '" + featureCode +
                            "' is not available in your plan"
            );
        }

        return joinPoint.proceed();
    }

}