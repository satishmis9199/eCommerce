package com.e_commerce.eCommerce.dto;

public class OnBoardingDecisionDto {
    private Long applicationId;
    private String action;
    private String remarks;
    private boolean allowResubmit;

    public boolean isAllowResubmit() {
        return allowResubmit;
    }

    public void setAllowResubmit(boolean allowResubmit) {
        this.allowResubmit = allowResubmit;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
