package com.github.ruediste.salta.benchmark;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.util.ClassUtils;

/**
 * Main program entry point
 */
public class Main {

	static class Key {
		String label;
		String injection;
		String visibility;

		@Override
		public int hashCode() {
			return label.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			Key other = (Key) obj;
			return label.equals(other.label)
					&& injection.equals(other.injection)
					&& visibility.equals(other.visibility);
		}
	}

	static class Entry {
		RunResult salta;
		RunResult guice;
	}

	public static void main(String[] argv) throws RunnerException, IOException {
		OptionsBuilder options = new OptionsBuilder();
		options.include(SaltaThroughput.class.getSimpleName());
		options.include(GuiceThroughput.class.getSimpleName());
		options.include(SaltaStartup.class.getSimpleName());
		options.include(GuiceStartup.class.getSimpleName());
		Runner runner = new Runner(options);
		Collection<RunResult> result;
		try {
			result = runner.run();
		} catch (Throwable t) {
			System.err.print("ERROR: ");
			t.printStackTrace(System.err);
			System.exit(1);
			throw t;
		}

		ArrayList<String> classNames = new ArrayList<String>();
		for (RunResult r : result) {
			classNames.add(r.getParams().getBenchmark());
		}
		Map<String, String> denseClassNames = ClassUtils
				.denseClassNames(classNames);

		HashMap<Key, Entry> resultMap = new HashMap<>();
		for (RunResult r : result) {
			String label = denseClassNames.get(r.getParams().getBenchmark());
			boolean isSalta = false;
			if (label.startsWith("Salta")) {
				isSalta = true;
				label = label.substring("Salta".length());
			} else {
				label = label.substring("Guice".length());
			}
			Key key = new Key();
			key.label = label;
			key.injection = r.getParams().getParam("injection");
			key.visibility = r.getParams().getParam("visibility");

			Entry entry = resultMap.get(key);
			if (entry == null) {
				entry = new Entry();
				resultMap.put(key, entry);
			}
			if (isSalta)
				entry.salta = r;
			else
				entry.guice = r;

		}

		System.out.printf("|%20s|%20s|%20s|%20s|\n", "Label", "Visibility",
				"Injection", "Speedup");
		System.out.printf("|----|----|----|----|\n");
		for (java.util.Map.Entry<Key, Entry> entry : resultMap.entrySet()) {
			try {
				System.out.printf("|%20s", entry.getKey().label);
				System.out.printf("|%20s", entry.getKey().visibility);
				System.out.printf("|%20s", entry.getKey().injection);
				double salta = entry.getValue().salta.getPrimaryResult()
						.getStatistics().getMean();
				// System.out.printf("|%20.2f", salta);
				double guice = entry.getValue().guice.getPrimaryResult()
						.getStatistics().getMean();
				// System.out.printf("|%20.2f", guice);
				if (entry.getValue().salta.getParams().getMode() == Mode.SingleShotTime)
					System.out.printf("|%20.2f|", guice / salta);
				else
					System.out.printf("|%20.2f|", salta / guice);
				System.out.println();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}
}
