package com.github.ruediste.salta.benchmark;

class TreeConfig {
	Visibility visibility;
	Injection injection;

	public TreeConfig(Visibility visibility, Injection injection) {
		super();
		this.visibility = visibility;
		this.injection = injection;
	}

	@Override
	public String toString() {
		return injection.toString() + "_" + visibility.toString();
	}
}