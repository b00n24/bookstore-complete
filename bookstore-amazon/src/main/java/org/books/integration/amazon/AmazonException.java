package org.books.integration.amazon;

public class AmazonException extends Exception {

    private final String code;

    public AmazonException(String message, String code) {
	super(message);
	this.code = code;
    }

    public String getCode() {
	return code;
    }
}
