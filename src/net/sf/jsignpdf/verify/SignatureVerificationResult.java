package net.sf.jsignpdf.verify;

import org.bouncycastle.tsp.TimeStampToken;

import java.security.cert.CertPath;
import java.security.cert.X509Certificate;

/**
 * Created by pkadlubowski on 22.02.2017.
 */
public class SignatureVerificationResult {
    private String sigName;
    private String name;
    private String subject;
    private int revisionId;
    private boolean wholeDocument;
    private boolean isModified;
    private X509Certificate sigingCertificate;
    private CertPath certPath;
    private TimeStampToken tstoken;
    private Object fails;
    private String extractedPath;

    public SignatureVerificationResult(String sigName, String name, String subject, int revisionId, boolean wholeDocument, boolean isModified, X509Certificate sigingCertificate, CertPath certPath, TimeStampToken tstoken, Object fails, String extractedPath) {
        this.sigName = sigName;
        this.name = name;
        this.subject = subject;
        this.revisionId = revisionId;
        this.wholeDocument = wholeDocument;
        this.isModified = isModified;
        this.sigingCertificate = sigingCertificate;
        this.certPath = certPath;
        this.tstoken = tstoken;
        this.fails = fails;
        this.extractedPath = extractedPath;
    }

    public String getSigName() {
        return sigName;
    }

    public String getName() {
        return name;
    }

    public String getSubject() {
        return subject;
    }

    public int getRevisionId() {
        return revisionId;
    }

    public boolean isWholeDocument() {
        return wholeDocument;
    }

    public boolean isModified() {
        return isModified;
    }

    public X509Certificate getSigingCertificate() {
        return sigingCertificate;
    }

    public CertPath getCertPath() {
        return certPath;
    }

    public TimeStampToken getTstoken() {
        return tstoken;
    }

    public Object getFails() {
        return fails;
    }

    public String getExtractedPath() {
        return extractedPath;
    }
}
