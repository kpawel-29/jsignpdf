package net.sf.jsignpdf.verify;

import java.util.ArrayList;

/**
 * Created by pkadlubowski on 22.02.2017.
 */
public class PAdESVerificationResult {
    private ArrayList<SignatureVerificationResult> signaturesVerifResults;
    private int totalRevisions;
    private VerificationResult tmpResult;

    private XmlDumper dumper;

    public PAdESVerificationResult(VerificationResult tmpResult) {
        this.tmpResult = tmpResult;
        this.totalRevisions = tmpResult.getTotalRevisions();
        manageSigResults();
        dumper.dump(this.signaturesVerifResults, this.totalRevisions);
    }

    private void manageSigResults() {
        ArrayList<SignatureVerificationResult> signVerifs = new ArrayList();
        for (SignatureVerification sv : tmpResult.getVerifications()) {
            signVerifs.add(signVerifs.size(), sv.getSigVerifyDTO());
        }

        this.signaturesVerifResults = signVerifs;
    }


}
