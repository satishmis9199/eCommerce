package com.e_commerce.eCommerce.dto;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserBannerResponseDTo {
//    { id:"b1", eyebrow:"Bulk Pricing", title:"Cement & TMT Steel,\ndirect from plant.",
//            subtitle:"Save up to 18% on bulk orders above 5 tonnes.",
//            ctaLabel:"Shop Cement & Steel",
//            ctaLink:"#", image:IMG.hero1}
    private String id;
    private String eyebrow;
    private String title;
    private String subtitle;
    private String ctaLabel;
    private String ctaLink;
    private String image;
}
