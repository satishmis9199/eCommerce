package com.e_commerce.eCommerce.config;

import com.e_commerce.eCommerce.CustomAnnotation.AuditLogs;
import com.e_commerce.eCommerce.CustomAnnotation.RequiresFeature;

import com.e_commerce.eCommerce.service.AuditLogService;
import com.e_commerce.eCommerce.service.CustomUserDetail;
import com.e_commerce.eCommerce.service.PlanFeatureService;
import com.e_commerce.eCommerce.service.VendorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;

import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


@Slf4j
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

    @Around("@annotation(auditLogs)")
//    @Around("execution(* com.e_commerce.eCommerce.controller..*(..))")
    public Object createAuditLogData(ProceedingJoinPoint proceedingJoinPoint, AuditLogs auditLogs) {
        try {
            Long userId = 0L;
            String logAction = auditLogs.value();
            Long currentTime = System.currentTimeMillis();
            Authentication authentication =
                    SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null &&
                    authentication.getPrincipal() instanceof CustomUserDetail userDetail) {

                userId = userDetail.getId();
            }

            String remarks = proceedingJoinPoint.getSignature().getName();
            Object result = proceedingJoinPoint.proceed();
            Long afterExe = System.currentTimeMillis();

            auditLogService.saveAudit(
                    userId,
                    logAction,
                    remarks,
                    afterExe - currentTime

            );

            return result;
        } catch (Throwable e) {
            log.error("Error ::" + e);
            return null;
        }
    }

}