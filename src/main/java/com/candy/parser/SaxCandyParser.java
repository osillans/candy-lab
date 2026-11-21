package com.candy.parser;

import com.candy.model.Sweet;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public final class SaxCandyParser extends AbstractCandyParser {

    public SaxCandyParser() {
        super("SAX");
    }

    @Override
    protected List<Sweet> parseStream(InputStream input)
            throws IOException, SAXException, ParserConfigurationException {
        SweetEventCollector collector = new SweetEventCollector();
        SAXParserFactory.newInstance().newSAXParser()
                .parse(input, new CollectingHandler(collector));
        return collector.sweets();
    }

    private static final class CollectingHandler extends DefaultHandler {

        private final SweetEventCollector collector;

        CollectingHandler(SweetEventCollector collector) {
            this.collector = collector;
        }

        @Override
        public void startElement(String uri, String localName, String qName,
                Attributes attributes) {
            collector.startElement(qName, attributes.getValue(XmlTags.ID));
        }

        @Override
        public void characters(char[] chars, int start, int length) {
            collector.characters(new String(chars, start, length));
        }

        @Override
        public void endElement(String uri, String localName, String qName) {
            collector.endElement(qName);
        }
    }
}