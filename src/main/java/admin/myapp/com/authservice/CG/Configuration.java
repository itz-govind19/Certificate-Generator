package admin.myapp.com.authservice.CG;

import org.springframework.beans.factory.annotation.Value;

@org.springframework.context.annotation.Configuration
public class Configuration {

    @Value(value = "${inputFilePath}")
    private String inputFilePath;


    @Value(value = "${outputFilePath}")
    private String outputFilePath;

    @Value(value = "${auditFile}")
    private String auditFile;

    @Value(value = "${libreOfficePath}")
    private String libreOfficePath;

    @Value(value = "${signaturePath}")
    private String signaturePath;

    @Value(value = "${logoPath}")
    private String logoPath;


    public String getOutputFilePath() {
        return outputFilePath;
    }

    public void setOutputFilePath(String outputFilePath) {
        this.outputFilePath = outputFilePath;
    }

    public String getInputFilePath() {
        return inputFilePath;
    }

    public void setInputFilePath(String inputFilePath) {
        this.inputFilePath = inputFilePath;
    }

    public String getAuditFile() {
        return auditFile;
    }

    public void setAuditFile(String auditFile) {
        this.auditFile = auditFile;
    }

    public String getLibreOfficePath() {
        return libreOfficePath;
    }

    public void setLibreOfficePath(String libreOfficePath) {
        this.libreOfficePath = libreOfficePath;
    }

    public String getSignaturePath() {
        return signaturePath;
    }

    public void setSignaturePath(String signaturePath) {
        this.signaturePath = signaturePath;
    }

    public String getLogoPath() {
        return logoPath;
    }

    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
    }
}
