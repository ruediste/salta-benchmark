package com.github.ruediste.salta.benchmark;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

@State(Scope.Thread)
public class GuiceStartup {

	@Param({ "FIELD" })
	// @Param({ "METHOD", "CONSTRUCTOR", "FIELD" })
	Injection injection;

	@Param({ "PUBLIC" })
	// @Param({ "PUBLIC", "PACKAGE", "PROTECTED", "PRIVATE" })
	Visibility visibility;

	@Benchmark
	@OutputTimeUnit(TimeUnit.MILLISECONDS)
	// @Measurement(iterations = 10, time = 200, timeUnit =
	// TimeUnit.MILLISECONDS)
	// @Warmup(iterations = 5, time = 300, timeUnit = TimeUnit.MILLISECONDS)
	// @Fork(1)
	@BenchmarkMode(Mode.SingleShotTime)
	public Object jit() throws Throwable {
		Class<?> rootClazz = Class
				.forName("com.github.ruediste.salta.benchmark.tree."
						+ new TreeConfig(visibility, injection, false));
		return Guice.createInjector().getInstance(rootClazz);
	}

	@Benchmark
	@OutputTimeUnit(TimeUnit.MILLISECONDS)
	// @Measurement(iterations = 10, time = 200, timeUnit =
	// TimeUnit.MILLISECONDS)
	// @Warmup(iterations = 5, time = 300, timeUnit = TimeUnit.MILLISECONDS)
	// @Fork(1)
	@BenchmarkMode(Mode.SingleShotTime)
	public Object bind() throws Throwable {
		Class<?> rootClazz = Class
				.forName("com.github.ruediste.salta.benchmark.tree."
						+ new TreeConfig(visibility, injection, true));

		Class<?> moduleClass = Class
				.forName("com.github.ruediste.salta.benchmark.tree."
						+ new TreeConfig(visibility, injection, true)
						+ "GuiceBind");

		Injector guice = Guice.createInjector((Module) moduleClass
				.newInstance());
		return guice.getInstance(rootClazz);
	}
}
