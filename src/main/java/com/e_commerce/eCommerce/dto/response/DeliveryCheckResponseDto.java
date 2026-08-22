package com.e_commerce.eCommerce.dto.response;

public class DeliveryCheckResponseDto {

    private boolean deliverable;
    private double distanceInKm;
    private String message;

    public DeliveryCheckResponseDto() {
    }

    public DeliveryCheckResponseDto(boolean deliverable, double distanceInKm, String message) {
        this.deliverable = deliverable;
        this.distanceInKm = distanceInKm;
        this.message = message;
    }

    public boolean isDeliverable() {
        return deliverable;
    }

    public void setDeliverable(boolean deliverable) {
        this.deliverable = deliverable;
    }

    public double getDistanceInKm() {
        return distanceInKm;
    }

    public void setDistanceInKm(double distanceInKm) {
        this.distanceInKm = distanceInKm;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}