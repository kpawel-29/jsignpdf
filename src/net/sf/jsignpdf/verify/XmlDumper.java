package net.sf.jsignpdf.verify;

import org.bouncycastle.jce.provider.X509CertificateObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import sun.security.provider.certpath.X509CertPath;

import javax.security.cert.Certificate;
import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.security.cert.CertPath;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

/**
 * Created by mhytry on 02.03.2016.
 */
public class XmlDumper {

    private File resultFile;

    private Document doc;


    public XmlDumper(String resultFilePath){
        resultFile = new File(resultFilePath);
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
                signatureResult.appendChild(createElement("RevisionId", Integer.toString(result.get(i).getRevisionId())));
                signatureResult.appendChild(createElement("IsWholeDocument", Boolean.toString(result.get(i).isWholeDocument())));
                signatureResult.appendChild(createElement("isModified", Boolean.toString(result.get(i).isModified())));


                byte[] signCert = result.get(i).getSigingCertificate().getEncoded();
                StringWriter sw = new StringWriter();
                sw.write(DatatypeConverter.printBase64Binary(signCert).replaceAll("(.{64})", "$1\n"));

                signatureResult.appendChild(createElement("SigningCertificate", sw.toString()));
                signatureResult.appendChild(createElement("Subject", result.get(i).getSubject()));
                signatureResult.appendChild(createCertChainElement(result.get(i).getCertPath()));//certChain


                if (result.get(i).getTstoken() != null) {
                    byte[] token = result.get(i).getTstoken().getEncoded();
                    sw.write(DatatypeConverter.printBase64Binary(token).replaceAll("(.{64})", "$1\n"));
                    signatureResult.appendChild(createElement("TimestampToken", sw.toString()));//TstToken
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
            StringWriter sw = new StringWriter();
            sw.write(DatatypeConverter.printBase64Binary(cert).replaceAll("(.{64})", "$1\n"));
            certificatePath.appendChild(createElement("certificate", sw.toString()));
        }

        return certificatePath;
    }

    private void save() throws IOException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
//        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
//        StreamResult result = new StreamResult(System.out);

//        Document document = (Document)doc;
        PrintWriter out = new PrintWriter(new FileWriter("/Users/pkadlubowski/dupa.xml"));
        out.println(source.getNode().getChildNodes().item(0).toString());
//        out.println("dupa");
        out.close();

//        System.out.print(source.getNode().getChildNodes().item(0).toString());


//        transformer.transform(source, result);
    }

}
