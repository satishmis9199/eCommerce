'use strict';

window.MyStoreAPI = {

    async loginSuperAdmin(credentials) {

        const response = await fetch("/api/v1/auth/super-admin/login", {

            method: "POST",

            credentials: "include",

           headers: {
               "Content-Type": "application/json",
               "X-Requested-With": "XMLHttpRequest"
           },

            body: JSON.stringify(credentials)

        });

        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.message);
        }

        return data;

    }

};