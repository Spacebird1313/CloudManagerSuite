package be.uantwerpen.tools;

import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.StringReader;

/**
 * Created by Thomas on 18/02/2016.
 */
public class XMLParser
{
    public static NodeList parseXML(String xmlString, String tagName) throws Exception
    {
        DocumentBuilder db;

        try
        {
            db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        }
        catch(ParserConfigurationException ex)
        {
            throw new Exception(ex.getMessage() + ":\n" + ex.getStackTrace());
        }

        InputSource inputSource = new InputSource();
        inputSource.setCharacterStream(new StringReader(xmlString));

        Document document = db.parse(inputSource);
        NodeList nodeList = document.getElementsByTagName(tagName);

        return nodeList;
    }
}
