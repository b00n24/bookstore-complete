package org.books.integration.amazon;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SecurityHandler implements SOAPHandler<SOAPMessageContext> {

    private static final String ASSOCIATE_TAG_VALUE = "test0e5d-20";
    private static final String ACCESS_KEY = "AKIAIYFLREOYORYNAQTQ";
    private static final String SECRET_KEY = "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";
    private static final String TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final String MAC_ALGORITHM = "HmacSHA256";

    // Elements
    private static final String ASSOCIATE_TAG = "AssociateTag";
    private static final String SIGNATURE = "Signature";
    private static final String TIMESTAMP = "Timestamp";
    private static final String AWS_ACCESS_KEY_ID = "AWSAccessKeyId";

    // Namespace
    private static final String SECURITY_NS = "aws";
    private static final String SECURITY_NS_URL = "http://security.amazonaws.com/doc/2007-01-01/";

    @Override
    public Set<QName> getHeaders() {
	return null;
    }
    
    @Override
    public boolean handleMessage(SOAPMessageContext context) {
	try {

	    // -------------------------------------------
	    // Calculate data
	    // -------------------------------------------	    
	    final SOAPBody soapBody = context.getMessage().getSOAPBody();

	    Logger.getLogger(SecurityHandler.class.getName()).log(Level.SEVERE, "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX=================XXXXXXXXXX");
	    Logger.getLogger(SecurityHandler.class.getName()).log(Level.SEVERE, context.getMessage().toString());
	    Logger.getLogger(SecurityHandler.class.getName()).log(Level.SEVERE, soapBody.toString());
	    Logger.getLogger(SecurityHandler.class.getName()).log(Level.SEVERE, soapBody.getChildElements().toString());
	    Logger.getLogger(SecurityHandler.class.getName()).log(Level.SEVERE, soapBody.getChildElements().next().toString());
	    Logger.getLogger(SecurityHandler.class.getName()).log(Level.SEVERE, soapBody.getChildElements().next().toString());
	    Logger.getLogger(SecurityHandler.class.getName()).log(Level.SEVERE, soapBody.getFirstChild().toString());

	    NodeList childNodes = soapBody.getChildNodes();
	    Node item = childNodes.item(0); 
	    String method = item.getNodeName();
	    
	    
//	    SOAPElement methodElement = (SOAPElement) soapBody.getChildElements().next();
//	    String method = methodElement.getTagName();
	    
	    DateFormat dateFormat = new SimpleDateFormat(TIMESTAMP_FORMAT);
	    String timestamp = dateFormat.format(Calendar.getInstance().getTime());

	    Mac mac = Mac.getInstance(MAC_ALGORITHM);
	    SecretKey key = new SecretKeySpec(SECRET_KEY.getBytes(), MAC_ALGORITHM);
	    mac.init(key);
	    byte[] data = mac.doFinal((method + timestamp).getBytes());
	    String signature = DatatypeConverter.printBase64Binary(data);

	    // -------------------------------------------
	    // manipulate Header
	    // -------------------------------------------
	    final SOAPHeader header = context.getMessage().getSOAPHeader();
	    header.addNamespaceDeclaration(SECURITY_NS, SECURITY_NS_URL);

	    // AWSAccessKeyId
	    SOAPElement accessKeyIdElement = header.addChildElement(AWS_ACCESS_KEY_ID, SECURITY_NS);
	    accessKeyIdElement.setValue(ACCESS_KEY);

	    // Timestamp
	    SOAPElement timestampElement = header.addChildElement(TIMESTAMP, SECURITY_NS);
	    timestampElement.setValue(timestamp);

	    // Signature
	    SOAPElement signatureElement = header.addChildElement(SIGNATURE, SECURITY_NS);
	    signatureElement.setValue(signature);

	    // -------------------------------------------
	    // manipulate Body
	    // -------------------------------------------	
	    // AssociateTag
//	    SOAPElement associateTagElement = methodElement.addChildElement(ASSOCIATE_TAG);
//	    associateTagElement.setValue(ASSOCIATE_TAG_VALUE);

	    return true;
	} catch (NoSuchAlgorithmException | InvalidKeyException | SOAPException ex) {
	    Logger.getLogger(SecurityHandler.class.getName()).log(Level.SEVERE, null, ex);
	}
	return false;
    }

    @Override
    public boolean handleFault(SOAPMessageContext context) {
	return true;
    }

    @Override
    public void close(MessageContext context) {
    }
}
