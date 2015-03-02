package com.github.ruediste.salta.benchmark;

class TreeConfig {
	Visibility visibility;
	Injection injection;
	public boolean interfaces;

	public TreeConfig(Visibility visibility, Injection injection,
			boolean interfaces) {
		super();
		this.visibility = visibility;
		this.injection = injection;
		this.interfaces = interfaces;
	}

	@Override
	public String toString() {
		return (interfaces ? "I_" : "") + injection.toString() + "_"
				+ visibility.toString();
	}
}