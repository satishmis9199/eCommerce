package com.e_commerce.eCommerce.service;

import com.e_commerce.eCommerce.config.TenantContext;
import com.e_commerce.eCommerce.dto.RowError;
import com.e_commerce.eCommerce.dto.UploadResult;
import com.e_commerce.eCommerce.entity.*;
import com.e_commerce.eCommerce.repository.CategoryRepository;
import com.e_commerce.eCommerce.repository.ProductRepository;
import com.e_commerce.eCommerce.repository.VendorRepository;
import jakarta.transaction.Transactional;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles generation of the bulk-product Excel template and parsing/validation
 * of vendor-uploaded product sheets.
 */
@Service
@RequiredArgsConstructor
public class ProductExcelService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    // ---- Layout constants ----
    private static final int COMPANY_ROW = 0;
    private static final int TITLE_ROW = 1;
    private static final int INSTRUCTION_ROW = 2;
    private static final int HEADER_ROW = 3;
    private static final int DATA_START_ROW = 4;
    private static final int SAMPLE_ROW_COUNT = 3;
    private static final int COLUMN_COUNT = 10;

    private static final int SAMPLE_MARKER_COL = COLUMN_COUNT; // column index 10 (11th column)
    private static final String SAMPLE_MARKER = "SAMPLE_ROW_DO_NOT_EDIT";

    private static final String[] HEADERS = {
            "Category ID", "Product Name", "Description",
            "Selling Price", "MRP", "Stock Quantity", "Unit", "Image URL", "Status", "Featured"
    };

    private static final String[] VALID_UNITS = {
            "BAG", "PIECE", "BOX", "KG", "LITRE", "METER", "DOZEN", "PACK"
    };

    private static final String[] VALID_STATUSES = {"ACTIVE", "INACTIVE"};

    private final DataFormatter formatter = new DataFormatter();
    private final VendorRepository vendorRepository;

    public byte[] generateTemplate(User user) throws Exception {

        Vendor vendor = vendorRepository.findByTenantIdAndId(
                TenantContext.getTenantId(), user.getVendorId());

        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Styles styles = new Styles(workbook);

            XSSFSheet sheet = workbook.createSheet("Products");
            buildProductSheet(workbook, sheet, vendor, styles);

            XSSFSheet instructionsSheet = workbook.createSheet("Instructions");
            buildInstructionsSheet(instructionsSheet, styles, vendor);

            workbook.setActiveSheet(0);
            workbook.write(out);
            return out.toByteArray();
        }
    }

    private void buildProductSheet(XSSFWorkbook workbook, XSSFSheet sheet, Vendor vendor, Styles styles) {

        sheet.setDefaultColumnWidth(22);
        sheet.setColumnWidth(2, 40 * 256);  // Description wider
        sheet.setColumnWidth(7, 35 * 256);  // Image URL wider

        // ---- Store name banner ----
        Row companyRow = sheet.createRow(COMPANY_ROW);
        companyRow.setHeightInPoints(26);
        Cell companyCell = companyRow.createCell(0);
        companyCell.setCellValue(vendor != null ? vendor.getBussinessName() : "Your Store");
        companyCell.setCellStyle(styles.banner);
        for (int i = 1; i < COLUMN_COUNT; i++) {
            companyRow.createCell(i).setCellStyle(styles.banner);
        }
        sheet.addMergedRegion(new CellRangeAddress(COMPANY_ROW, COMPANY_ROW, 0, COLUMN_COUNT - 1));

        // ---- Title ----
        Row titleRow = sheet.createRow(TITLE_ROW);
        titleRow.setHeightInPoints(22);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("Bulk Product Upload Template");
        titleCell.setCellStyle(styles.title);
        for (int i = 1; i < COLUMN_COUNT; i++) {
            titleRow.createCell(i).setCellStyle(styles.title);
        }
        sheet.addMergedRegion(new CellRangeAddress(TITLE_ROW, TITLE_ROW, 0, COLUMN_COUNT - 1));

        // ---- Instruction strip ----
        Row instrRow = sheet.createRow(INSTRUCTION_ROW);
        instrRow.setHeightInPoints(18);
        Cell instrCell = instrRow.createCell(0);
        instrCell.setCellValue("Fill data starting from row " + (DATA_START_ROW + 1)
                + ". Do not change column order. Sample rows below are for reference only "
                + "and are ignored automatically on upload, but you may delete them. "
                + "See 'Instructions' sheet for field rules.");
        instrCell.setCellStyle(styles.instruction);
        for (int i = 1; i < COLUMN_COUNT; i++) {
            instrRow.createCell(i).setCellStyle(styles.instruction);
        }
        sheet.addMergedRegion(new CellRangeAddress(INSTRUCTION_ROW, INSTRUCTION_ROW, 0, COLUMN_COUNT - 1));

        // ---- Header row ----
        Row headerRow = sheet.createRow(HEADER_ROW);
        headerRow.setHeightInPoints(20);
        for (int i = 0; i < HEADERS.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(HEADERS[i]);
            cell.setCellStyle(styles.header);
        }

        Object[][] samples = {
                {1L, "UltraTech Cement", "Premium OPC 53-grade cement, 50kg bag", 420, 450, 250, "BAG", "https://example.com/cement.jpg", "ACTIVE", true},
                {2L, "Ambuja Cement", "PPC blended cement for general construction", 395, 410, 180, "BAG", "https://example.com/ambuja.jpg", "ACTIVE", true},
                {3L, "Tata Steel TMT Bar", "Fe 500D grade, 12mm corrosion resistant", 62, 68, 500, "PIECE", "https://example.com/tmt.jpg", "ACTIVE", true}
        };

        for (int r = 0; r < SAMPLE_ROW_COUNT; r++) {
            Row row = sheet.createRow(DATA_START_ROW + r);
            Object[] data = samples[r];

            row.createCell(0).setCellValue((Long) data[0]);
            row.createCell(1).setCellValue((String) data[1]);
            row.createCell(2).setCellValue((String) data[2]);
            row.createCell(3).setCellValue((Integer) data[3]);
            row.createCell(4).setCellValue((Integer) data[4]);
            row.createCell(5).setCellValue((Integer) data[5]);
            row.createCell(6).setCellValue((String) data[6]);
            row.createCell(7).setCellValue((String) data[7]);
            row.createCell(8).setCellValue((String) data[8]);
            row.createCell(9).setCellValue((Boolean) data[9]);

            for (int c = 0; c < COLUMN_COUNT; c++) {
                Cell cell = row.getCell(c);
                if (c == 3 || c == 4) {
                    cell.setCellStyle(styles.currencySample);
                } else {
                    cell.setCellStyle(styles.sample);
                }
            }

            // Tag this row as a sample so the parser skips it on upload,
            // no matter what the vendor edits it to or where it ends up.
            Cell marker = row.createCell(SAMPLE_MARKER_COL);
            marker.setCellValue(SAMPLE_MARKER);
        }
        int blankRowsEnd = DATA_START_ROW + SAMPLE_ROW_COUNT + 200;
        for (int r = DATA_START_ROW + SAMPLE_ROW_COUNT; r < blankRowsEnd; r++) {
            Row row = sheet.createRow(r);
            for (int c = 0; c < COLUMN_COUNT; c++) {
                Cell cell = row.createCell(c);
                cell.setCellStyle((c == 3 || c == 4) ? styles.currencyInput : styles.input);
            }
        }
        addDropdownValidation(sheet, VALID_UNITS, DATA_START_ROW, blankRowsEnd - 1, 6);
        addDropdownValidation(
                sheet,
                VALID_STATUSES,
                DATA_START_ROW,
                blankRowsEnd - 1,
                8
        );

        addDropdownValidation(
                sheet,
                new String[]{"TRUE", "FALSE"},
                DATA_START_ROW,
                blankRowsEnd - 1,
                9
        );

        // ---- Category ID dropdown, populated from the vendor's existing active categories ----
        // NOTE: assumes CategoryRepository has a method returning full entities (not just IDs)
        // and ProductCategory exposes getCategoryName() — adjust names if your entity differs.
        List<ProductCategory> vendorCategories = categoryRepository.findByTenantIdAndVendorIdAndStatus(
                TenantContext.getTenantId(), vendor != null ? vendor.getId() : null, CategoryStatus.ACTIVE);
        addCategoryDropdown(workbook, sheet, vendorCategories, DATA_START_ROW, blankRowsEnd - 1);

        // ---- Hide the internal sample marker column from the vendor ----
        sheet.setColumnHidden(SAMPLE_MARKER_COL, true);

        // ---- Freeze header rows so they stay visible while scrolling ----
        sheet.createFreezePane(0, DATA_START_ROW);
    }

    private void addDropdownValidation(XSSFSheet sheet, String[] options, int firstRow, int lastRow, int column) {
        XSSFDataValidationHelper helper = new XSSFDataValidationHelper(sheet);
        DataValidationConstraint constraint = helper.createExplicitListConstraint(options);
        CellRangeAddressList addressList = new CellRangeAddressList(firstRow, lastRow, column, column);
        DataValidation validation = helper.createValidation(constraint, addressList);
        validation.setSuppressDropDownArrow(true);
        validation.setShowErrorBox(true);
        validation.createErrorBox("Invalid Value", "Please choose a value from the dropdown list.");
        sheet.addValidationData(validation);
    }

    private void addCategoryDropdown(XSSFWorkbook workbook, XSSFSheet productSheet,
                                     List<ProductCategory> categories, int firstRow, int lastRow) {
        if (categories == null || categories.isEmpty()) {
            return;
        }

        XSSFSheet listSheet = workbook.createSheet("CategoryList");
        for (int i = 0; i < categories.size(); i++) {
            ProductCategory category = categories.get(i);
            String display = category.getId() + " - " + category.getCategoryName();
            listSheet.createRow(i).createCell(0).setCellValue(display);
        }
        workbook.setSheetHidden(workbook.getSheetIndex(listSheet), true);

        String formula = "CategoryList!$A$1:$A$" + categories.size();
        XSSFDataValidationHelper helper = new XSSFDataValidationHelper(productSheet);
        DataValidationConstraint constraint = helper.createFormulaListConstraint(formula);
        CellRangeAddressList addressList = new CellRangeAddressList(firstRow, lastRow, 0, 0);
        DataValidation validation = helper.createValidation(constraint, addressList);
        validation.setSuppressDropDownArrow(true);
        validation.setShowErrorBox(true);
        validation.createErrorBox("Invalid Category", "Please choose a category from the dropdown list.");
        productSheet.addValidationData(validation);
    }

    private void buildInstructionsSheet(XSSFSheet sheet, Styles styles, Vendor vendor) {
        String tenantId = TenantContext.getTenantId();
        sheet.setColumnWidth(0, 22 * 256);
        sheet.setColumnWidth(1, 60 * 256);

        Row header = sheet.createRow(0);
        Cell h1 = header.createCell(0);
        h1.setCellValue("Column");
        h1.setCellStyle(styles.header);
        Cell h2 = header.createCell(1);
        h2.setCellValue("Rules");
        h2.setCellStyle(styles.header);
        List<ProductCategory> categories = categoryRepository.findByTenantIdAndVendorIdAndStatus(
                tenantId, vendor != null ? vendor.getId() : null, CategoryStatus.ACTIVE);
        StringBuilder stringBuildder = new StringBuilder();
        for (ProductCategory category : categories) {
            stringBuildder.append(" ").append(category.getId()).append(" - ").append(category.getCategoryName()).append(" ,");
        }


        String[][] rules = {
                {"Category ID", "Required. Choose from the dropdown - shows \"ID - Category Name\", only your existing active categories are listed."},
                {"Product Name", "Required. Max 150 characters."},
                {"Description", "Optional. Plain text, no formulas."},
                {"Selling Price", "Required. Numeric, must be greater than 0."},
                {"MRP", "Required. Numeric, must be greater than or equal to Selling Price."},
                {"Stock Quantity", "Required. Whole number, 0 or more."},
                {"Unit", "Required. Choose from the dropdown (BAG, PIECE, BOX, KG, LITRE, METER, DOZEN, PACK)."},
                {"Image URL", "Optional. Must be a direct, publicly accessible image link."},
                {"Status", "Required. Choose ACTIVE or INACTIVE from the dropdown."},
                {"Featured", "Required. Choose TRUE or FALSE from the dropdown."},
                {"Sample rows", "The first 3 filled-in rows below the header are examples only. "
                        + "They are ignored automatically when you upload, even if you don't delete them."},
                {"Availble Category", "Please Use Category From This Availble Category IDs" + stringBuildder.toString()}
        };

        for (int i = 0; i < rules.length; i++) {
            Row row = sheet.createRow(i + 1);
            Cell c0 = row.createCell(0);
            c0.setCellValue(rules[i][0]);
            c0.setCellStyle(styles.sample);
            Cell c1 = row.createCell(1);
            c1.setCellValue(rules[i][1]);
            c1.setCellStyle(styles.sample);
        }
    }

    @Transactional
    public UploadResult uploadProducts(MultipartFile file, User user) throws Exception {

        List<ParsedProductRow> validRows = new ArrayList<>();
        List<RowError> errors = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {

            Sheet sheet = workbook.getSheetAt(0);

            for (int i = DATA_START_ROW; i <= sheet.getLastRowNum(); i++) {

                Row row = sheet.getRow(i);
                if (row == null || isRowEmpty(row)) {
                    continue;
                }
                if (isSampleRow(row)) {
                    continue;
                }

                int excelRowNumber = i + 1;
                List<String> rowErrors = new ArrayList<>();

                Long categoryId = parseCategoryId(getCellValue(row, 0), rowErrors);

                ProductCategory category = null;
                if (categoryId != null) {
                    category = categoryRepository
                            .findByIdAndVendorIdAndTenantId(categoryId, user.getVendorId(), TenantContext.getTenantId());
                    if (category == null) {
                        rowErrors.add("Category does not exist. Please Use category from DropDown");
                    }
                }

                String productName = requireText(getCellValue(row, 1), "Product Name", rowErrors);

                if (productName != null && categoryId != null) {
                    boolean exists = productRepository
                            .existsByVendorIdAndCategoryIdAndProductNameIgnoreCase(
                                    user.getVendorId(),
                                    categoryId,
                                    productName);

                    if (exists) {
                        rowErrors.add("Product already exists...Skipped");
                    }
                }

                String description = getCellValue(row, 2);
                BigDecimal sellingPrice = parseDecimal(getCellValue(row, 3), "Selling Price", rowErrors);
                BigDecimal mrp = parseDecimal(getCellValue(row, 4), "MRP", rowErrors);
                Integer stock = parseInt(getCellValue(row, 5), "Stock Quantity", rowErrors);
                String unit = requireText(getCellValue(row, 6), "Unit", rowErrors);
                String image = getCellValue(row, 7);
                String statusText = requireText(getCellValue(row, 8), "Status", rowErrors);
                String featuredText = requireText(getCellValue(row, 9), "Featured", rowErrors);

                if (unit != null && !isValidUnit(unit)) {
                    rowErrors.add("Unit '" + unit + "' is not one of the allowed values.");
                }
                if (sellingPrice != null && sellingPrice.compareTo(BigDecimal.ZERO) <= 0) {
                    rowErrors.add("Selling Price must be greater than 0.");
                }
                if (sellingPrice != null && mrp != null && mrp.compareTo(sellingPrice) < 0) {
                    rowErrors.add("MRP cannot be less than Selling Price.");
                }
                if (stock != null && stock < 0) {
                    rowErrors.add("Stock Quantity cannot be negative.");
                }

                ProductStatus status = null;
                if (statusText != null) {
                    if (isValidStatus(statusText)) {
                        status = ProductStatus.valueOf(statusText.toUpperCase());
                    } else {
                        rowErrors.add("Status '" + statusText + "' must be ACTIVE or INACTIVE.");
                    }
                }

                Boolean featured = null;
                if (featuredText != null) {
                    if ("TRUE".equalsIgnoreCase(featuredText) || "FALSE".equalsIgnoreCase(featuredText)) {
                        featured = Boolean.parseBoolean(featuredText);
                    } else {
                        rowErrors.add("Featured '" + featuredText + "' must be TRUE or FALSE.");
                    }
                }

                if (!rowErrors.isEmpty()) {
                    errors.add(new RowError(excelRowNumber, rowErrors));
                    continue;
                }

                validRows.add(ParsedProductRow.builder()
                        .categoryId(categoryId)
                        .productName(productName)
                        .description(description)
                        .sellingPrice(sellingPrice)
                        .mrp(mrp)
                        .stockQuantity(stock)
                        .unit(unit)
                        .imageUrl(image)
                        .status(status)
                        .featured(featured)
                        .build());
            }
        }

        List<Product> products = new ArrayList<>();
        for (ParsedProductRow r : validRows) {

            Product product = Product.builder()
                    .tenantId(TenantContext.getTenantId())
                    .vendorId(user.getVendorId())
                    .categoryId(r.getCategoryId())
                    .productName(r.getProductName())
                    .description(r.getDescription())
                    .sellingPrice(r.getSellingPrice())
                    .mrp(r.getMrp())
                    .stockQuantity(r.getStockQuantity())
                    .unit(ProductUnit.valueOf(r.getUnit().toUpperCase()))
                    .productImage(r.getImageUrl())
                    .status(r.getStatus())
                    .featured(r.getFeatured())
                    .createdBy(user.getId())      // <-- Add this
                    .updatedBy(user.getId())      // <-- Add this
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            products.add(product);
        }
        productRepository.saveAll(products);

        return UploadResult.builder()
                .totalRowsProcessed(validRows.size() + errors.size())
                .successCount(validRows.size())
                .failureCount(errors.size())
                .rowErrors(errors)
                .build();
    }

    // ---- Parsing helpers ----

    private String getCellValue(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
        if (cell == null) {
            return "";
        }
        return formatter.formatCellValue(cell).trim();
    }

    private boolean isRowEmpty(Row row) {
        for (int i = 0; i < COLUMN_COUNT; i++) {
            if (!getCellValue(row, i).isBlank()) {
                return false;
            }
        }
        return true;
    }

    private boolean isSampleRow(Row row) {
        return SAMPLE_MARKER.equals(getCellValue(row, SAMPLE_MARKER_COL));
    }

    private boolean isValidUnit(String unit) {
        for (String u : VALID_UNITS) {
            if (u.equalsIgnoreCase(unit)) {
                return true;
            }
        }
        return false;
    }

    private boolean isValidStatus(String status) {
        for (String s : VALID_STATUSES) {
            if (s.equalsIgnoreCase(status)) {
                return true;
            }
        }
        return false;
    }

    private Long parseCategoryId(String value, List<String> errors) {
        if (value == null || value.isBlank()) {
            errors.add("Category ID is required.");
            return null;
        }

        String leadingDigits = value.trim().split("\\s*-\\s*", 2)[0].trim();
        try {
            return Long.parseLong(leadingDigits);
        } catch (NumberFormatException e) {
            errors.add("Category ID must be selected from the dropdown.");
            return null;
        }
    }

    private String requireText(String value, String field, List<String> errors) {
        if (value == null || value.isBlank()) {
            errors.add(field + " is required.");
            return null;
        }
        return value;
    }

    private Long parseLong(String value, String field, List<String> errors) {
        if (value == null || value.isBlank()) {
            errors.add(field + " is required.");
            return null;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            errors.add(field + " must be a whole number.");
            return null;
        }
    }

    private Integer parseInt(String value, String field, List<String> errors) {
        if (value == null || value.isBlank()) {
            errors.add(field + " is required.");
            return null;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            errors.add(field + " must be a whole number.");
            return null;
        }
    }

    private BigDecimal parseDecimal(String value, String field, List<String> errors) {
        if (value == null || value.isBlank()) {
            errors.add(field + " is required.");
            return null;
        }
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            errors.add(field + " must be a valid number.");
            return null;
        }
    }

    private static class Styles {

        final CellStyle banner;
        final CellStyle title;
        final CellStyle instruction;
        final CellStyle header;
        final CellStyle sample;
        final CellStyle currencySample;
        final CellStyle input;
        final CellStyle currencyInput;

        Styles(Workbook workbook) {
            banner = bannerStyle(workbook);
            title = titleStyle(workbook);
            instruction = instructionStyle(workbook);
            header = headerStyle(workbook);
            sample = borderedStyle(workbook, IndexedColors.WHITE.getIndex(), false);
            currencySample = currencyStyle(workbook, false);
            input = borderedStyle(workbook, IndexedColors.WHITE.getIndex(), true);
            currencyInput = currencyStyle(workbook, true);
        }

        private CellStyle bannerStyle(Workbook workbook) {
            CellStyle style = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            font.setFontHeightInPoints((short) 14);
            font.setColor(IndexedColors.WHITE.getIndex());
            style.setFont(font);
            style.setFillForegroundColor(IndexedColors.BLACK.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            style.setAlignment(HorizontalAlignment.CENTER);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            return style;
        }

        private CellStyle titleStyle(Workbook workbook) {
            CellStyle style = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            font.setFontHeightInPoints((short) 16);
            font.setColor(IndexedColors.WHITE.getIndex());
            style.setFont(font);
            style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            style.setAlignment(HorizontalAlignment.CENTER);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            return style;
        }

        private CellStyle instructionStyle(Workbook workbook) {
            CellStyle style = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setItalic(true);
            font.setFontHeightInPoints((short) 10);
            font.setColor(IndexedColors.DARK_RED.getIndex());
            style.setFont(font);
            style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            style.setAlignment(HorizontalAlignment.LEFT);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            return style;
        }

        private CellStyle headerStyle(Workbook workbook) {
            CellStyle style = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            font.setColor(IndexedColors.WHITE.getIndex());
            style.setFont(font);
            style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            style.setAlignment(HorizontalAlignment.CENTER);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            applyThinBorder(style);
            return style;
        }

        private CellStyle borderedStyle(Workbook workbook, short fillColor, boolean isInputRow) {
            CellStyle style = workbook.createCellStyle();
            if (isInputRow) {
                style.setFillForegroundColor(IndexedColors.WHITE.getIndex());
                style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            } else {
                style.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
                style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            }
            applyThinBorder(style);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            return style;
        }

        private CellStyle currencyStyle(Workbook workbook, boolean isInputRow) {
            CellStyle style = borderedStyle(workbook, IndexedColors.WHITE.getIndex(), isInputRow);
            style.setDataFormat(workbook.createDataFormat().getFormat("#,##0.00"));
            return style;
        }

        private void applyThinBorder(CellStyle style) {
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
        }
    }

    // =========================================================================
    // DTOs
    // =========================================================================

    @Data
    @Builder
    public static class ParsedProductRow {
        private Long categoryId;
        private String productName;
        private String description;
        private BigDecimal sellingPrice;
        private BigDecimal mrp;
        private Integer stockQuantity;
        private String unit;
        private String imageUrl;
        private ProductStatus status;
        private Boolean featured;
    }
}