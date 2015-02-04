package org.books.integration.amazon;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Set;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

public class LoggingHandler implements SOAPHandler<SOAPMessageContext> {

    private static final Logger logger = Logger.getLogger(LoggingHandler.class.getName());

    @Override
    public Set<QName> getHeaders() {
	return null;
    }

    @Override
    public boolean handleMessage(SOAPMessageContext context) {
	log(context);
	return true;
    }

    @Override
    public boolean handleFault(SOAPMessageContext context) {
	log(context);
	return true;
    }

    @Override
    public void close(MessageContext context) {
    }

    private void log(SOAPMessageContext context) {
	boolean outbound = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
	if (!outbound) {
	    return;
	}
	try {
	    StringWriter buffer = new StringWriter();
	    PrintWriter writer = new PrintWriter(buffer);
	    Transformer transformer = TransformerFactory.newInstance().newTransformer();
	    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
	    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
	    transformer.transform(new DOMSource(context.getMessage().getSOAPPart()), new StreamResult(writer));
	    logger.info(buffer.toString());
	} catch (Exception ex) {
	}
    }
}
