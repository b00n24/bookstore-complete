package org.books.persistence;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;

public class ValueObject implements Serializable {

	@Override
	public int hashCode() {
		return Arrays.hashCode(serialize());
	}

	@Override
	public boolean equals(Object other) {
		return other != null && other instanceof ValueObject
				&& Arrays.equals(((ValueObject) other).serialize(), serialize());
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "{hash=" + hashCode() + "}";
	}

	private byte[] serialize() {
		try {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			new ObjectOutputStream(stream).writeObject(this);
			return stream.toByteArray();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
}
