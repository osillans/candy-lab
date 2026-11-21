package com.candy.parser;

import com.candy.model.Sweet;
import java.io.InputStream;
import java.util.List;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public final class StaxCandyParser extends AbstractCandyParser {

    public StaxCandyParser() {
        super("StAX");
    }

    @Override
    protected List<Sweet> parseStream(InputStream input)
            throws XMLStreamException {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        factory.setProperty(XMLInputFactory.IS_COALESCING, true);
        XMLStreamReader reader = factory.createXMLStreamReader(input);
        try {
            SweetEventCollector collector = new SweetEventCollector();
            while (reader.hasNext()) {
                switch (reader.next()) {
                    case XMLStreamConstants.START_ELEMENT ->
                            collector.startElement(reader.getLocalName(),
                                    reader.getAttributeValue(null,
                                            XmlTags.ID));
                    case XMLStreamConstants.CHARACTERS ->
                            collector.characters(reader.getText());
                    case XMLStreamConstants.END_ELEMENT ->
                            collector.endElement(reader.getLocalName());
                    default -> {
                    }
                }
            }
            return collector.sweets();
        } finally {
            reader.close();
        }
    }
}