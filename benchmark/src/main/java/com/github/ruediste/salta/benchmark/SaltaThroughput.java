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

import com.github.ruediste.salta.core.InjectionStrategy;
import com.github.ruediste.salta.jsr330.AbstractModule;
import com.github.ruediste.salta.jsr330.Injector;
import com.github.ruediste.salta.jsr330.Salta;
import com.github.ruediste.salta.jsr330.SaltaModule;

@State(Scope.Thread)
public class SaltaThroughput {

	// @Param({ "FIELD" })
	// @Param({ "METHOD" })
	@Param({ "CONSTRUCTOR" })
	// @Param({ "METHOD", "CONSTRUCTOR", "FIELD" })
	Injection injection;

	// @Param({ "PUBLIC" })
	@Param({ "PACKAGE" })
	// @Param({ "PUBLIC", "PACKAGE", "PROTECTED", "PRIVATE" })
	Visibility visibility;

	@Param({ "INVOKE_DYNAMIC" })
	// @Param({ "REFLECTION", "INVOKE_DYNAMIC" })
	InjectionStrategy injectionStrategy;

	private Injector saltaJit;

	private Class<?> rootClazz;

	private Injector saltaBind;

	@Setup
	public void setup() throws Exception {
		rootClazz = Class.forName("com.github.ruediste.salta.benchmark.tree."
				+ new TreeConfig(visibility, injection, false));
		saltaJit = Salta.createInjector(new AbstractModule() {

			@Override
			protected void configure() {
				getConfiguration().config.config.injectionStrategy = injectionStrategy;
			}
		});

		Class<?> moduleClass = Class
				.forName("com.github.ruediste.salta.benchmark.tree."
						+ new TreeConfig(visibility, injection, true)
						+ "SaltaBind");

		saltaBind = Salta.createInjector(new AbstractModule() {

			@Override
			protected void configure() {
				getConfiguration().config.config.injectionStrategy = injectionStrategy;
			}
		}, (SaltaModule) moduleClass.newInstance());
	}

	@Benchmark
	@OutputTimeUnit(TimeUnit.MILLISECONDS)
	@Measurement(iterations = 10, time = 300, timeUnit = TimeUnit.MILLISECONDS)
	@Warmup(iterations = 5, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
	@Fork(5)
	public Object jit() throws Throwable {

		return saltaJit.getInstance(rootClazz);
	}

	// @Benchmark
	@OutputTimeUnit(TimeUnit.MILLISECONDS)
	@Measurement(iterations = 10, time = 200, timeUnit = TimeUnit.MILLISECONDS)
	@Warmup(iterations = 5, time = 500, timeUnit = TimeUnit.MILLISECONDS)
	@Fork(1)
	public Object bind() throws Throwable {

		return saltaBind.getInstance(rootClazz);
	}

}
