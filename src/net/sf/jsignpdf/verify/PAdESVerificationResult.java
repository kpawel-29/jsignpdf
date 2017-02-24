package net.sf.jsignpdf.verify;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;

/**
 * Created by pkadlubowski on 22.02.2017.
 */
public class PAdESVerificationResult {
    private ArrayList<SignatureVerificationResult> signaturesVerifResults;
    private int totalRevisions;
    private XmlDumper dumper;

    public PAdESVerificationResult(ArrayList<SignatureVerificationResult> signaturesVerifResults, int totalRevisions, String outputFile) throws IOException {
        this.signaturesVerifResults = signaturesVerifResults;
        this.totalRevisions = totalRevisions;
        this.dumper = new XmlDumper(outputFile);
    }

    public void dump() {
        try {
            dumper.dump(signaturesVerifResults, totalRevisions);
        } catch (Exception e) {
            dumper.dump(e);
            System.exit(1);
        }
    }

}
