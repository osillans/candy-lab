# Candy Lab

Console application that reads a candy catalogue from an XML file (variant 9).

## Features

- XML data file `data/candy.xml` and XSD schema `data/candy.xsd`.
- Three parsers that fill the same collection of `Sweet` objects: DOM, SAX
  and StAX.
- Sorting with the `Comparator` interface: by name, by energy, by producer.
- Validation of the XML file against the XSD schema.
- XSL transformation that splits the flat list of sweets into groups by type.
- All runtime messages go through `java.util.logging`.

## Requirements

- JDK 21
- Maven 3.9 or newer

## Project structure

```
com.candy.model      Sweet, Ingredients, NutritionValue, CandyType,
                     ChocolateKind, XmlEnum, Validation
com.candy.sort       SweetOrder (Comparator<Sweet>)
com.candy.parser     CandyParser, AbstractCandyParser, DomCandyParser,
                     SaxCandyParser, StaxCandyParser, SweetBuilder,
                     SweetEventCollector, XmlTags
com.candy.xml        XmlValidator, XslTransformer, LoggingErrorListener
com.candy.logging    LoggingSetup, LogFormatter
com.candy.exception  CandyException
com.candy.app        Main, CandyApplication, AppFiles
```

## XML format

The root element is `Candy`. It contains one or more `Sweet` elements:

```
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
```

- `id` is a required attribute of type `xs:ID` and looks like `C001`.
- `Type` is one of `Caramel`, `Toffee`, `ChocolateFilled`,
  `ChocolatePlain`.
- `ChocolateType` (`Dark`, `Milk`, `White`) is present only for chocolate
  sweets. The schema cannot express this rule, so `Sweet` checks it.
- Every value has a `unit` attribute that is required and fixed by the
  schema.
- All values are given per 100 g of candy.

## Logging

`LoggingSetup` configures the `com.candy` logger: INFO and above go to the
console, FINE and above go to `logs/candy.log`.

## Build, test and run

```
mvn test
mvn -q compile
java -cp target/classes com.candy.app.Main
```

The grouped XML is written to `output/candy-grouped.xml`.

Open `target/site/jacoco/index.html` after `mvn test` to see the coverage
report.