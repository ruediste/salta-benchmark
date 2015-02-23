package com.github.ruediste.salta.benchmark;

enum Visibility {
	PUBLIC("public"), PROTECTED("protected"), PACKAGE(""), PRIVATE(
			"private");
	public String keyword;

	private Visibility(String keyword) {
		this.keyword = keyword;
	}
}