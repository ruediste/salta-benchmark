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
import com.google.inject.Injector;
import com.google.inject.Module;

@State(Scope.Thread)
public class GuiceThroughput {
	// @Param({ "FIELD" })
	// @Param({ "METHOD" })
	@Param({ "CONSTRUCTOR" })
	// @Param({ "METHOD", "CONSTRUCTOR", "FIELD" })
	Injection injection;

	// @Param({ "PUBLIC" })
	@Param({ "PACKAGE" })
	// @Param({ "PUBLIC", "PACKAGE", "PROTECTED", "PRIVATE" })
	Visibility visibility;

	private com.google.inject.Injector guiceJit;

	private Class<?> rootClazz;

	private Injector guiceBind;

	@Setup
	public void setup() throws Exception {
		rootClazz = Class.forName("com.github.ruediste.salta.benchmark.tree."
				+ new TreeConfig(visibility, injection, false));
		guiceJit = Guice.createInjector();

		Class<?> moduleClass = Class
				.forName("com.github.ruediste.salta.benchmark.tree."
						+ new TreeConfig(visibility, injection, true)
						+ "GuiceBind");

		guiceBind = Guice.createInjector((Module) moduleClass.newInstance());
	}

	@Benchmark
	@OutputTimeUnit(TimeUnit.MILLISECONDS)
	@Measurement(iterations = 10, time = 200, timeUnit = TimeUnit.MILLISECONDS)
	@Warmup(iterations = 5, time = 500, timeUnit = TimeUnit.MILLISECONDS)
	@Fork(1)
	public Object jit() throws Throwable {
		return guiceJit.getInstance(rootClazz);
	}

	@Benchmark
	@OutputTimeUnit(TimeUnit.MILLISECONDS)
	@Measurement(iterations = 10, time = 200, timeUnit = TimeUnit.MILLISECONDS)
	@Warmup(iterations = 5, time = 500, timeUnit = TimeUnit.MILLISECONDS)
	@Fork(1)
	public Object bind() throws Throwable {
		return guiceBind.getInstance(rootClazz);
	}

}
