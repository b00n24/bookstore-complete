package org.books.integration.amazon;

import javax.ejb.Singleton;

@Singleton
public class AmazonTimeGuard {

    /**
     * Time to wait for between requests in milliseconds
     */
    private static final int MINIMAL_AMAZON_SERVICE_WAITING_TIME = 1000;

    private long lastStartTime = 0;

    public void setLastStartTime(long time) {
	this.lastStartTime = time;
    }

    public boolean isSafe(long time) {
	return time - lastStartTime < MINIMAL_AMAZON_SERVICE_WAITING_TIME;
    }

    public long getSleepingTime(long time) {
	return time - lastStartTime;
    }
}
