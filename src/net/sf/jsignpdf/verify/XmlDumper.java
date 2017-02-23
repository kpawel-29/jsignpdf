package net.sf.jsignpdf.verify;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.text.SimpleDateFormat;
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

    //TODO: dodać info o miejscu wyexportowania pdf'ów
    public void dump(ArrayList<SignatureVerificationResult> result, int totalRevisions){
        try {

            createDocumentContainer();

            addElement("TotalRevisions", Integer.toString(totalRevisions));

            for (int i=0; i < result.size(); i++) {
//                addElement();
            }


//            if (!result.isSuccess && result.errorMsg != null) {
//                addElement("ErrorMessage", result.errorMsg);
//            }
//
//            if (result.signingTime != null){
//                SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                addElement("SigningTime", sdfDate.format(result.signingTime));
//            }
//
//
//            if (result.messageImprint != null){
//                addElement("MessageImprint", result.messageImprint);
//            }
//
//            if (result.messageImprintAlgo != null){
//                addElement("MessageImprintAlgorithm", result.messageImprintAlgo);
//            }
//
//            if (result.certificate != null){
//                addElement("Certificate", DatatypeConverter.printBase64Binary(result.certificate.getEncoded()));
//            }

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
        Element rootElement = doc.createElement("TimestampResult");
        doc.appendChild(rootElement);
    }

    private void addElement(String name, String value){
        Element newNode = doc.createElement(name);
        newNode.appendChild(doc.createTextNode(value));
        doc.getDocumentElement().appendChild(newNode);
    }

    private void save() throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(resultFile);

        transformer.transform(source, result);
    }

}
