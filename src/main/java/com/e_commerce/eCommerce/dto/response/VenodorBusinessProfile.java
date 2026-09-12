
package com.e_commerce.eCommerce.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VenodorBusinessProfile {

    private String businessName;
    private String ownerName;
    private String gstNumber;
    private String panNumber;
    private String registrationNumber;
    private String description;


}
