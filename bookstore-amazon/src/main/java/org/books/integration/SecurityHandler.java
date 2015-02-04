package org.books.integration;

import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

public class SecurityHandler implements SOAPHandler<SOAPMessageContext> {

    @Override
    public Set<QName> getHeaders() {
	return null;
    }

    @Override
    public boolean handleMessage(SOAPMessageContext context) {
	// TODO: Add security header to request message
	return true;
    }

    @Override
    public boolean handleFault(SOAPMessageContext context) {
	return true;
    }

    @Override
    public void close(MessageContext context) {
    }
}
