package com.github.ruediste.salta.benchmark;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Generator for the benchmark classes
 * 
 * <p>
 * <b> Dimensions: </b><br/>
 * <ul>
 * <li>visibility
 * <ul>
 * <li>public</li>
 * <li>protected</li>
 * <li>package</li>
 * <li>private</li>
 * </ul>
 * </li>
 * 
 * <li>injection
 * <ul>
 * <li>constructor</li>
 * <li>field</li>
 * <li>method</li>
 * </ul>
 * </li>
 * 
 * <li>Topography
 * <ul>
 * <li>tree</li>
 * <li>list</li>
 * </ul>
 * </li>
 * 
 * </ul>
 * </p>
 * 
 *
 */
public class Generator {

	public static void main(String... args) throws IOException {

		generateTree();
	}

	private static void generateTree() throws IOException {
		Path target = initDirectory("com.github.ruediste.salta.benchmark.tree");

		generateTree(target, 5, "", new TreeConfig(Visibility.PUBLIC,
				Injection.CONSTRUCTOR));
	}

	enum Visibility {
		PUBLIC("public"), PROTECTED("protected"), PACKAGE(""), PRIVATE(
				"private");
		public String keyword;

		private Visibility(String keyword) {
			this.keyword = keyword;
		}
	}

	enum Injection {
		CONSTRUCTOR, FIELD, METHOD
	}

	static class TreeConfig {
		Visibility visibility;
		Injection injection;

		public TreeConfig(Visibility visibility, Injection injection) {
			super();
			this.visibility = visibility;
			this.injection = injection;
		}

	}

	private static void generateTree(Path target, int depth, String name,
			TreeConfig config) throws IOException {

		if (depth > 0)
			for (int i = 0; i < 5; i++) {
				generateTree(target, depth - 1, name + i, config);
			}

		BufferedWriter writer = Files.newBufferedWriter(
				target.resolve("Node" + name + ".java"),
				Charset.forName("UTF-8"), StandardOpenOption.CREATE);

		writer.append("package com.github.ruediste.salta.benchmark.tree;");
		writer.append("import javax.inject.Inject;");
		writer.append("public class " + name + "{");

		if (depth > 0)
			switch (config.injection) {
			case CONSTRUCTOR:
				writer.append("@Inject");
				writer.append(config.visibility.keyword + " Node" + name + "(");
				for (int i = 0; i < 5; i++) {
					if (i > 0)
						writer.append(",\n");
					writer.append("Node" + name + i + " arg" + i);
				}
				writer.append("){}");
				break;
			case FIELD:
				break;
			case METHOD:
				break;
			default:
				throw new UnsupportedOperationException();

			}
		writer.append("}");

		writer.close();
	}

	private static Path initDirectory(String pckg) {
		Path target = Paths.get("src/generated/java/" + pckg.replace('.', '/'));
		try {
			if (Files.exists(target))
				removeRecursive(target);
			Files.createDirectories(target);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return target;
	}

	public static void removeRecursive(Path path) throws IOException {
		Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file,
					BasicFileAttributes attrs) throws IOException {
				Files.delete(file);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFileFailed(Path file, IOException exc)
					throws IOException {
				// try to delete the file anyway, even if its attributes
				// could not be read, since delete-only access is
				// theoretically possible
				Files.delete(file);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc)
					throws IOException {
				if (exc == null) {
					Files.delete(dir);
					return FileVisitResult.CONTINUE;
				} else {
					// directory iteration failed; propagate exception
					throw exc;
				}
			}
		});
	}
}
