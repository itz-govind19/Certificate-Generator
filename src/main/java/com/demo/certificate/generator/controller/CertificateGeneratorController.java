package com.demo.certificate.generator.controller;

import com.demo.certificate.generator.DTO.CertificateDTO;
import com.demo.certificate.generator.DTO.PaginationDto;
import com.demo.certificate.generator.entity.CertificatesTable;
import com.demo.certificate.generator.service.CertificateGeneratorService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;



@Controller
public class CertificateGeneratorController {

    @Autowired
    private CertificateGeneratorService certificateGeneratorService;

    @GetMapping("/")
    public String showForm(Model model) {
        if (!model.containsAttribute("certificateDTO")) {
            model.addAttribute("certificateDTO", new CertificateDTO());
        }
        return "index";
    }



    @PostMapping("/generate")
    public String generateCertificate(@ModelAttribute CertificateDTO dto,
                                      HttpServletResponse response,
                                      RedirectAttributes redirectAttributes) {
        try {
            // Generate PDF as byte array
            ByteArrayOutputStream pdfOutput = certificateGeneratorService.startPdfGeneration(dto);

            // Set response headers to trigger download
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=certificate.pdf");

            OutputStream out = response.getOutputStream();
            pdfOutput.writeTo(out);
            out.flush();
            out.close();

            return null; // prevents redirect — response already handled

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Failed to generate PDF");
            return "redirect:/";
        }
    }


    @GetMapping("/certificates")
    public String listCertificates(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        try {
            PaginationDto paginated = certificateGeneratorService.getAllList(page, size);
            model.addAttribute("certificates", (List<CertificatesTable>)paginated.getData());
            model.addAttribute("pageNumber", page);
            model.addAttribute("totalPages",paginated.getTotalPages());
            model.addAttribute("size", size);
        }catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", e.getMessage());
            return "redirect:/";
        }
        return "certificates";
    }

//    @GetMapping("/download/{certificateId}")
//    public void downloadCertificate(@PathVariable String certificateId, HttpServletResponse response) {
//        try {
//            ByteArrayOutputStream pdf = certificateGeneratorService.getPdfByCertificateId(certificateId);
//            if (pdf == null) {
//                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Certificate not found");
//                return;
//            }
//
//            response.setContentType("application/pdf");
//            response.setHeader("Content-Disposition", "attachment; filename=certificate_" + certificateId + ".pdf");
//
//            OutputStream out = response.getOutputStream();
//            pdf.writeTo(out);
//            out.flush();
//            out.close();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            response.setStatus(HttpServletResponse.SC_EXPECTATION_FAILED,e.getMessage());
//            return;
//        }
//    }

    @GetMapping("/download/{certificateId}")
    public void downloadCertificate(@PathVariable String certificateId, HttpServletResponse response) throws IOException {
        try {
                ByteArrayOutputStream pdf = certificateGeneratorService.getPdfByCertificateId(certificateId);

            if (pdf == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Certificate not found");
                return;
            }

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=certificate_" + certificateId + ".pdf");

            OutputStream out = response.getOutputStream();
            pdf.writeTo(out);
            out.flush();
            out.close();

        } catch (Exception e) {
            try {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("Error generating the certificate: " + e.getMessage());
            } catch (IOException ioException) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("Error generating the certificate: " + e.getMessage());            }
        }
    }

}
