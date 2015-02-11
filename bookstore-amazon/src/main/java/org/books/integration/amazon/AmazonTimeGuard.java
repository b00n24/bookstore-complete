package org.books.integration.amazon;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;

@Singleton
public class AmazonTimeGuard {

    /**
     * Time to wait for between requests in milliseconds
     */
    private static final int MINIMAL_AMAZON_SERVICE_WAITING_TIME = 1000;

    private long lastStartTime = 0;

    public void setLastStartTime() {
	this.lastStartTime = System.currentTimeMillis();
    }

//    @Lock(LockType.WRITE)
    public boolean isSafe() {
	return (System.currentTimeMillis() - lastStartTime) > MINIMAL_AMAZON_SERVICE_WAITING_TIME;
    }
    
//    @Lock(LockType.WRITE)
//    public boolean isSafe() {
//	if (getSleepingTime() < MINIMAL_AMAZON_SERVICE_WAITING_TIME) {
//	    try {
//		Thread.sleep(getSleepingTime());
//	    } catch (InterruptedException ex) {
//		Logger.getLogger(AmazonCatalog.class.getName()).log(Level.SEVERE, "Could not sleep", ex);
//	    }
//	}
//	return true;
//    }

//    @Lock(LockType.READ)
    public long getSleepingTime() {
	return System.currentTimeMillis() - lastStartTime;
    }
}
