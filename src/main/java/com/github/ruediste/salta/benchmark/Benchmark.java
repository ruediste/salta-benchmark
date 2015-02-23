package com.github.ruediste.salta.benchmark;

import java.util.concurrent.TimeUnit;

import com.github.ruediste.salta.Salta;
import com.github.ruediste.salta.jsr330.JSR330Module;
import com.github.ruediste.salta.standard.Injector;
import com.google.common.base.Stopwatch;
import com.google.inject.Guice;

public class Benchmark {

	public static void main(String... args) throws Exception {
		treeBenchmark();
	}

	private static void treeBenchmark() throws ClassNotFoundException {
		Injector salta = Salta.createInjector(new JSR330Module());
		com.google.inject.Injector guice = Guice.createInjector();

		for (Visibility v : Visibility.values())
			for (Injection i : Injection.values()) {
				TreeConfig config = new TreeConfig(v, i);
				Class<?> rootClass = Class
						.forName("com.github.ruediste.salta.benchmark.tree."
								+ config.toString());
				double saltaSpeed = benchmark(
						"Salta Tree " + config.toString(),
						() -> salta.getInstance(rootClass));
				double guiceSpeed = benchmark(
						"Guice Tree " + config.toString(),
						() -> guice.getInstance(rootClass));
				System.out.printf("%40s: %f\n", "Salta/Guice", saltaSpeed
						/ guiceSpeed);
				System.out.println();
			}
	}

	private static double benchmark(String name, Runnable action) {
		long count = 1;

		// warmup
		action.run();

		// find count
		Stopwatch watch = Stopwatch.createStarted();
		do {
			watch.reset().start();
			count *= 2;
			for (long p = 0; p < count; p++) {
				action.run();
			}
		} while (watch.elapsed(TimeUnit.MILLISECONDS) < 200);

		// run benchmark
		int repetitions = 3;
		double sum = 0;
		for (int i = 0; i < repetitions; i++) {
			watch.reset().start();
			for (long p = 0; p < count; p++) {
				action.run();
			}
			double t = count * 1000.0 / watch.elapsed(TimeUnit.MILLISECONDS);
			sum += t;
			System.out.printf("%40s: %10.3g iterations/s\n", name, t);
		}

		return sum / repetitions;
	}
}
