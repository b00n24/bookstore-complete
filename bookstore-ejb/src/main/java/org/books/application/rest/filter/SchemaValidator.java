/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.books.application.rest.filter;

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.xml.sax.SAXException;

/**
 *
 * @author Silvan
 */
public class SchemaValidator {

    private static final String SCHEMA_LOCATION = "http://www.sws.bfh.ch/~fischli/courses/eadj/jws/project/schemas/orders.xsd";

    public static void validate(String xml) throws MalformedURLException, SAXException, IOException {
	StreamSource xmlFile = new StreamSource(new StringReader(xml));
	SchemaFactory schemaFactory = SchemaFactory
		.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

	Schema schema = schemaFactory.newSchema(new URL(SCHEMA_LOCATION));
	Validator validator = schema.newValidator();
	validator.validate(xmlFile);
    }
}
