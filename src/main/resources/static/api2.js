'use strict';

/* ===========================================================
                    MyStore Enterprise API
=========================================================== */

const MyStoreAPI = (() => {

    const BASE_URL = "";

    const DEFAULT_HEADERS = {
        "Content-Type": "application/json",
        "X-Requested-With": "XMLHttpRequest"
    };

    const TIMEOUT = 15000;

    /* =======================================================
                        HTTP REQUEST
    ======================================================= */

    async function request(url, options = {}) {

        const controller = new AbortController();

        const timeout = setTimeout(() => {

            controller.abort();

        }, TIMEOUT);

        try {

            const response = await fetch(BASE_URL + url, {

                credentials: "include",

                signal: controller.signal,

                headers: {
                    ...DEFAULT_HEADERS,
                    ...(options.headers || {})
                },

                ...options

            });

            clearTimeout(timeout);

            const data = await response.json();

            if (!response.ok) {

                if (response.status === 401) {

                    window.location.href = "/super/admin";

                    return;

                }

                throw new Error(data.message || "Request Failed");

            }

            return data;

        }

        catch (error) {

            clearTimeout(timeout);

            throw error;

        }

    }

    /* =======================================================
                        DASHBOARD
    ======================================================= */

    async function getDashboard() {

        return request("/api/v1/super/dashboard");

    }

    /* =======================================================
                        VENDORS
    ======================================================= */

    async function getVendors(page = 0) {

        return request(`/api/v1/super/vendors?page=${page}`);

    }

    async function getVendor(id) {

        return request(`/api/v1/super/vendors/${id}`);

    }

    async function approveVendor(id) {

        return request(`/api/v1/super/vendors/${id}/approve`, {

            method: "POST"

        });

    }

    async function rejectVendor(id) {

        return request(`/api/v1/super/vendors/${id}/reject`, {

            method: "POST"

        });

    }

    async function blockVendor(id) {

        return request(`/api/v1/super/vendors/${id}/block`, {

            method: "POST"

        });

    }

    async function suspendVendor(id) {

        return request(`/api/v1/super/vendors/${id}/suspend`, {

            method: "POST"

        });

    }

    /* =======================================================
                        ANALYTICS
    ======================================================= */

    async function getRevenue() {

        return request("/api/v1/super/revenue");

    }

    async function getAnalytics() {

        return request("/api/v1/super/analytics");

    }

    async function getPlatformHealth() {

        return request("/api/v1/super/platform-health");

    }

    /* =======================================================
                        NOTIFICATIONS
    ======================================================= */

    async function getNotifications() {

        return request("/api/v1/super/notifications");

    }

    async function markNotificationRead(id) {

        return request(`/api/v1/super/notifications/${id}`, {

            method: "PATCH"

        });

    }

    /* =======================================================
                        PROFILE
    ======================================================= */

    async function getProfile() {

        return request("/api/v1/super/profile");

    }

    async function updateProfile(payload) {

        return request("/api/v1/super/profile", {

            method: "PUT",

            body: JSON.stringify(payload)

        });

    }

    async function changePassword(payload) {

        return request("/api/v1/super/change-password", {

            method: "PUT",

            body: JSON.stringify(payload)

        });

    }

    /* =======================================================
                        SETTINGS
    ======================================================= */

    async function getSettings() {

        return request("/api/v1/super/settings");

    }

    async function updateSettings(payload) {

        return request("/api/v1/super/settings", {

            method: "PUT",

            body: JSON.stringify(payload)

        });

    }

    /* =======================================================
                        LOGOUT
    ======================================================= */

    async function logout() {

        return request("/api/v1/auth/logout", {

            method: "POST"

        });

    }

    return {

        getDashboard,

        getVendors,

        getVendor,

        approveVendor,

        rejectVendor,

        blockVendor,

        suspendVendor,

        getRevenue,

        getAnalytics,

        getPlatformHealth,

        getNotifications,

        markNotificationRead,

        getProfile,

        updateProfile,

        changePassword,

        getSettings,

        updateSettings,

        logout

    };

})();

window.MyStoreAPI = MyStoreAPI;