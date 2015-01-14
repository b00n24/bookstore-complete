package org.books.persistence;

import java.io.Serializable;
import java.util.Objects;

public class IdentifiableObject implements Serializable {

	private Integer id;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public boolean equals(Object other) {
		return other != null && other instanceof IdentifiableObject && Objects.equals(id, ((IdentifiableObject) other).id);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "{" + "id=" + id + '}';
	}
}
