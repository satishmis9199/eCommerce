package com.e_commerce.eCommerce.dto;

public class OnboardingDataDto {

    private BasicInfoDto basic;
    private BusinessInfoDto business;
    private AddressInfoDto address;
    private BankInfoDto bank;
    private BrandingInfoDto branding;

    public BasicInfoDto getBasic() {
        return basic;
    }

    public void setBasic(BasicInfoDto basic) {
        this.basic = basic;
    }

    public BusinessInfoDto getBusiness() {
        return business;
    }

    public void setBusiness(BusinessInfoDto business) {
        this.business = business;
    }

    public AddressInfoDto getAddress() {
        return address;
    }

    public void setAddress(AddressInfoDto address) {
        this.address = address;
    }

    public BankInfoDto getBank() {
        return bank;
    }

    public void setBank(BankInfoDto bank) {
        this.bank = bank;
    }

    public BrandingInfoDto getBranding() {
        return branding;
    }

    public void setBranding(BrandingInfoDto branding) {
        this.branding = branding;
    }
}