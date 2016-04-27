package org.telegram.structure;

public enum EventPreiod {
	TODAY("today"), SEVEN_DAYS("week"), LATER("month");

	private String value;

	EventPreiod(final String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return this.getValue();
	}
}