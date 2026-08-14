package com.e_commerce.eCommerce.repository;

import com.e_commerce.eCommerce.dto.CustomerListResponseDTO;
import com.e_commerce.eCommerce.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface UserRepos extends JpaRepository<User,Long> {
    boolean existsByEmail(String email);

    User findByEmail(String username);

    User findByEmailAndVendorIdAndTenantId(@NotBlank(message = "Email is required") @Email(message = "Invalid email address") @Size(max = 100) String email, Long id, String tenantId);


    List<User> findAllByIdIn(Set<Long> ids);


    List<User> findAllByTenantIdAndVendorId(String tenantid, Long id);

    @Query("""
SELECT new com.e_commerce.eCommerce.dto.CustomerListResponseDTO(
    u.id,
    CONCAT(u.firstName,' ',u.lastName),
    u.email,
    u.phone,
    COUNT(DISTINCT o.id),
    COALESCE(SUM(oi.lineTotal),0),
    MAX(o.createdAt)
)
FROM User u
LEFT JOIN Order o
    ON o.userId = u.id
    AND o.tenantId = :tenantId
    AND o.vendorId = :vendorId
LEFT JOIN OrderItem oi
    ON oi.orderId = o.id
WHERE u.role = com.e_commerce.eCommerce.entity.Roles.USER
AND u.tenantId = :tenantId AND o.orderStatus=
com.e_commerce.eCommerce.entity.OrderStatus.DELIVERED
GROUP BY
    u.id,
    u.firstName,
    u.lastName,
    u.email,
    u.phone
ORDER BY COALESCE(SUM(oi.lineTotal),0) DESC
""")
    List<CustomerListResponseDTO> getCustomerList(
            @Param("tenantId") String tenantId,
            @Param("vendorId") Long vendorId
    );

    User findByMobileNumberAndVendorIdAndTenantId(@NotBlank(message = "Mobile number is required") @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid mobile number") String mobileNumber, Long vendorId, String tenantId);


    User findByTenantIdAndEmail(String tenanTid, String email);
    @Query("""
SELECT u.id, CONCAT(u.firstName,' ',u.lastName)
FROM User u
WHERE u.tenantId = :tenantId
AND u.id IN :userIds
""")
    List<Object[]> findUserNames(
            @Param("tenantId") String tenantId,
            @Param("userIds") List<Long> userIds
    );

    User findByTenantIdAndId(String tenantId, Long userId);

    User findByEmailAndTenantId(String email, String tenantId);
}
