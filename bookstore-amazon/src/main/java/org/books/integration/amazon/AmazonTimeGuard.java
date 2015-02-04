package org.books.integration.amazon;

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

    public boolean isSafe() {
	return (System.currentTimeMillis() - lastStartTime) > MINIMAL_AMAZON_SERVICE_WAITING_TIME;
    }

    public long getSleepingTime() {
	return System.currentTimeMillis() - lastStartTime;
    }
}
