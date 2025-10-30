package com.demo.certificate.generator.utility;

import com.demo.certificate.generator.Configuration;
import com.demo.certificate.generator.entity.CertificatesTable;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ExcelWritter {

    @Autowired
    private Configuration configuration;

    public boolean auditCertificate(CertificatesTable certificate) {
        String auditFile = configuration.getAuditFile();
        try {
            File file = new File(auditFile);
            Workbook workbook;
            Sheet sheet;

            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                workbook = new XSSFWorkbook(fis);
                sheet = workbook.getSheetAt(0);
                fis.close();
            } else {
                workbook = new XSSFWorkbook();
                sheet = workbook.createSheet("Audit");
                createHeaderRow(sheet);
            }

            // Check for duplicate certificate ID
            if (isDuplicate(sheet, certificate.getCertificateId())) {
                System.out.println("❌ Duplicate certificate ID: " + certificate.getCertificateId());
                workbook.close();
                return false;
            }

            // Append new row
            int lastRow = sheet.getLastRowNum() + 1;
            Row row = sheet.createRow(lastRow);

            writeCell(row, 0, certificate.getCertificateId());
            writeCell(row, 1, certificate.getCertificateTitle());
            writeCell(row, 2, certificate.getAuthorName());
            writeCell(row, 3, certificate.getPaperTopic());
            writeCell(row, 4, certificate.getVolumeNo());
            writeCell(row, 5, certificate.getIssueNo());
            writeCell(row, 6, formatDate(certificate.getCertificateDate()));
            writeCell(row, 7, formatDate(certificate.getIssueDate()));
            writeCell(row, 8, certificate.getIssuedBy());

            FileOutputStream fos = new FileOutputStream(file);
            workbook.write(fos);
            workbook.close();
            fos.close();

            System.out.println("✅ Certificate added: " + certificate.getCertificateId());
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void createHeaderRow(Sheet sheet) {
        Row header = sheet.createRow(0);
        writeCell(header, 0, "Certificate ID");
        writeCell(header, 1, "Title");
        writeCell(header, 2, "Author");
        writeCell(header, 3, "Paper");
        writeCell(header, 4, "Volume");
        writeCell(header, 5, "Issue");
        writeCell(header, 6, "Certificate Date");
        writeCell(header, 7, "Issue Date");
        writeCell(header, 8, "Issued By");
    }

    private void writeCell(Row row, int colIndex, String value) {
        Cell cell = row.createCell(colIndex);
        cell.setCellValue(value != null ? value : "");
    }

    private String formatDate(Date date) {
        if (date == null) return "";
        return new SimpleDateFormat("dd/MM/yyyy").format(date);
    }

    private boolean isDuplicate(Sheet sheet, String certificateId) {
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                Cell cell = row.getCell(0); // Column 0 = certificateId
                if (cell != null && certificateId.equals(cell.getStringCellValue())) {
                    return true;
                }
            }
        }
        return false;
    }

    public String validateOrGenerateNumericCertificateId(String givenId) throws RuntimeException {
        String auditFile = configuration.getAuditFile();
        Set<Integer> existingIds = new HashSet<>();

        try {
            File file = new File(auditFile);
            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                Workbook workbook = new XSSFWorkbook(fis);
                Sheet sheet = workbook.getSheetAt(0);

                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row != null) {
                        Cell cell = row.getCell(0); // Column 0 = certificateId
                        if (cell != null && cell.getCellType() == CellType.STRING) {
                            try {
                                existingIds.add(Integer.parseInt(cell.getStringCellValue().trim()));
                            } catch (NumberFormatException ignored) {}
                        } else if (cell != null && cell.getCellType() == CellType.NUMERIC) {
                            existingIds.add((int) cell.getNumericCellValue());
                        }
                    }
                }

                workbook.close();
                fis.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Error reading Excel file.";
        }

        // If input is null or empty → auto-generate the next available ID
        if (givenId == null || givenId.trim().isEmpty()) {
            return String.valueOf(getNextId(existingIds));
        }

        try {
            int inputId = Integer.parseInt(givenId.trim());

            if (existingIds.contains(inputId)) {
                throw new RuntimeException("Duplicate certificate ID: " + inputId);
            } else {
                return String.valueOf(inputId); // Valid, non-duplicate ID
            }

        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid ID format. Must be a number.");
        }
    }

    private int getNextId(Set<Integer> existingIds) {
        int id = 1;
        while (existingIds.contains(id)) {
            id++;
        }
        return id;
    }


    public List<CertificatesTable> readAll() {
        List<CertificatesTable> list = new ArrayList<>();

        String auditFile = configuration.getAuditFile();
        try (FileInputStream fis = new FileInputStream(new File(auditFile));
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            // Skip header
            if (rows.hasNext()) {
                rows.next();
            }

            while (rows.hasNext()) {
                Row row = rows.next();
                CertificatesTable cert = new CertificatesTable();

                cert.setCertificateId(getStringCellValue(row.getCell(0)));
                cert.setCertificateTitle(getStringCellValue(row.getCell(1)));
                cert.setAuthorName(getStringCellValue(row.getCell(2)));
                cert.setPaperTopic(getStringCellValue(row.getCell(3)));
                cert.setVolumeNo(getStringCellValue(row.getCell(4)));
                cert.setIssueNo(getStringCellValue(row.getCell(5)));
                cert.setCertificateDate(getDateCellValue(row.getCell(6)));
                cert.setIssueDate(getDateCellValue(row.getCell(7)));
                cert.setIssuedBy(getStringCellValue(row.getCell(8)));

                list.add(cert);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private String getStringCellValue(Cell cell) {
        if (cell == null) return "";
        return cell.getCellType() == CellType.STRING
                ? cell.getStringCellValue()
                : String.valueOf((long) cell.getNumericCellValue());
    }

    private Date getDateCellValue(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return cell.getDateCellValue();
        }
        try {
            return new SimpleDateFormat("dd/MM/yyyy").parse(getStringCellValue(cell));
        } catch (Exception e) {
            return null;
        }
    }
}



