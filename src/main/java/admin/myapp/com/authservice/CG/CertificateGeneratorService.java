package admin.myapp.com.authservice.CG;

import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlCursor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CertificateGeneratorService {

    @Autowired
    private Configuration configuration;

    @Autowired
    private ExcelWritter writeExcel;

    public ByteArrayOutputStream startPdfGeneration(CertificateDTO dto) {
        try {


            String date = convertToDdMmYyyy(dto.getDate());
            String authername = dto.getAuthername();
            String paperId = dto.getPaperId();
            String paperTopic = dto.getPaperTopic();
            String pages = dto.getPages();
            String certificateId = dto.getCertificateId();

            String s = writeExcel.validateOrGenerateNumericCertificateId(certificateId);
            dto.setCertificateId(s);
            LocalDate localDate = LocalDate.of(Integer.valueOf(date.split("-")[2]), Integer.valueOf(date.split("-")[1]), Integer.valueOf(date.split("-")[0]));
            String[] ints = calculateVolumeAndIssue(localDate);

            String inPath = openEditAndWriteDocx1(authername, paperId, paperTopic, ints[0], ints[1], localDate, pages, s);
            ByteArrayOutputStream byteArrayOutputStream = convertWordToPdf1(inPath, configuration.getOutputFilePath());
            CertificatesTable certificatesTable = convertDtoToEntity(dto, ints[0], ints[1]);
            writeExcel.auditCertificate(certificatesTable);
            return byteArrayOutputStream;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }


    public static String convertToDdMmYyyy(String inputDate) {
        // Parse input date (ISO format)
        LocalDate localDate = LocalDate.parse(inputDate); // e.g., "2025-05-28"

        // Format to dd-MM-yyyy
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return localDate.format(formatter);
    }


    private CertificatesTable convertDtoToEntity(CertificateDTO dto, String volume, String issue) throws ParseException {
        CertificatesTable target = new CertificatesTable();
        target.setCertificateId(dto.getCertificateId());
        target.setCertificateTitle("IJITMES");
        target.setAuthorName(dto.getAuthername());
        target.setPaperTopic(dto.getPaperTopic());
        target.setVolumeNo(volume);
        target.setIssueNo(issue);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        target.setCertificateDate(sdf.parse(dto.getDate()));
        target.setIssueDate(new Date());
        target.setIssuedBy("AKASH JADHAV");
        return target;
    }


    public String openEditAndWriteDocx1(String authorName, String paperId, String paperTopic,
                                        String volume, String issue, LocalDate localDate,
                                        String pages, String certificateId) {

        String inputPath = configuration.getInputFilePath();
        String outputFilePath = configuration.getOutputFilePath();
        String outputPath = outputFilePath + volume + "_" + certificateId + "certificate.docx";

        File docFile = new File(inputPath);

        try (FileInputStream fis = new FileInputStream(docFile);
             XWPFDocument document = new XWPFDocument(fis)) {

            List<XWPFParagraph> paragraphs = new ArrayList<>(document.getParagraphs());

            boolean imageInserted = false;

            for (XWPFParagraph paragraph : paragraphs) {

                StringBuilder fullTextBuilder = new StringBuilder();
                for (XWPFRun run : paragraph.getRuns()) {
                    String runText = run.getText(0);
                    if (runText != null) {
                        fullTextBuilder.append(runText);
                    }
                }

                String fullText = fullTextBuilder.toString();

                // Handle REF line replacement
                if (fullText.equals("R E F : I J I T M E S / C E R T I F I C A T E / V O L U M E ? ? / I S S U E # # / 0 0 0  @")) {
                    XWPFRun oldRun = paragraph.getRuns().isEmpty() ? null : paragraph.getRuns().get(0);

                    String verticalAlignment = null;
                    int fontSize = -1;
                    String fontFamily = null;
                    String color = null;
                    boolean isBold = false;
                    boolean isItalic = false;
                    UnderlinePatterns underline = null;

                    if (oldRun != null) {
                        verticalAlignment = oldRun.getVerticalAlignment() != null ? oldRun.getVerticalAlignment().toString() : null;
                        fontSize = oldRun.getFontSize();
                        fontFamily = oldRun.getFontFamily();
                        color = oldRun.getColor();
                        isBold = oldRun.isBold();
                        isItalic = oldRun.isItalic();
                        underline = oldRun.getUnderline();
                    }

                    for (int i = paragraph.getRuns().size() - 1; i >= 0; i--) {
                        paragraph.removeRun(i);
                    }

                    XWPFRun newRun = paragraph.createRun();
                    String spacedVolume = volume.replaceAll("(.)", "$1 ").trim();
                    String spacedIssue = issue.replaceAll("(.)", "$1 ").trim();
                    newRun.setText("R E F : I J I T M E S / C E R T I F I C A T E / V O L U M E " + spacedVolume + " / I S S U E " + spacedIssue + " / 0 0 0 " + certificateId);

                    if (verticalAlignment != null) {
                        newRun.setVerticalAlignment(verticalAlignment);
                    }
                    if (fontSize != -1) {
                        newRun.setFontSize(fontSize);
                    }
                    if (fontFamily != null) {
                        newRun.setFontFamily(fontFamily);
                    }
                    if (color != null) {
                        newRun.setColor(color);
                    }
                    newRun.setBold(isBold);
                    newRun.setItalic(isItalic);
                    if (underline != null) {
                        newRun.setUnderline(underline);
                    }

                    continue;
                }

                // Handle DATE replacement
                if (fullText.equals("DATE:DD/MM/YYYY")) {
                    XWPFRun oldRun = paragraph.getRuns().isEmpty() ? null : paragraph.getRuns().get(0);

                    String verticalAlignment = null;
                    int fontSize = -1;
                    String fontFamily = null;
                    String color = null;
                    boolean isBold = false;
                    boolean isItalic = false;
                    UnderlinePatterns underline = null;

                    if (oldRun != null) {
                        verticalAlignment = oldRun.getVerticalAlignment() != null ? oldRun.getVerticalAlignment().toString() : null;
                        fontSize = oldRun.getFontSize();
                        fontFamily = oldRun.getFontFamily();
                        color = oldRun.getColor();
                        isBold = oldRun.isBold();
                        isItalic = oldRun.isItalic();
                        underline = oldRun.getUnderline();
                    }

                    for (int i = paragraph.getRuns().size() - 1; i >= 0; i--) {
                        paragraph.removeRun(i);
                    }

                    XWPFRun newRun = paragraph.createRun();
                    newRun.setText("DATE:" + localDate.getDayOfMonth() + "/" + localDate.getMonthValue() + "/" + localDate.getYear());

                    if (verticalAlignment != null) {
                        newRun.setVerticalAlignment(verticalAlignment);
                    }
                    if (fontSize != -1) {
                        newRun.setFontSize(9);
                    }
                    if (fontFamily != null) {
                        newRun.setFontFamily("Cambria");
                    }
                    if (color != null) {
                        newRun.setColor(color);
                    }
                    newRun.setBold(true);
                    if (underline != null) {
                        newRun.setUnderline(underline);
                    }

                    continue;
                }

                for (XWPFRun run : new ArrayList<>(paragraph.getRuns())) {
                    String runText = run.getText(0);
                    if (runText != null) {
                        if (runText.contains("Name of the author")) {
                            run.setText(runText.replace("Name of the author", authorName), 0);
                        }

                        if (runText.contains("*")) {
                            run.setText(runText.replace("*", paperId), 0);
                        }

                        if (runText.contains("Name of the paper topic")) {
                            run.setText(runText.replace("Name of the paper topic", paperTopic), 0);
                        }

                        if (runText.contains("XX")) {
                            run.setText(runText.replace("XX", volume.replace(" ", "")), 0);
                        }

                        if (runText.equals("YY")) {
                            run.setText(runText.replace("YY", issue.replace(" ", "")), 0);
                        }

                        if (runText.contains("Month 2025")) {
                            run.setText(runText.replace("Month 2025", localDate.getMonth() + " " + localDate.getYear()), 0);
                        }

                        if (runText.contains("-0001")) {
                            run.setText(runText.replace("0001", pages.split(" ")[0]), 0);
                        }

                        if (runText.contains("0004.")) {
                            run.setText(runText.replace("0004.", pages.split(" ")[2]), 0);

                        }


                        if (!imageInserted) {
                            imageInserted = true;

                            // Create the image table

                            XmlCursor cursor = paragraph.getCTP().newCursor();
                            cursor.toNextSibling(); // Move to insert *after* this paragraph

                            XWPFTable imageTable = document.createTable(2, 3);

                            imageTable.setTableAlignment(TableRowAlign.CENTER);
                            imageTable.setWidth("90%");
                            imageTable.removeBorders();


                            // ---- Logo cell ----
                            XWPFTableCell cell1 = imageTable.getRow(0).getCell(0);
                            XWPFParagraph logoPara = cell1.getParagraphs().get(0);
                            logoPara.setAlignment(ParagraphAlignment.CENTER);
                            XWPFRun logoRun = logoPara.createRun();
                            String logoPath = configuration.getLogoPath();
                            try (FileInputStream logoImage = new FileInputStream(logoPath)) {
                                logoRun.addPicture(
                                        logoImage,
                                        Document.PICTURE_TYPE_PNG,
                                        "logo.png",
                                        Units.toEMU(150),
                                        Units.toEMU(150)
                                );
                            } catch (Exception e) {
                                System.err.println("Logo image insert failed: " + e.getMessage());
                            }

                            XWPFTableCell blankCell = imageTable.getRow(0).getCell(1);
                            CTTcPr tcPr = blankCell.getCTTc().addNewTcPr();
                            CTTblWidth cellWidth = tcPr.addNewTcW();
                            cellWidth.setW(BigInteger.valueOf(0)); // adjust the value as needed
                            cellWidth.setType(STTblWidth.DXA); // DXA = twentieths of a point

                            // ---- Signature cell ----
                            XWPFTableCell cell2 = imageTable.getRow(0).getCell(2);
                            XWPFParagraph sigPara = cell2.getParagraphs().get(0);
                            sigPara.setAlignment(ParagraphAlignment.CENTER);

                            String signaturePath = configuration.getSignaturePath();
                            try (FileInputStream sigImage = new FileInputStream(signaturePath)) {
                                // Signature image
                                XWPFRun sigRun = sigPara.createRun();
                                sigRun.addPicture(
                                        sigImage,
                                        Document.PICTURE_TYPE_PNG,
                                        "signature.png",
                                        Units.toEMU(150),
                                        Units.toEMU(80)
                                );

                                // "Editor in Chief" — appears below
                                XWPFRun titleRun = sigPara.createRun();
                                titleRun.addBreak();
                                titleRun.setText("Editor in Chief");
                                titleRun.setFontSize(12);
                                titleRun.setBold(true);
                                titleRun.setFontFamily("Cambria");

                                System.out.println("Signature, line, and title inserted.");


                                XWPFTableCell welocome = imageTable.getRow(1).getCell(2);
                                XWPFParagraph welocomePara = welocome.getParagraphs().get(0);
                                welocomePara.setAlignment(ParagraphAlignment.CENTER);
                                XWPFRun welocomeParaRun = welocomePara.createRun();

                                CTTcPr tcPr1 = welocome.getCTTc().addNewTcPr();
                                CTTblWidth welcomeWidth = tcPr1.addNewTcW();
                                welcomeWidth.setW(BigInteger.valueOf(0));
                                welcomeWidth.setType(STTblWidth.DXA);

                                CTVerticalJc vAlign = tcPr1.addNewVAlign();
                                vAlign.setVal(STVerticalJc.BOTTOM);

                                XWPFTableRow row = imageTable.getRow(1);
                                CTTrPr trPr = row.getCtRow().addNewTrPr();
                                CTHeight rowHeight = trPr.addNewTrHeight();
                                rowHeight.setVal(BigInteger.valueOf(2000)); // Height in twips
                                rowHeight.setHRule(STHeightRule.AT_LEAST);

// Remove spacing in the paragraph to make it touch bottom
                                welocomePara.setSpacingAfter(0);
                                welocomePara.setSpacingBefore(0);
                                welocomePara.setSpacingBetween(1.0); // Single line spacing

                                welocomeParaRun.setColor("7B1212");
                                welocomeParaRun.addBreak();
                                welocomeParaRun.setText("WE WISH FOR YOUR BETTER FUTURE");
                                welocomeParaRun.addBreak();
                                welocomeParaRun.setText("WWW.IJITMES.COM");
                                welocomeParaRun.setFontSize(12);
                                welocomeParaRun.setBold(true);
                                welocomeParaRun.setItalic(true);
                                welocomeParaRun.setFontFamily("Palatino Linotype");


                            } catch (Exception e) {
                                System.err.println("Error inserting signature image: " + e.getMessage());
                                e.printStackTrace();
                            }
                        }


                    }
                }
            }

            // Save output document
            try (FileOutputStream fos = new FileOutputStream(outputPath)) {
                document.write(fos);
                System.out.println("File read, edited, and saved successfully to: " + outputPath);
            }
        } catch (IOException e) {
            System.err.println("IO Exception occurred: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected exception occurred: " + e.getMessage());
            e.printStackTrace();
        }
        return outputPath;
    }


    public ByteArrayOutputStream convertWordToPdf1(String input, String outputPath) throws Exception {
        try {
            File inputFile = new File(input);


            if (!inputFile.exists()) {
                throw new FileNotFoundException("Input file not found: " + input);
            }

            String s = String.valueOf(System.currentTimeMillis());
            String libreOfficePath = configuration.getLibreOfficePath();
            String[] command = {
                    libreOfficePath,
                    "--headless",
                    "--convert-to",
                    "pdf:writer_pdf_Export",
                    "--outdir",
                    new File(outputPath).getAbsolutePath(),
                    inputFile.getAbsolutePath()
            };

            ProcessBuilder processBuilder = new ProcessBuilder(command);
            Process process = processBuilder.start();
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                throw new RuntimeException("LibreOffice conversion failed with exit code: " + exitCode);
            }

            // ✅ Read the actual PDF file into ByteArrayOutputStream
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try (FileInputStream fis = new FileInputStream(input.replace("docx", "pdf"))) {
                byte[] buffer = new byte[1024];
                int len;
                while ((len = fis.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, len);
                }
            }

            return outputStream;

        } catch (Exception e) {
            System.err.println("Error during conversion: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public static String[] calculateVolumeAndIssue(LocalDate inputDate) {
        // Reference date: May 20th is issue 1, volume 1
        // We'll use a base year to calculate from
        int baseYear = 2025; // You can adjust this as needed
        LocalDate baseDate = LocalDate.of(baseYear, 5, 20); // May 20, base year

        // Calculate volume (changes every year on May 20th)
        int volume;
        if (inputDate.getMonthValue() >= 5 && inputDate.getDayOfMonth() >= 20) {
            // From May 20th onwards in the year
            volume = inputDate.getYear() - baseYear + 1;
        } else {
            // Before May 20th in the year
            volume = inputDate.getYear() - baseYear;
        }

        // Ensure volume is at least 1
        if (volume < 1) volume = 1;

        // Calculate issue (changes every month on the 20th)
        int issue;
        LocalDate currentVolumeStart;

        if (inputDate.getMonthValue() >= 5 && inputDate.getDayOfMonth() >= 20) {
            // Current volume started in May of this year
            currentVolumeStart = LocalDate.of(inputDate.getYear(), 5, 20);
        } else {
            // Current volume started in May of previous year
            currentVolumeStart = LocalDate.of(inputDate.getYear() - 1, 5, 20);
        }

        // Count months from volume start to input date
        int monthsFromStart = 0;
        LocalDate checkDate = currentVolumeStart;

        while (checkDate.isBefore(inputDate) || checkDate.isEqual(inputDate)) {
            LocalDate nextIssueDate = checkDate.plusMonths(1);
            if (nextIssueDate.isAfter(inputDate)) {
                break;
            }
            monthsFromStart++;
            checkDate = nextIssueDate;
        }

        issue = monthsFromStart + 1;

        if (volume < 10) {
            volume = volume;
        }
        return new String[]{String.format("%02d", volume), String.format("%02d", issue)};
    }

    public PaginationDto getAllList(int page, int size) {

        PaginationDto dto = new PaginationDto();
        List<CertificatesTable> certificatesTables = writeExcel.readAll();

        int total = certificatesTables.size();
        int start = Math.min(page * size, total);
        int end = Math.min((page + 1) * size, total);

        List<CertificatesTable> paginated = certificatesTables.subList(start, end);
        dto.setData(paginated);
        dto.setTotalElements(total);
        int totalPages = (int) Math.ceil((double) total / size);
        dto.setTotalPages(totalPages);
        dto.setPage(page);
        return dto;
    }


    public ByteArrayOutputStream getPdfByCertificateId(String certificateId) {
        String outputFilePath = configuration.getOutputFilePath();
        List<File> files = listFilesInDirectory(outputFilePath);
        Optional<File> first = files.stream().filter(en -> en.getName().contains(certificateId + "certificate.pdf")).findFirst();
        if (first.isPresent()) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try (FileInputStream fis = new FileInputStream(first.get())) {
                byte[] buffer = new byte[1024];
                int len;
                while ((len = fis.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, len);
                }
                return outputStream;
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public List<File> listFilesInDirectory(String directoryPath) {
        List<File> list = new ArrayList<>();
        File directory = new File(directoryPath);

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null && files.length > 0) {
                System.out.println("Files in directory " + directoryPath + ":");
                for (File file : files) {
                    if (file.isFile()) {
                        list.add(file);
                    }
                }
            } else {
                System.out.println("No files found in directory " + directoryPath);
            }
        } else {
            System.out.println(directoryPath + " is not a valid directory");
        }
        return list;
    }

}
