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

import com.github.ruediste.salta.AbstractModule;
import com.github.ruediste.salta.Salta;
import com.github.ruediste.salta.core.InjectionStrategy;
import com.github.ruediste.salta.jsr330.JSR330Module;
import com.github.ruediste.salta.standard.Injector;
import com.github.ruediste.salta.standard.Module;

@State(Scope.Thread)
public class SaltaThroughput {

	// @Param({ "FIELD" })
	// @Param({ "METHOD" })
	@Param({ "METHOD", "CONSTRUCTOR", "FIELD" })
	Injection injection;

	// @Param({ "PUBLIC" })
	@Param({ "PUBLIC", "PACKAGE", "PROTECTED", "PRIVATE" })
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
				getConfiguration().config.injectionStrategy = injectionStrategy;
			}
		}, new JSR330Module());

		Class<?> moduleClass = Class
				.forName("com.github.ruediste.salta.benchmark.tree."
						+ new TreeConfig(visibility, injection, true)
						+ "SaltaBind");

		saltaBind = Salta.createInjector(new AbstractModule() {

			@Override
			protected void configure() {
				getConfiguration().config.injectionStrategy = injectionStrategy;
			}
		}, (Module) moduleClass.newInstance(), new JSR330Module());
	}

	@Benchmark
	@OutputTimeUnit(TimeUnit.MILLISECONDS)
	@Measurement(iterations = 10, time = 200, timeUnit = TimeUnit.MILLISECONDS)
	@Warmup(iterations = 5, time = 500, timeUnit = TimeUnit.MILLISECONDS)
	@Fork(1)
	public Object jit() throws Throwable {

		return saltaJit.getInstance(rootClazz);
	}

	@Benchmark
	@OutputTimeUnit(TimeUnit.MILLISECONDS)
	@Measurement(iterations = 10, time = 200, timeUnit = TimeUnit.MILLISECONDS)
	@Warmup(iterations = 5, time = 500, timeUnit = TimeUnit.MILLISECONDS)
	@Fork(1)
	public Object bind() throws Throwable {

		return saltaBind.getInstance(rootClazz);
	}

}
