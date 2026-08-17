package com.e_commerce.eCommerce.dto;

import com.e_commerce.eCommerce.entity.VendorStatus;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.LocalDateTime;

@JsonPropertyOrder({
        "vendorId",
        "fullName",
        "email",
        "mobileNumber",
        "status",
        "registrationDate",
        "businessName",
        "storeName",
        "businessType",
        "businessCategory",
        "businessDescription",
        "gstNumber",
        "panNumber"
})

public class VendorDetailsResponseDto {

    // =====================================================
    // Basic Information
    // =====================================================

    private Long vendorId;

    private String fullName;

    private String email;

    private String mobileNumber;

    private VendorStatus status;

    private LocalDateTime registrationDate;

    // =====================================================
    // Business Information
    // =====================================================

    private String businessName;

    private String storeName;

    private String businessType;

    private String businessCategory;

    private String businessDescription;

    private String gstNumber;

    private String panNumber;

    // =====================================================
    // Getters & Setters
    // =====================================================

    public Long getVendorId() {
        return vendorId;
    }

    public void setVendorId(Long vendorId) {
        this.vendorId = vendorId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public VendorStatus getStatus() {
        return status;
    }

    public void setStatus(VendorStatus status) {
        this.status = status;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getBusinessCategory() {
        return businessCategory;
    }

    public void setBusinessCategory(String businessCategory) {
        this.businessCategory = businessCategory;
    }

    public String getBusinessDescription() {
        return businessDescription;
    }

    public void setBusinessDescription(String businessDescription) {
        this.businessDescription = businessDescription;
    }

    public String getGstNumber() {
        return gstNumber;
    }

    public void setGstNumber(String gstNumber) {
        this.gstNumber = gstNumber;
    }

    public String getPanNumber() {
        return panNumber;
    }

    public void setPanNumber(String panNumber) {
        this.panNumber = panNumber;
    }
}