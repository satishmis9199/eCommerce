package com.e_commerce.eCommerce.config;

import com.e_commerce.eCommerce.entity.Vendor;
import com.e_commerce.eCommerce.service.TenantService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class TenantFilter extends OncePerRequestFilter {

    private final TenantService tenantService;
    private static final Logger logger= LoggerFactory.getLogger(TenantFilter.class);

    public TenantFilter(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @Value("${app.super-admin-domain}")
    private String superAdminDomain;
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String host = request.getServerName();

            if (superAdminDomain.equalsIgnoreCase(host)) {
                TenantContext.setTenantId("0");

                filterChain.doFilter(request, response);
                return;
            }



            Vendor vendor = tenantService.resolveTenant(host);

            String accept = request.getHeader("Accept");
            String requestedWith = request.getHeader("X-Requested-With");

            boolean isApiRequest =
                    request.getRequestURI().startsWith("/api")
                            || "XMLHttpRequest".equals(requestedWith)
                            || (accept != null && accept.contains("application/json"));

            if (vendor == null) {
//                logger.error("Vendor Is Null");

                if (isApiRequest) {

                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.setContentType("application/json");

                    response.getWriter().write("""
        {
            "success": false,
            "message": "Tenant not found"
        }
        """);

                } else {

                    response.sendRedirect("/s4/v1/tenantnot-found");
                }

                return;
            }


            TenantContext.setTenant(vendor);

            String uri = request.getRequestURI();

            if (TenantContext.getTenantId() != null
                    && uri.contains("/super")) {

                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            filterChain.doFilter(request, response);



        }
        catch(Exception e){
            e.printStackTrace();

            logger.info("Error while Finding a Tenant id",e.getMessage());
//            response.sendRedirect("/tenant-not-found");

                logger.info("Error while resolving tenant", e);

                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.setContentType("application/json");
                response.getWriter().write("""
    {
        "success": false,
        "message": "Internal Server Error"
    }
    """);
            response.sendRedirect("/s4/v1/tenantnot-found");
                return;



        }
        finally {


            TenantContext.clear();

        }
    }
}