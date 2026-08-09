package com.e_commerce.eCommerce.dto;

import lombok.*;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserReviewResponseDTO {
    private String reviewId;
    private String customerName;
    private boolean isVerified;
    private Integer rating;
    private String comment;

}
