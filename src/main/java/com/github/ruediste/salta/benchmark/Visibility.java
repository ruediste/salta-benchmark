package com.github.ruediste.salta.benchmark;

enum Visibility {
	PROTECTED("protected"), PUBLIC("public"), PACKAGE(""), PRIVATE("private");
	public String keyword;

	private Visibility(String keyword) {
		this.keyword = keyword;
	}
}