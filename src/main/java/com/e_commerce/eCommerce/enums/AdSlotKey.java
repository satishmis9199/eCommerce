package com.e_commerce.eCommerce.enums;


public enum AdSlotKey {

    HOME_LEADERBOARD("Homepage - full width banner below the hero"),
    HOME_MIDFEED_NATIVE("Homepage - native banner between Flash Sale and Featured"),
    HOME_SPONSORED_CARD("Homepage - sponsored card between Best Sellers and Recommended"),
    CART_SIDEBAR("Cart page - rectangle below the order summary"),
    CHECKOUT_STRIP("Checkout page - slim strip below the payment methods"),
    FOOTER_BANNER("All pages - wide banner above the footer");

    private final String description;

    AdSlotKey(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}