package com.candy.support;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class XmlSamples {

    public static final String CARAMEL = """
            <Sweet id="C001">
                <Name>Sunny Caramel</Name>
                <Energy unit="kcal">390</Energy>
                <Type>Caramel</Type>
                <Ingredients>
                    <Water unit="ml">8</Water>
                    <Sugar unit="mg">62000</Sugar>
                    <Fructose unit="mg">9000</Fructose>
                    <Vanillin unit="mg">12</Vanillin>
                </Ingredients>
                <Value>
                    <Proteins unit="g">0.0</Proteins>
                    <Fats unit="g">0.1</Fats>
                    <Carbohydrates unit="g">97.5</Carbohydrates>
                </Value>
                <Production>Kyiv Confectionery</Production>
            </Sweet>
            """;

    public static final String CHOCOLATE = """
            <Sweet id="C003">
                <Name>Dark Truffle</Name>
                <Energy unit="kcal">550</Energy>
                <Type>ChocolateFilled</Type>
                <Ingredients>
                    <Water unit="ml">2</Water>
                    <Sugar unit="mg">38000</Sugar>
                    <Fructose unit="mg">1500</Fructose>
                    <ChocolateType>Dark</ChocolateType>
                    <Vanillin unit="mg">35</Vanillin>
                </Ingredients>
                <Value>
                    <Proteins unit="g">5.5</Proteins>
                    <Fats unit="g">35.0</Fats>
                    <Carbohydrates unit="g">52.0</Carbohydrates>
                </Value>
                <Production>Lviv Sweets</Production>
            </Sweet>
            """;

    private XmlSamples() {
    }

    public static String document(String... sweets) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<Candy>\n"
                + String.join("", sweets) + "</Candy>\n";
    }

    public static String replace(String text, String from, String to) {
        if (!text.contains(from)) {
            throw new IllegalArgumentException(
                    "Sample does not contain: " + from);
        }
        return text.replace(from, to);
    }

    public static String without(String text, String tag) {
        String result = text.replaceAll(
                "\\s*<" + tag + "[^>]*>[^<]*</" + tag + ">", "");
        if (result.equals(text)) {
            throw new IllegalArgumentException(
                    "Sample does not contain tag: " + tag);
        }
        return result;
    }

    public static Path write(Path directory, String fileName, String content)
            throws IOException {
        return Files.writeString(directory.resolve(fileName), content);
    }
}