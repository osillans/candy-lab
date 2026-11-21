package com.candy.parser;

import com.candy.model.Sweet;
import java.util.ArrayList;
import java.util.List;

final class SweetEventCollector {

    private final List<Sweet> sweets = new ArrayList<>();
    private final StringBuilder text = new StringBuilder();
    private SweetBuilder current;

    void startElement(String name, String id) {
        text.setLength(0);
        if (name.equals(XmlTags.SWEET)) {
            current = new SweetBuilder(id);
        }
    }

    void characters(String chunk) {
        text.append(chunk);
    }

    void endElement(String name) {
        if (current == null) {
            return;
        }
        if (name.equals(XmlTags.SWEET)) {
            sweets.add(current.build());
            current = null;
        } else {
            current.set(name, text.toString());
        }
    }

    List<Sweet> sweets() {
        return sweets;
    }
}