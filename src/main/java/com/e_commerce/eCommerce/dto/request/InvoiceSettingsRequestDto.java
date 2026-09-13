package com.e_commerce.eCommerce.dto.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class InvoiceSettingsRequestDto {

    private String invoicePrefix;

    private String invoiceFooter;

    private boolean showGst;

    private boolean showBankDetails;

}