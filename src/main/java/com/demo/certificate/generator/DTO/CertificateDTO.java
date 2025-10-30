package com.demo.certificate.generator.DTO;

public class CertificateDTO {
    private String authername;
    private String paperId;
    private String paperTopic;
    private String pages;
    private String certificateId;
    private String date;

    public String getAuthername() {
        return authername;
    }

    public void setAuthername(String authername) {
        this.authername = authername;
    }

    public String getPaperId() {
        return paperId;
    }

    public void setPaperId(String paperId) {
        this.paperId = paperId;
    }

    public String getPaperTopic() {
        return paperTopic;
    }

    public void setPaperTopic(String paperTopic) {
        this.paperTopic = paperTopic;
    }

    public String getPages() {
        return pages;
    }

    public void setPages(String pages) {
        this.pages = pages;
    }

    public String getCertificateId() {
        return certificateId;
    }

    public void setCertificateId(String certificateId) {
        this.certificateId = certificateId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
