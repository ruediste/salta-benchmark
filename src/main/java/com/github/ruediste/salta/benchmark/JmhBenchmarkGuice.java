package com.github.ruediste.salta.benchmark;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import com.google.inject.Guice;

@State(Scope.Thread)
public class JmhBenchmarkGuice {

	@Param({ "METHOD", "CONSTRUCTOR", "FIELD" })
	Injection injection;

	@Param({ "PUBLIC", "PACKAGE", "PROTECTED", "PRIVATE" })
	Visibility visibility;

	private com.google.inject.Injector guice;

	private Class<?> rootClazz;

	@Setup
	public void setup() throws ClassNotFoundException {
		rootClazz = Class.forName("com.github.ruediste.salta.benchmark.tree."
				+ new TreeConfig(visibility, injection));
		guice = Guice.createInjector();
	}

	@Benchmark
	@OutputTimeUnit(TimeUnit.MILLISECONDS)
	@Measurement(iterations = 3, time = 100, timeUnit = TimeUnit.MILLISECONDS)
	@Warmup(iterations = 2)
	@Fork(1)
	public Object measure() throws Throwable {
		return guice.getInstance(rootClazz);
	}

}
