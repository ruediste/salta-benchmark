package com.github.ruediste.salta.benchmark;

public enum Visibility {
	PUBLIC("public"), PACKAGE(""), PROTECTED("protected"), PRIVATE("private");
	public String keyword;

	private Visibility(String keyword) {
		this.keyword = keyword;
	}
}