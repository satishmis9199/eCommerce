package com.e_commerce.eCommerce.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankAccountRequestDto {

    private String accountHolderName;

    private String bankName;

    private String accountNumber;

    private String ifscCode;

    private String branchName;

    private String upiId;
}