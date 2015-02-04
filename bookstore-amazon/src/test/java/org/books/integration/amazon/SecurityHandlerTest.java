/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.books.integration.amazon;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import static org.testng.Assert.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author Silvan
 */
public class SecurityHandlerTest {
    
    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
     @Test
     public void hello() throws IOException, SOAPException {
	String plainMessageOriginal = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns=\"http://webservices.amazon.com/AWSECommerceService/2011-08-01\" xmlns:aws=\"http://security.amazonaws.com/doc/2007-01-01/\">\n"
		+ "<soapenv:Header />\n"
		+ "   <soapenv:Body>\n"
		+ "      <ns:ItemSearch>\n"
		+ "      <ns:Request>\n"
		+ "      	<ns:SearchIndex>Books</ns:SearchIndex>\n"
		+ "            <ns:Keywords>Harry%20Potter</ns:Keywords>\n"
		+ "         </ns:Request>\n"
		+ "      </ns:ItemSearch>\n"
		+ "   </soapenv:Body>\n"
		+ "</soapenv:Envelope>";

	String plainMessageErwartet = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns=\"http://webservices.amazon.com/AWSECommerceService/2011-08-01\" xmlns:aws=\"http://security.amazonaws.com/doc/2007-01-01/\">\n"
		+ "<soapenv:Header>\n"
		+ "   <aws:AWSAccessKeyId>AKIAIYFLREOYORYNAQTQ</aws:AWSAccessKeyId>\n"
		+ "   <aws:Timestamp>2015-02-02T16:07:22Z</aws:Timestamp>\n"
		+ "   <aws:Signature>f334/b40XnmcWTjB5ZAXZFnbaYscvslrpRKonvkfkzQ=</aws:Signature>\n"
		+ "</soapenv:Header>\n"
		+ "   <soapenv:Body>\n"
		+ "      <ns:ItemSearch>\n"
		+ "      <ns:AssociateTag>test0e5d-20</ns:AssociateTag>\n"
		+ "      <ns:Request>\n"
		+ "      	<ns:SearchIndex>Books</ns:SearchIndex>\n"
		+ "            <ns:Keywords>Harry%20Potter</ns:Keywords>\n"
		+ "         </ns:Request>\n"
		+ "      </ns:ItemSearch>\n"
		+ "   </soapenv:Body>\n"
		+ "</soapenv:Envelope>";

	MessageFactory messageFactory = MessageFactory.newInstance();
	

	InputStream is = new ByteArrayInputStream(plainMessageOriginal.getBytes());
	SOAPMessage request = messageFactory.createMessage(null, is);
	
	SOAPMessageContext message = new SOAPMessageContext() {

	    private SOAPMessage message;
	    @Override
	    public SOAPMessage getMessage() {
		return this.message;
	    }

	    @Override
	    public void setMessage(SOAPMessage message) {
		this.message = message;
	    }

	    @Override
	    public Object[] getHeaders(QName header, JAXBContext context, boolean allRoles) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	    }

	    @Override
	    public Set<String> getRoles() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	    }

	    @Override
	    public void setScope(String name, MessageContext.Scope scope) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	    }

	    @Override
	    public MessageContext.Scope getScope(String name) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	    }

	    @Override
	    public int size() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	    }

	    @Override
	    public boolean isEmpty() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	    }

	    @Override
	    public boolean containsKey(Object key) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	    }

	    @Override
	    public boolean containsValue(Object value) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	    }

	    @Override
	    public Object get(Object key) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	    }

	    @Override
	    public Object put(String key, Object value) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	    }

	    @Override
	    public Object remove(Object key) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	    }

	    @Override
	    public void putAll(Map<? extends String, ? extends Object> m) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	    }

	    @Override
	    public void clear() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	    }

	    @Override
	    public Set<String> keySet() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	    }

	    @Override
	    public Collection<Object> values() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	    }

	    @Override
	    public Set<Map.Entry<String, Object>> entrySet() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	    }
	};

	message.setMessage(request);
	
	System.out.println(message);
	boolean handleMessage = securityHandler.handleMessage(message);
	assertTrue(handleMessage);
	System.out.println(message);     
     }
    
    private static SecurityHandler securityHandler;

    @BeforeClass
    public static void setUpClass() throws Exception {
	securityHandler = new SecurityHandler();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
}
