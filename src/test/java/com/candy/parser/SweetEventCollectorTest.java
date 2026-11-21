package com.candy.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.candy.exception.CandyException;
import com.candy.model.Sweet;
import com.candy.support.SweetFixtures;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class SweetEventCollectorTest {

    private static final Map<String, String> CARAMEL = Map.ofEntries(
            Map.entry("Name", "Sunny Caramel"),
            Map.entry("Energy", "390"),
            Map.entry("Type", "Caramel"),
            Map.entry("Water", "8"),
            Map.entry("Sugar", "62000"),
            Map.entry("Fructose", "9000"),
            Map.entry("Vanillin", "12"),
            Map.entry("Proteins", "0.0"),
            Map.entry("Fats", "0.1"),
            Map.entry("Carbohydrates", "97.5"),
            Map.entry("Production", "Kyiv Confectionery"));

    private static void element(SweetEventCollector collector, String tag,
            String text) {
        collector.startElement(tag, null);
        collector.characters(text);
        collector.endElement(tag);
    }

    private static void caramel(SweetEventCollector collector, String id) {
        collector.startElement("Sweet", id);
        collector.characters("\n    ");
        CARAMEL.forEach((tag, text) -> {
            element(collector, tag, text);
            collector.characters("\n    ");
        });
        collector.endElement("Sweet");
    }

    @Test
    void startsEmpty() {
        assertTrue(new SweetEventCollector().sweets().isEmpty());
    }

    @Test
    void buildsSweetFromEvents() {
        SweetEventCollector collector = new SweetEventCollector();
        collector.startElement("Candy", null);
        caramel(collector, "C001");
        collector.endElement("Candy");

        assertEquals(List.of(SweetFixtures.caramel()), collector.sweets());
    }

    @Test
    void joinsTextReceivedInSeveralChunks() {
        SweetEventCollector collector = new SweetEventCollector();
        collector.startElement("Sweet", "C001");
        CARAMEL.forEach((tag, text) -> {
            if (tag.equals("Name")) {
                collector.startElement(tag, null);
                collector.characters("Sunny ");
                collector.characters("Caramel");
                collector.endElement(tag);
            } else {
                element(collector, tag, text);
            }
        });
        collector.endElement("Sweet");

        assertEquals("Sunny Caramel", collector.sweets().get(0).name());
    }

    @Test
    void doesNotLeakTextBetweenElements() {
        SweetEventCollector collector = new SweetEventCollector();
        collector.startElement("Sweet", "C001");
        collector.characters("leftover");
        CARAMEL.forEach((tag, text) -> element(collector, tag, text));
        collector.endElement("Sweet");

        assertEquals(SweetFixtures.caramel(), collector.sweets().get(0));
    }

    @Test
    void ignoresEventsOutsideSweet() {
        SweetEventCollector collector = new SweetEventCollector();
        collector.startElement("Candy", null);
        collector.characters("text");
        collector.endElement("Candy");

        assertTrue(collector.sweets().isEmpty());
    }

    @Test
    void collectsSweetsInDocumentOrder() {
        SweetEventCollector collector = new SweetEventCollector();
        caramel(collector, "C001");
        caramel(collector, "C002");

        List<Sweet> sweets = collector.sweets();

        assertEquals(2, sweets.size());
        assertEquals("C001", sweets.get(0).id());
        assertEquals("C002", sweets.get(1).id());
    }

    @Test
    void failsWhenSweetIsIncomplete() {
        SweetEventCollector collector = new SweetEventCollector();
        collector.startElement("Sweet", "C001");
        element(collector, "Name", "Only Name");

        assertThrows(CandyException.class,
                () -> collector.endElement("Sweet"));
    }
}