'use strict';

/* ==========================================================
                    MyStore Super Admin
                    dashboard.js
========================================================== */

const Dashboard = {

    init() {

        this.cacheDOM();
        this.bindEvents();
        this.loadDashboard();

    },

    cacheDOM() {

        this.sidebar = document.querySelector(".sidebar");

        this.menuBtn = document.querySelector(".menu-btn");

        this.themeBtn = document.querySelector(".theme-btn");

        this.profile = document.querySelector(".profile");

        this.profileDropdown =
            document.querySelector(".profile-dropdown");

        this.notificationBtn =
            document.querySelector(".notification-btn");

        this.notificationDrawer =
            document.querySelector(".notification-drawer");

        this.closeNotification =
            document.querySelector(".close-btn");

        this.loader =
            document.querySelector("#loader");

        this.toastContainer =
            document.querySelector("#toastContainer");

    },

    bindEvents() {

        this.menuBtn?.addEventListener("click", () => {

            this.toggleSidebar();

        });

        this.themeBtn?.addEventListener("click", () => {

            this.toggleTheme();

        });

        this.profile?.addEventListener("click", () => {

            this.profileDropdown.classList.toggle("show");

        });

        this.notificationBtn?.addEventListener("click", () => {

            this.notificationDrawer.classList.toggle("open");

        });

        this.closeNotification?.addEventListener("click", () => {

            this.notificationDrawer.classList.remove("open");

        });

        window.addEventListener("click", (e) => {

            if (!e.target.closest(".profile")) {

                this.profileDropdown.classList.remove("show");

            }

        });

    },

    toggleSidebar() {

        this.sidebar.classList.toggle("collapsed");

    },

    toggleTheme() {

        document.body.classList.toggle("dark");

        const mode = document.body.classList.contains("dark")
            ? "dark"
            : "light";

        localStorage.setItem("theme", mode);

    },

    loadTheme() {

        const mode = localStorage.getItem("theme");

        if (mode === "dark") {

            document.body.classList.add("dark");

        }

    },

    showLoader() {

        this.loader.style.display = "flex";

    },

    hideLoader() {

        this.loader.style.display = "none";

    },

    toast(message, type = "success") {

        const toast = document.createElement("div");

        toast.className = `toast ${type}`;

        toast.innerHTML = `
            <span>${message}</span>
        `;

        this.toastContainer.appendChild(toast);

        setTimeout(() => {

            toast.remove();

        }, 3000);

    },

    async loadDashboard() {

        try {

            this.showLoader();

            const response =
                await MyStoreAPI.getDashboard();

            console.log(response);

            this.toast("Dashboard Loaded");

        }

        catch (error) {

            console.error(error);

            this.toast(error.message, "error");

        }

        finally {

            this.hideLoader();

        }

    },

    async logout() {

        try {

            await MyStoreAPI.logout();

            window.location.href =
                "/super/admin";

        }

        catch (e) {

            this.toast("Logout Failed", "error");

        }

    }

};

document.addEventListener(

    "DOMContentLoaded",

    () => {

        Dashboard.loadTheme();

        Dashboard.init();

    }

);