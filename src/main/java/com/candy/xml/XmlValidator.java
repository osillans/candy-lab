package com.candy.xml;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public final class XmlValidator {

    private static final Logger LOGGER =
            Logger.getLogger(XmlValidator.class.getName());

    private final Path schemaFile;

    public XmlValidator(Path schemaFile) {
        this.schemaFile = schemaFile;
    }

    public List<String> validate(Path xmlFile) {
        LOGGER.info("Validating " + xmlFile + " against " + schemaFile);
        List<String> errors = new ArrayList<>();
        try {
            Schema schema = SchemaFactory
                    .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
                    .newSchema(schemaFile.toFile());
            Validator validator = schema.newValidator();
            validator.setErrorHandler(new ErrorCollector(errors));
            validator.validate(new StreamSource(xmlFile.toFile()));
        } catch (SAXException | IOException e) {
            errors.add("Cannot validate: " + e.getMessage());
        }
        if (errors.isEmpty()) {
            LOGGER.info(xmlFile + " is valid");
        }
        errors.forEach(LOGGER::severe);
        return errors;
    }

    private static final class ErrorCollector extends DefaultHandler {

        private final List<String> errors;

        ErrorCollector(List<String> errors) {
            this.errors = errors;
        }

        @Override
        public void error(SAXParseException exception) {
            errors.add("line " + exception.getLineNumber() + ": "
                    + exception.getMessage());
        }
    }
}