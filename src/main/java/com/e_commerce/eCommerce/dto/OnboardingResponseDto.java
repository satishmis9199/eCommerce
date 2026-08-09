package com.e_commerce.eCommerce.dto;

public class OnboardingResponseDto {

    private boolean success;
    private String message;

    private ApplicationDto application;

    private OnboardingDataDto data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ApplicationDto getApplication() {
        return application;
    }

    public void setApplication(ApplicationDto application) {
        this.application = application;
    }

    public OnboardingDataDto getData() {
        return data;
    }

    public void setData(OnboardingDataDto data) {
        this.data = data;
    }
}