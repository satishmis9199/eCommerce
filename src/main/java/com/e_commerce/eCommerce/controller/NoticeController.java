package com.e_commerce.eCommerce.controller;

import com.e_commerce.eCommerce.dto.ApiResponse;
import com.e_commerce.eCommerce.dto.NoticeListDto;
import com.e_commerce.eCommerce.dto.NoticeResponseDto;
import com.e_commerce.eCommerce.dto.request.NoticeRequestDto;
import com.e_commerce.eCommerce.dto.request.NoticeStatusUpdateDto;
import com.e_commerce.eCommerce.service.CustomUserDetail;
import com.e_commerce.eCommerce.service.NoticeService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @PostMapping(
            value = "/vendor/s11/v1/notice",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> addNotices(
            @RequestBody NoticeRequestDto noticeRequestDto,
            @AuthenticationPrincipal CustomUserDetail userDetail) {

        try {

            String message = noticeService.addNotices(
                    noticeRequestDto,
                    userDetail
            );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(
                            new ApiResponse<>(
                                    true,
                                    "Notice Added Successfully",
                                    message
                            )
                    );

        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(
                            new ApiResponse<>(
                                    false,
                                    e.getMessage(),
                                    null
                            )
                    );
        }
    }

    @GetMapping(
            value = "/vendor/s11/v1/notice",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<NoticeListDto>>> loadAllNotices(
            @AuthenticationPrincipal CustomUserDetail userDetail) {

        try {

            List<NoticeListDto> noticeListDtoList =
                    noticeService.findAllNotices(userDetail);

            return ResponseEntity
                    .ok()
                    .body(
                            new ApiResponse<>(
                                    true,
                                    "Notices Fetched Successfully",
                                    noticeListDtoList
                            )
                    );

        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(
                            new ApiResponse<>(
                                    false,
                                    e.getMessage(),
                                    null
                            )
                    );
        }
    }

    @GetMapping(
            value = "/api/u1/v1/notices/active",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<NoticeResponseDto>> getActiveNotices() {

        List<NoticeResponseDto> notices =
                noticeService.getActiveNotices();

        return ResponseEntity.ok(notices);
    }

    @PutMapping(
            value = "/vendor/s11/v1/notice/{noticeId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> updateNotice(
            @PathVariable Long noticeId,
            @RequestBody NoticeRequestDto noticeRequestDto,
            @AuthenticationPrincipal CustomUserDetail userDetail) {

        try {

            String message = noticeService.updateNotice(
                    noticeId,
                    noticeRequestDto,
                    userDetail
            );

            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,
                            "Notice Updated Successfully",
                            message
                    )
            );

        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(
                            new ApiResponse<>(
                                    false,
                                    e.getMessage(),
                                    null
                            )
                    );
        }
    }

    @DeleteMapping(
            value = "/vendor/s11/v1/notice/{noticeId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteNotice(
            @PathVariable Long noticeId,
            @AuthenticationPrincipal CustomUserDetail userDetail) {

        try {

            String message = noticeService.deleteNotice(
                    noticeId,
                    userDetail
            );

            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,
                            "Notice Deleted Successfully",
                            message
                    )
            );

        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(
                            new ApiResponse<>(
                                    false,
                                    e.getMessage(),
                                    null
                            )
                    );
        }
    }


    @PatchMapping(
            value = "/vendor/s11/v1/notice/status",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> changeNoticeStatus(
            @RequestBody NoticeStatusUpdateDto request,
            @AuthenticationPrincipal CustomUserDetail userDetail) {

        try {

            String message = noticeService.changeNoticeStatus(
                    request.getId(),
                    request.getStatus(),
                    userDetail
            );

            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,
                            "Notice Status Updated Successfully",
                            message
                    )
            );

        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(
                            new ApiResponse<>(
                                    false,
                                    e.getMessage(),
                                    null
                            )
                    );
        }
    }
}