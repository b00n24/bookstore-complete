package org.books.application.rest.filter;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.xml.sax.SAXException;

/**
 *
 * @author Silvan
 */
@Provider
@PreMatching
public class RESTRequestValidationFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestCtx) throws IOException {
	InputStream entityStream = requestCtx.getEntityStream();

	// Read Stream to String for validation
	String content = IOUtils.toString(entityStream, "UTF-8");

	// Set read Stream again to the Request
	requestCtx.setEntityStream(IOUtils.toInputStream(content, "UTF-8"));
	try {
	    if (StringUtils.trimToNull(content) != null) {
		SchemaValidator.validate(content);
	    }
	} catch (MalformedURLException | SAXException ex) {
	    Logger.getLogger(RESTRequestValidationFilter.class.getName()).log(Level.SEVERE, null, ex);
	    requestCtx.abortWith(Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(ExceptionUtils.getStackTrace(ex))
                    .build());
	}

    }

}
