package com.e_commerce.eCommerce.dto.request;

import com.e_commerce.eCommerce.enums.NoticeStatus;
import jakarta.persistence.NamedEntityGraph;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NoticeStatusUpdateDto {

    private Long id;

    private NoticeStatus status;
}
