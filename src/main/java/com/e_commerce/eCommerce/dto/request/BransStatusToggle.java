package com.e_commerce.eCommerce.dto.request;

import com.e_commerce.eCommerce.enums.BrandStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.checkerframework.checker.units.qual.A;

@Getter
@Setter
@AllArgsConstructor
public class BransStatusToggle {
    private Long id;
    private String status;
}
