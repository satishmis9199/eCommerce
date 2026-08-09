package com.e_commerce.eCommerce.dto;

import lombok.*;
import software.amazon.awssdk.annotations.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MoveAndDeleteDto {


    private Long oldCategoryId;


    private Long newCategoryId;

}