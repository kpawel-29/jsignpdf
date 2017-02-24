package net.sf.jsignpdf.verify;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import java.io.*;
import java.security.cert.CertPath;
import java.security.cert.CertificateEncodingException;
import java.util.ArrayList;

public class XmlDumper {

    private FileWriter resultFile;

    private Document doc;

    public XmlDumper(String resultFilePath) throws IOException {
        resultFile = new FileWriter(resultFilePath);
    }

    public void dump(ArrayList<SignatureVerificationResult> result, int totalRevisions){
        try {

            createDocumentContainer();

            addElement("TotalRevisions", Integer.toString(totalRevisions));

            Element signaturesResults = doc.createElement("SignaturesResults");

            for (int i=0; i < result.size(); i++) {
                Element signatureResult = doc.createElement("SignatureResult");

                signatureResult.appendChild(createElement("sigName", result.get(i).getSigName()));
                signatureResult.appendChild(createElement("Name", result.get(i).getName()));
                signatureResult.appendChild(createElement("Subject", result.get(i).getSubject()));
                signatureResult.appendChild(createElement("Revision", Integer.toString(result.get(i).getRevisionId())));
                signatureResult.appendChild(createElement("IsWholeDocument", Boolean.toString(result.get(i).isWholeDocument())));
                signatureResult.appendChild(createElement("isModified", Boolean.toString(result.get(i).isModified())));

                if (result.get(i).getSigingCertificate() != null) {
                    signatureResult.appendChild(createElement("SigningCertificate", getSigningCertificateString(result.get(i))));
                }

                signatureResult.appendChild(createElement("Subject", result.get(i).getSubject()));

                signatureResult.appendChild(createCertChainElement(result.get(i).getCertPath()));

                if (result.get(i).getTstoken() != null) {
                    signatureResult.appendChild(createElement("TimeStampToken", getTimestampString(result.get(i))));
                }

                if (result.get(i).getExtractedPath() != null) {
                    signatureResult.appendChild(createElement("ExtractPath", result.get(i).getExtractedPath()));
                }

                signaturesResults.appendChild(signatureResult);
            }
            doc.getDocumentElement().appendChild(signaturesResults);

            save();

        } catch (Exception e){
            System.out.print("Nie udało się zapisać rezultatu w xml");
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void dump(Exception e){

        try {
            createDocumentContainer();

            addElement("VerificationStatus", "incorrect");
            addElement("ErrorMessage", e.getMessage());

            save();
        } catch (Exception ex){
            System.out.print("Nie udało się zapisać rezultatu w xml");
            ex.printStackTrace();
            System.exit(1);
        }

    }

    private void createDocumentContainer() throws ParserConfigurationException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("PAdESResult");
        doc.appendChild(rootElement);
    }

    private Element createElement(String name, String value){
        Element newNode = doc.createElement(name);
        newNode.appendChild(doc.createTextNode(value));
        return newNode;
    }

    private void addElement(String name, String value){
        Element newNode = doc.createElement(name);
        newNode.appendChild(doc.createTextNode(value));
        doc.getDocumentElement().appendChild(newNode);
    }

    private Element createCertChainElement(CertPath certPath) throws CertificateEncodingException {
        Element certificatePath = doc.createElement("certificationPath");
        for (int i = 0; i < certPath.getCertificates().size(); i++) {
            byte[] cert = certPath.getCertificates().get(0).getEncoded();

            certificatePath.appendChild(createElement("certificate", byteToSting(cert)));
        }

        return certificatePath;
    }

    private String getTimestampString(SignatureVerificationResult result) throws IOException {
        byte[] token = result.getTstoken().getEncoded();

        return  byteToSting(token);
    }

    private String getSigningCertificateString(SignatureVerificationResult result) throws CertificateEncodingException {
        byte[] signCert = result.getSigingCertificate().getEncoded();

        return byteToSting(signCert);
    }

    private String byteToSting(byte[] b) {
        StringWriter sw = new StringWriter();
        sw.write(DatatypeConverter.printBase64Binary(b).replaceAll("(.{64})", "$1\n"));

        return sw.toString();
    }

    private void save() {
        DOMSource source = new DOMSource(doc);
        PrintWriter out = new PrintWriter(resultFile);

        String prefix = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>";
        out.println(prefix + source.getNode().getChildNodes().item(0).toString());
        out.close();
    }

}
