package com.e_commerce.eCommerce.constants;

import com.e_commerce.eCommerce.enums.VendorRequestStatus;

public final class GlobalConstants {

    private GlobalConstants() {
    }

    private static final String VENDOR_REQUEST_PENDING =
            "Your vendor request has been submitted successfully.";

    private static final String VENDOR_REQUEST_CONTACTED =
            "Our team has contacted you and your request is being processed.";

    private static final String VENDOR_REQUEST_APPROVED =
            "Your vendor request has been approved. Our team will contact you with the next steps.";

    private static final String VENDOR_REQUEST_REJECTED =
            "Your vendor request was not approved.";

    public static String getVendorRequestStatusMessage(
            VendorRequestStatus status
    ) {
        return switch (status) {

            case PENDING ->
                    VENDOR_REQUEST_PENDING;

            case CONTACTED ->
                    VENDOR_REQUEST_CONTACTED;

            case APPROVED ->
                    VENDOR_REQUEST_APPROVED;

            case REJECTED ->
                    VENDOR_REQUEST_REJECTED;
        };
    }
}