package com.demo.certificate.generator.entity;

import java.util.Date;

public class CertificatesTable {
    private String certificateId;
    private String certificateTitle;
    private String authorName;
    private String paperTopic;
    private String volumeNo;
    private String issueNo;
    private Date certificateDate;
    private Date issueDate;
    private String issuedBy;

    public String getCertificateId() {
        return certificateId;
    }

    public void setCertificateId(String certificateId) {
        this.certificateId = certificateId;
    }

    public String getCertificateTitle() {
        return certificateTitle;
    }

    public void setCertificateTitle(String certificateTitle) {
        this.certificateTitle = certificateTitle;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getVolumeNo() {
        return volumeNo;
    }

    public String getPaperTopic() {
        return paperTopic;
    }

    public void setPaperTopic(String paperTopic) {
        this.paperTopic = paperTopic;
    }

    public void setVolumeNo(String volumeNo) {
        this.volumeNo = volumeNo;
    }

    public String getIssueNo() {
        return issueNo;
    }

    public void setIssueNo(String issueNo) {
        this.issueNo = issueNo;
    }

    public Date getCertificateDate() {
        return certificateDate;
    }

    public void setCertificateDate(Date cerificateDate) {
        this.certificateDate = cerificateDate;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public String getIssuedBy() {
        return issuedBy;
    }

    public void setIssuedBy(String issuedBy) {
        this.issuedBy = issuedBy;
    }

}
