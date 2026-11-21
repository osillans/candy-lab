package com.candy.parser;

import com.candy.model.Sweet;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public final class DomCandyParser extends AbstractCandyParser {

    public DomCandyParser() {
        super("DOM");
    }

    @Override
    protected List<Sweet> parseStream(InputStream input)
            throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilder builder =
                DocumentBuilderFactory.newInstance().newDocumentBuilder();
        builder.setErrorHandler(new DefaultHandler());
        Document document = builder.parse(input);
        NodeList nodes = document.getElementsByTagName(XmlTags.SWEET);
        List<Sweet> sweets = new ArrayList<>();
        for (int i = 0; i < nodes.getLength(); i++) {
            sweets.add(toSweet((Element) nodes.item(i)));
        }
        return sweets;
    }

    private static Sweet toSweet(Element element) {
        SweetBuilder builder =
                new SweetBuilder(element.getAttribute(XmlTags.ID));
        NodeList children = element.getElementsByTagName("*");
        for (int i = 0; i < children.getLength(); i++) {
            Element child = (Element) children.item(i);
            builder.set(child.getTagName(), child.getTextContent());
        }
        return builder.build();
    }
}