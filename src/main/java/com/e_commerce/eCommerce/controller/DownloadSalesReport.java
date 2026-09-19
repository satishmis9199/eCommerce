package com.e_commerce.eCommerce.controller;

import com.e_commerce.eCommerce.config.TenantContext;
import com.e_commerce.eCommerce.dto.SalesReportProjection;
import com.e_commerce.eCommerce.entity.Vendor;
import com.e_commerce.eCommerce.repository.OrderItemRepository;
import com.e_commerce.eCommerce.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/vendor/v1/reports")
@RequiredArgsConstructor

public class DownloadSalesReport {

    private final OrderItemRepository orderItemRepository;
    private final VendorRepository vendorRepository;


    @GetMapping("/sales")
    public ResponseEntity<byte[]> downloadSalesReport(

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate
    ) throws IOException {
        String tenantId = TenantContext.getTenantId();

        if (tenantId == null || tenantId.isBlank()) {
            throw new IllegalStateException("Tenant not found");
        }
        LocalDate actualStartDate =
                startDate != null
                        ? startDate
                        : LocalDate.now().withDayOfMonth(1);

        LocalDate actualEndDate =
                endDate != null
                        ? endDate
                        : LocalDate.now();


        if (actualStartDate.isAfter(actualEndDate)) {
            throw new IllegalArgumentException(
                    "Start date cannot be greater than end date"
            );
        }


        LocalDateTime startDateTime =
                actualStartDate.atStartOfDay();
        LocalDateTime endDateTime =
                actualEndDate
                        .plusDays(1)
                        .atStartOfDay();
        Vendor vendor = vendorRepository
                .findByTenantId(tenantId)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Vendor not found for tenant: " + tenantId
                        )
                );

        String businessName = vendor.getBussinessName();

        if (businessName == null || businessName.isBlank()) {
            businessName = "Business Sales Report";
        }

        List<SalesReportProjection> report =
                orderItemRepository.getSalesReport(
                        tenantId,
                        startDateTime,
                        endDateTime
                );

        byte[] excelFile =
                createExcelReport(
                        report,
                        businessName,
                        actualStartDate,
                        actualEndDate
                );

        String fileName =
                vendor.getBussinessName()+"_Sales_Report_"
                        + actualStartDate
                        + "_to_"
                        + actualEndDate
                        + ".xlsx";
        return ResponseEntity.ok()
                .contentType(
                        MediaType.parseMediaType(
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                        )
                )
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileName + "\""
                )
                .body(excelFile);
    }
    private byte[] createExcelReport(
            List<SalesReportProjection> report,
            String businessName,
            LocalDate startDate,
            LocalDate endDate
    ) throws IOException {


        try (Workbook workbook = new XSSFWorkbook()) {
            CellStyle businessStyle =
                    createBusinessStyle(workbook);

            CellStyle titleStyle =
                    createTitleStyle(workbook);

            CellStyle subTitleStyle =
                    createSubTitleStyle(workbook);

            CellStyle headerStyle =
                    createHeaderStyle(workbook);

            CellStyle dataStyle =
                    createDataStyle(workbook);

            CellStyle numberStyle =
                    createNumberStyle(workbook);

            CellStyle currencyStyle =
                    createCurrencyStyle(workbook);

            CellStyle totalStyle =
                    createTotalStyle(workbook);

            CellStyle sectionStyle =
                    createSectionStyle(workbook);
            Sheet summarySheet =
                    workbook.createSheet(businessName+" Sales Summary");


            Row businessRow =
                    summarySheet.createRow(0);

            Cell businessCell =
                    businessRow.createCell(0);

            businessCell.setCellValue(
                    businessName
            );

            businessCell.setCellStyle(
                    businessStyle
            );

            summarySheet.addMergedRegion(
                    new CellRangeAddress(
                            0,
                            0,
                            0,
                            5
                    )
            );

            Row titleRow =
                    summarySheet.createRow(1);

            Cell titleCell =
                    titleRow.createCell(0);

            titleCell.setCellValue(
                    "SALES REPORT"
            );

            titleCell.setCellStyle(
                    titleStyle
            );

            summarySheet.addMergedRegion(
                    new CellRangeAddress(
                            1,
                            1,
                            0,
                            5
                    )
            );
            Row periodRow =
                    summarySheet.createRow(2);

            Cell periodCell =
                    periodRow.createCell(0);

            periodCell.setCellValue(
                    "Period: "
                            + formatDate(startDate)
                            + " to "
                            + formatDate(endDate)
            );

            periodCell.setCellStyle(
                    subTitleStyle
            );

            summarySheet.addMergedRegion(
                    new CellRangeAddress(
                            2,
                            2,
                            0,
                            5
                    )
            );

            long totalOrders =
                    report.stream()
                            .map(SalesReportProjection::getOrderNumber)
                            .filter(Objects::nonNull)
                            .distinct()
                            .count();


            long totalItems =
                    report.size();


            long totalQuantity =
                    report.stream()
                            .map(SalesReportProjection::getQuantity)
                            .filter(Objects::nonNull)
                            .mapToLong(Integer::longValue)
                            .sum();


            BigDecimal totalMRP =
                    report.stream()
                            .map(SalesReportProjection::getMrp)
                            .filter(Objects::nonNull)
                            .reduce(
                                    BigDecimal.ZERO,
                                    BigDecimal::add
                            );


            BigDecimal totalSales =
                    report.stream()
                            .map(SalesReportProjection::getLineTotal)
                            .filter(Objects::nonNull)
                            .reduce(
                                    BigDecimal.ZERO,
                                    BigDecimal::add
                            );
            Row summaryHeader =
                    summarySheet.createRow(4);

            createCell(
                    summaryHeader,
                    0,
                    "Metric",
                    headerStyle
            );

            createCell(
                    summaryHeader,
                    1,
                    "Value",
                    headerStyle
            );


            createSummaryRow(
                    summarySheet,
                    5,
                    "Total Orders",
                    totalOrders,
                    dataStyle
            );

            createSummaryRow(
                    summarySheet,
                    6,
                    "Total Items",
                    totalItems,
                    dataStyle
            );

            createSummaryRow(
                    summarySheet,
                    7,
                    "Total Quantity Sold",
                    totalQuantity,
                    numberStyle
            );

            createSummaryRow(
                    summarySheet,
                    8,
                    "Total MRP Value",
                    totalMRP,
                    currencyStyle
            );

            createSummaryRow(
                    summarySheet,
                    9,
                    "Total Sales",
                    totalSales,
                    currencyStyle
            );
            int paymentStartRow = 12;

            Row paymentTitle =
                    summarySheet.createRow(
                            paymentStartRow
                    );

            createCell(
                    paymentTitle,
                    0,
                    "Payment Method Breakdown",
                    sectionStyle
            );

            summarySheet.addMergedRegion(
                    new CellRangeAddress(
                            paymentStartRow,
                            paymentStartRow,
                            0,
                            2
                    )
            );


            Row paymentHeader =
                    summarySheet.createRow(
                            paymentStartRow + 1
                    );

            createCell(
                    paymentHeader,
                    0,
                    "Payment Method",
                    headerStyle
            );

            createCell(
                    paymentHeader,
                    1,
                    "Orders",
                    headerStyle
            );

            createCell(
                    paymentHeader,
                    2,
                    "Sales",
                    headerStyle
            );


            Map<String, List<SalesReportProjection>>
                    paymentGroups =
                    report.stream()
                            .filter(x ->
                                    x.getPaymentMethod() != null
                            )
                            .collect(
                                    Collectors.groupingBy(
                                            SalesReportProjection
                                                    ::getPaymentMethod
                                    )
                            );


            int paymentRow =
                    paymentStartRow + 2;


            for (Map.Entry<String,
                    List<SalesReportProjection>> entry
                    : paymentGroups.entrySet()) {

                String paymentMethod =
                        entry.getKey();

                List<SalesReportProjection> items =
                        entry.getValue();


                long orders =
                        items.stream()
                                .map(
                                        SalesReportProjection
                                                ::getOrderNumber
                                )
                                .filter(Objects::nonNull)
                                .distinct()
                                .count();


                BigDecimal sales =
                        items.stream()
                                .map(
                                        SalesReportProjection
                                                ::getLineTotal
                                )
                                .filter(Objects::nonNull)
                                .reduce(
                                        BigDecimal.ZERO,
                                        BigDecimal::add
                                );


                Row row =
                        summarySheet.createRow(
                                paymentRow++
                        );


                createCell(
                        row,
                        0,
                        paymentMethod,
                        dataStyle
                );

                createCell(
                        row,
                        1,
                        orders,
                        numberStyle
                );

                createCell(
                        row,
                        2,
                        sales,
                        currencyStyle
                );
            }
            int statusStartRow =
                    paymentRow + 2;


            Row statusTitle =
                    summarySheet.createRow(
                            statusStartRow
                    );

            createCell(
                    statusTitle,
                    0,
                    "Order Status Breakdown",
                    sectionStyle
            );

            summarySheet.addMergedRegion(
                    new CellRangeAddress(
                            statusStartRow,
                            statusStartRow,
                            0,
                            2
                    )
            );


            Row statusHeader =
                    summarySheet.createRow(
                            statusStartRow + 1
                    );

            createCell(
                    statusHeader,
                    0,
                    "Order Status",
                    headerStyle
            );

            createCell(
                    statusHeader,
                    1,
                    "Orders",
                    headerStyle
            );

            createCell(
                    statusHeader,
                    2,
                    "Sales",
                    headerStyle
            );


            Map<String, List<SalesReportProjection>>
                    statusGroups =
                    report.stream()
                            .filter(x ->
                                    x.getOrderStatus() != null
                            )
                            .collect(
                                    Collectors.groupingBy(
                                            SalesReportProjection
                                                    ::getOrderStatus
                                    )
                            );


            int statusRow =
                    statusStartRow + 2;


            for (Map.Entry<String,
                    List<SalesReportProjection>> entry
                    : statusGroups.entrySet()) {


                String status =
                        entry.getKey();

                List<SalesReportProjection> items =
                        entry.getValue();


                long orders =
                        items.stream()
                                .map(
                                        SalesReportProjection
                                                ::getOrderNumber
                                )
                                .filter(Objects::nonNull)
                                .distinct()
                                .count();


                BigDecimal sales =
                        items.stream()
                                .map(
                                        SalesReportProjection
                                                ::getLineTotal
                                )
                                .filter(Objects::nonNull)
                                .reduce(
                                        BigDecimal.ZERO,
                                        BigDecimal::add
                                );


                Row row =
                        summarySheet.createRow(
                                statusRow++
                        );


                createCell(
                        row,
                        0,
                        status,
                        dataStyle
                );

                createCell(
                        row,
                        1,
                        orders,
                        numberStyle
                );

                createCell(
                        row,
                        2,
                        sales,
                        currencyStyle
                );
            }


            // Summary widths
            summarySheet.setColumnWidth(
                    0,
                    28 * 256
            );

            summarySheet.setColumnWidth(
                    1,
                    20 * 256
            );

            summarySheet.setColumnWidth(
                    2,
                    20 * 256
            );

            summarySheet.createFreezePane(
                    0,
                    4
            );
            Sheet detailSheet =
                    workbook.createSheet(
                            "Order Details"
                    );
            Row detailBusinessRow =
                    detailSheet.createRow(0);

            Cell detailBusinessCell =
                    detailBusinessRow.createCell(0);

            detailBusinessCell.setCellValue(
                    businessName
            );

            detailBusinessCell.setCellStyle(
                    businessStyle
            );


            detailSheet.addMergedRegion(
                    new CellRangeAddress(
                            0,
                            0,
                            0,
                            13
                    )
            );


            // Title
            Row detailTitleRow =
                    detailSheet.createRow(1);

            Cell detailTitleCell =
                    detailTitleRow.createCell(0);

            detailTitleCell.setCellValue(
                    "ORDER DETAILS"
            );

            detailTitleCell.setCellStyle(
                    titleStyle
            );


            detailSheet.addMergedRegion(
                    new CellRangeAddress(
                            1,
                            1,
                            0,
                            13
                    )
            );


            // Period
            Row detailPeriodRow =
                    detailSheet.createRow(2);

            Cell detailPeriodCell =
                    detailPeriodRow.createCell(0);

            detailPeriodCell.setCellValue(
                    "Period: "
                            + formatDate(startDate)
                            + " to "
                            + formatDate(endDate)
            );

            detailPeriodCell.setCellStyle(
                    subTitleStyle
            );


            detailSheet.addMergedRegion(
                    new CellRangeAddress(
                            2,
                            2,
                            0,
                            13
                    )
            );
            Row headerRow =
                    detailSheet.createRow(4);


            String[] headers = {

                    "Order Number",
                    "Order Date",
                    "Customer ID",
                    "Customer Name",
                    "Product ID",
                    "Product Name",
                    "Brand",
                    "Quantity",
                    "MRP",
                    "Unit Price",
                    "Line Total",
                    "Payment Method",
                    "Payment Status",
                    "Order Status"
            };


            for (int i = 0;
                 i < headers.length;
                 i++) {

                createCell(
                        headerRow,
                        i,
                        headers[i],
                        headerStyle
                );
            }
            int rowIndex = 5;

            DateTimeFormatter dateFormatter =
                    DateTimeFormatter.ofPattern(
                            "dd-MM-yyyy HH:mm"
                    );


            for (SalesReportProjection item
                    : report) {


                Row row =
                        detailSheet.createRow(
                                rowIndex++
                        );


                int col = 0;


                createCell(
                        row,
                        col++,
                        item.getOrderNumber(),
                        dataStyle
                );


                createCell(
                        row,
                        col++,
                        item.getOrderDate() != null
                                ? item.getOrderDate()
                                .format(dateFormatter)
                                : "",
                        dataStyle
                );


                createCell(
                        row,
                        col++,
                        item.getCustomerId(),
                        numberStyle
                );


                createCell(
                        row,
                        col++,
                        item.getCustomerName(),
                        dataStyle
                );


                createCell(
                        row,
                        col++,
                        item.getProductId(),
                        numberStyle
                );


                createCell(
                        row,
                        col++,
                        item.getProductName(),
                        dataStyle
                );


                createCell(
                        row,
                        col++,
                        item.getBrandName(),
                        dataStyle
                );


                createCell(
                        row,
                        col++,
                        item.getQuantity(),
                        numberStyle
                );


                createCell(
                        row,
                        col++,
                        item.getMrp(),
                        currencyStyle
                );


                createCell(
                        row,
                        col++,
                        item.getUnitPrice(),
                        currencyStyle
                );


                createCell(
                        row,
                        col++,
                        item.getLineTotal(),
                        currencyStyle
                );


                createCell(
                        row,
                        col++,
                        item.getPaymentMethod(),
                        dataStyle
                );


                createCell(
                        row,
                        col++,
                        item.getPaymentStatus(),
                        dataStyle
                );


                createCell(
                        row,
                        col++,
                        item.getOrderStatus(),
                        dataStyle
                );
            }
            for (int i = 0;
                 i < headers.length;
                 i++) {

                detailSheet.autoSizeColumn(i);
                if (detailSheet.getColumnWidth(i)
                        > 40 * 256) {

                    detailSheet.setColumnWidth(
                            i,
                            40 * 256
                    );
                }
            }



            detailSheet.createFreezePane(
                    0,
                    5
            );

            ByteArrayOutputStream outputStream =
                    new ByteArrayOutputStream();


            workbook.write(outputStream);


            return outputStream.toByteArray();
        }
    }
    private CellStyle createBusinessStyle(
            Workbook workbook
    ) {

        CellStyle style =
                workbook.createCellStyle();

        Font font =
                workbook.createFont();

        font.setBold(true);
        font.setFontHeightInPoints(
                (short) 18
        );

        style.setFont(font);

        style.setAlignment(
                HorizontalAlignment.CENTER
        );

        style.setVerticalAlignment(
                VerticalAlignment.CENTER
        );

        return style;
    }


    private CellStyle createTitleStyle(
            Workbook workbook
    ) {

        CellStyle style =
                workbook.createCellStyle();

        Font font =
                workbook.createFont();

        font.setBold(true);
        font.setFontHeightInPoints(
                (short) 14
        );

        style.setFont(font);

        style.setAlignment(
                HorizontalAlignment.CENTER
        );

        return style;
    }


    private CellStyle createSubTitleStyle(
            Workbook workbook
    ) {

        CellStyle style =
                workbook.createCellStyle();

        Font font =
                workbook.createFont();

        font.setItalic(true);

        style.setFont(font);

        style.setAlignment(
                HorizontalAlignment.CENTER
        );

        return style;
    }


    private CellStyle createHeaderStyle(
            Workbook workbook
    ) {

        CellStyle style =
                workbook.createCellStyle();

        Font font =
                workbook.createFont();

        font.setBold(true);

        style.setFont(font);

        style.setAlignment(
                HorizontalAlignment.CENTER
        );

        style.setVerticalAlignment(
                VerticalAlignment.CENTER
        );

        addBorders(style);

        return style;
    }


    private CellStyle createDataStyle(
            Workbook workbook
    ) {

        CellStyle style =
                workbook.createCellStyle();

        addBorders(style);

        return style;
    }


    private CellStyle createNumberStyle(
            Workbook workbook
    ) {

        CellStyle style =
                createDataStyle(workbook);

        style.setDataFormat(
                workbook
                        .createDataFormat()
                        .getFormat("#,##0")
        );

        return style;
    }


    private CellStyle createCurrencyStyle(
            Workbook workbook
    ) {

        CellStyle style =
                createDataStyle(workbook);

        style.setDataFormat(
                workbook
                        .createDataFormat()
                        .getFormat(
                                "₹#,##0.00"
                        )
        );

        return style;
    }


    private CellStyle createTotalStyle(
            Workbook workbook
    ) {

        CellStyle style =
                createCurrencyStyle(workbook);

        Font font =
                workbook.createFont();

        font.setBold(true);

        style.setFont(font);

        return style;
    }


    private CellStyle createSectionStyle(
            Workbook workbook
    ) {

        CellStyle style =
                workbook.createCellStyle();

        Font font =
                workbook.createFont();

        font.setBold(true);
        font.setFontHeightInPoints(
                (short) 12
        );

        style.setFont(font);

        return style;
    }


    private void addBorders(
            CellStyle style
    ) {

        style.setBorderTop(
                BorderStyle.THIN
        );

        style.setBorderBottom(
                BorderStyle.THIN
        );

        style.setBorderLeft(
                BorderStyle.THIN
        );

        style.setBorderRight(
                BorderStyle.THIN
        );
    }

    private void createCell(
            Row row,
            int column,
            Object value,
            CellStyle style
    ) {

        Cell cell =
                row.createCell(column);


        if (value == null) {

            cell.setCellValue("");

        } else if (value instanceof Number number) {

            cell.setCellValue(
                    number.doubleValue()
            );

        } else if (value instanceof BigDecimal decimal) {

            cell.setCellValue(
                    decimal.doubleValue()
            );

        } else {

            cell.setCellValue(
                    value.toString()
            );
        }


        cell.setCellStyle(style);
    }


    private void createSummaryRow(
            Sheet sheet,
            int rowNumber,
            String metric,
            Object value,
            CellStyle style
    ) {

        Row row =
                sheet.createRow(rowNumber);


        createCell(
                row,
                0,
                metric,
                style
        );


        createCell(
                row,
                1,
                value,
                style
        );
    }


    private String formatDate(
            LocalDate date
    ) {

        return date.format(
                DateTimeFormatter.ofPattern(
                        "dd-MM-yyyy"
                )
        );
    }
}