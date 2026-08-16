package com.e_commerce.eCommerce.dto.request;

public class BannerStatusRequestDTO {

    private Long id;
    private Boolean active;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
