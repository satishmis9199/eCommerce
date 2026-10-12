package com.e_commerce.eCommerce.dto.response;

import lombok.Builder;

@Builder
public class UserProfileUpdateResponse {

    private UserResponse user;

    public UserProfileUpdateResponse() {
    }

    public UserProfileUpdateResponse(UserResponse user) {
        this.user = user;
    }

    public UserResponse getUser() {
        return user;
    }

    public void setUser(UserResponse user) {
        this.user = user;
    }

    public static class UserResponse {

        private Long id;
        private String firstName;
        private String lastName;
        private String email;
        private String mobileNumber;

        public UserResponse() {
        }

        public UserResponse(Long id, String firstName, String lastName,
                            String email, String mobileNumber) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.mobileNumber = mobileNumber;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getMobileNumber() {
            return mobileNumber;
        }

        public void setMobileNumber(String mobileNumber) {
            this.mobileNumber = mobileNumber;
        }
    }
}