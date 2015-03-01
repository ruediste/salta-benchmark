package com.github.ruediste.salta.benchmark;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import com.github.ruediste.salta.AbstractModule;
import com.github.ruediste.salta.Salta;
import com.github.ruediste.salta.core.InjectionStrategy;
import com.github.ruediste.salta.jsr330.JSR330Module;
import com.github.ruediste.salta.standard.Injector;

@State(Scope.Thread)
public class SaltaStartup {

	// @Param({ "FIELD" })
	@Param({ "METHOD", "CONSTRUCTOR", "FIELD" })
	Injection injection;

	@Param({ "PUBLIC", "PACKAGE", "PROTECTED", "PRIVATE" })
	Visibility visibility;

	@Param({ "INVOKE_DYNAMIC" })
	// @Param({ "REFLECTION", "INVOKE_DYNAMIC" })
	InjectionStrategy injectionStrategy;

	@Benchmark
	@OutputTimeUnit(TimeUnit.MILLISECONDS)
	// @Measurement(iterations = 10, time = 200, timeUnit =
	// TimeUnit.MILLISECONDS)
	// @Warmup(iterations = 5, time = 300, timeUnit = TimeUnit.MILLISECONDS)
	@BenchmarkMode(Mode.SingleShotTime)
	// @Fork(1)
	public Object measure() throws Throwable {
		Class<?> rootClazz = Class
				.forName("com.github.ruediste.salta.benchmark.tree."
						+ new TreeConfig(visibility, injection));
		Injector salta = Salta.createInjector(new AbstractModule() {

			@Override
			protected void configure() {
				getConfiguration().config.injectionStrategy = injectionStrategy;
			}
		}, new JSR330Module());
		return salta.getInstance(rootClazz);
	}

}
