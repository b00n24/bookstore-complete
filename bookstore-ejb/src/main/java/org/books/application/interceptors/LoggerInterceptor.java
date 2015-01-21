package org.books.application.interceptors;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

/**
 * Für Debugging kann dieser Interceptor verwendet werden.
 *
 * @author Silvan
 */
public class LoggerInterceptor {

    @AroundInvoke
    public Object intercept(InvocationContext ic) throws Exception {
	StringBuilder sb = new StringBuilder("\n____________________________________________________");
	sb.append("\nMethode: ");
	sb.append(ic.getMethod());
	sb.append("\nParams:\n");
	for (Object param : ic.getParameters()) {
	    if (param != null) {
		sb.append(param.getClass() + ": ");
		sb.append(param + "\n");
	    }else{
		sb.append("null");
	    }
	}

	sb.append("____________________________________________________");
	Logger.getLogger(LoggerInterceptor.class.getName()).log(Level.INFO, sb.toString());

	return ic.proceed();
    }
}
