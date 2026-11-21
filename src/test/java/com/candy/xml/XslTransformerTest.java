package com.candy.xml;

import static com.candy.support.XmlSamples.CARAMEL;
import static com.candy.support.XmlSamples.document;
import static com.candy.support.XmlSamples.write;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.candy.exception.CandyException;
import com.candy.support.CapturingHandler;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilderFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class XslTransformerTest {

    private static final Path SAMPLE = Path.of("data", "candy.xml");
    private static final Path STYLESHEET =
            Path.of("data", "candy-grouped.xsl");

    private final Logger logger =
            Logger.getLogger(XslTransformer.class.getName());

    @TempDir
    Path tempDir;

    private CapturingHandler handler;
    private XslTransformer transformer;

    @BeforeEach
    void setUp() {
        handler = CapturingHandler.attachTo(logger);
        transformer = new XslTransformer(STYLESHEET);
    }

    @AfterEach
    void tearDown() {
        handler.detach();
    }

    private static Element root(Path file) throws Exception {
        Document document = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder().parse(file.toFile());
        return document.getDocumentElement();
    }

    private static List<Element> children(Element parent) {
        List<Element> result = new ArrayList<>();
        NodeList nodes = parent.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node instanceof Element element) {
                result.add(element);
            }
        }
        return result;
    }

    private static List<String> names(List<Element> elements) {
        return elements.stream().map(Element::getTagName).toList();
    }

    private static List<String> sweetNames(Element group) {
        return children(group).stream()
                .map(sweet -> sweet.getElementsByTagName("Name").item(0)
                        .getTextContent())
                .toList();
    }

    @Test
    void groupsSweetsByTypeUnderCandyRoot() throws Exception {
        Path output = tempDir.resolve("grouped.xml");

        transformer.transform(SAMPLE, output);

        Element candy = root(output);
        assertEquals("Candy", candy.getTagName());
        assertEquals(List.of("Caramel", "ChocolateFilled",
                "ChocolatePlain", "Toffee"), names(children(candy)));
    }

    @Test
    void putsEverySweetIntoExactlyOneGroup() throws Exception {
        Path output = tempDir.resolve("grouped.xml");

        transformer.transform(SAMPLE, output);

        int total = 0;
        for (Element group : children(root(output))) {
            assertEquals(2, children(group).size());
            total += children(group).size();
        }
        assertEquals(8, total);
        assertEquals(8, root(output).getElementsByTagName("Sweet")
                .getLength());
    }

    @Test
    void sortsSweetsInsideGroupsByName() throws Exception {
        Path output = tempDir.resolve("grouped.xml");

        transformer.transform(SAMPLE, output);

        List<Element> groups = children(root(output));
        assertEquals(List.of("Lemon Drop", "Sunny Caramel"),
                sweetNames(groups.get(0)));
        assertEquals(List.of("Dark Truffle", "Hazelnut Delight"),
                sweetNames(groups.get(1)));
        assertEquals(List.of("Pure Milk Chocolate", "White Pearl"),
                sweetNames(groups.get(2)));
        assertEquals(List.of("Cream Toffee", "Milk Toffee"),
                sweetNames(groups.get(3)));
    }

    @Test
    void keepsSweetAttributesAndContent() throws Exception {
        Path output = tempDir.resolve("grouped.xml");

        transformer.transform(SAMPLE, output);

        Element sweet = children(children(root(output)).get(0)).get(1);
        assertEquals("C001", sweet.getAttribute("id"));
        assertEquals("390", sweet.getElementsByTagName("Energy").item(0)
                .getTextContent());
    }

    @Test
    void createsOnlyGroupsThatExist() throws Exception {
        Path input = write(tempDir, "one.xml", document(CARAMEL));
        Path output = tempDir.resolve("one-grouped.xml");

        transformer.transform(input, output);

        assertEquals(List.of("Caramel"), names(children(root(output))));
    }

    @Test
    void createsMissingOutputDirectories() throws Exception {
        Path output = tempDir.resolve("a").resolve("b").resolve("out.xml");

        transformer.transform(SAMPLE, output);

        assertTrue(Files.exists(output));
    }

    @Test
    void replacesExistingOutput() throws Exception {
        Path output = write(tempDir, "out.xml", "old content");

        transformer.transform(SAMPLE, output);

        assertEquals("Candy", root(output).getTagName());
    }

    @Test
    void logsStartAndResult() {
        Path output = tempDir.resolve("grouped.xml");

        transformer.transform(SAMPLE, output);

        assertTrue(handler.contains(Level.INFO, "Transforming"));
        assertTrue(handler.contains(Level.INFO, "Result written to"));
    }

    @Test
    void rejectsBrokenStylesheet() throws IOException {
        Path broken = write(tempDir, "broken.xsl", "this is not xml");
        XslTransformer failing = new XslTransformer(broken);
        Path output = tempDir.resolve("out.xml");

        CandyException error = assertThrows(CandyException.class,
                () -> failing.transform(SAMPLE, output));

        assertTrue(error.getMessage()
                .startsWith("XSL transformation failed"));
    }

    @Test
    void rejectsMissingStylesheet() {
        XslTransformer failing =
                new XslTransformer(tempDir.resolve("none.xsl"));
        Path output = tempDir.resolve("out.xml");

        assertThrows(CandyException.class,
                () -> failing.transform(SAMPLE, output));
    }

    @Test
    void rejectsMissingInput() {
        Path output = tempDir.resolve("out.xml");

        assertThrows(CandyException.class, () -> transformer
                .transform(tempDir.resolve("none.xml"), output));
    }

    @Test
    void rejectsMalformedInput() throws IOException {
        Path input = write(tempDir, "bad.xml", "<Candy>");
        Path output = tempDir.resolve("out.xml");

        assertThrows(CandyException.class,
                () -> transformer.transform(input, output));
    }

    @Test
    void rejectsOutputInsideARegularFile() throws IOException {
        Path blocker = write(tempDir, "blocker", "file");
        Path output = blocker.resolve("out.xml");

        assertThrows(CandyException.class,
                () -> transformer.transform(SAMPLE, output));
    }
}