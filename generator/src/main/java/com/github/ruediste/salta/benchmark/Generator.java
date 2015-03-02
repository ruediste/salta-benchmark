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

		for (Visibility v : Visibility.values())
			for (Injection i : Injection.values())
			// Visibility v = Visibility.PUBLIC;
			// Injection i = Injection.METHOD;
			{
				int depth = 3;
				int childCount = 10;
				TreeConfig config = new TreeConfig(v, i, false);
				generateTree(target, depth, childCount, config.toString(),
						config);

				config = new TreeConfig(v, i, true);
				generateTree(target, depth, childCount, config.toString(),
						config);

				// generate modules
				generateTreeModules(target, depth, childCount, config);
			}
	}

	private static void generateTreeModules(Path target, int depth,
			int childCount, TreeConfig config) throws IOException {
		writeBindingModule(target, depth, childCount, config, config
				+ "GuiceBind", "import com.google.inject.AbstractModule;");
		writeBindingModule(target, depth, childCount, config, config
				+ "SaltaBind",
				"import com.github.ruediste.salta.AbstractModule;");
	}

	protected static void writeBindingModule(Path target, int depth,
			int childCount, TreeConfig config, String name, String imports)
			throws IOException {
		BufferedWriter writer = Files.newBufferedWriter(
				target.resolve(name + ".java"), Charset.forName("UTF-8"),
				StandardOpenOption.CREATE);

		writer.append("package com.github.ruediste.salta.benchmark.tree;\n");
		writer.append(imports + "\n");
		writer.append("public class " + name + " extends AbstractModule {");
		writer.append("@Override\n" + "protected void configure() {\n");
		writeBindings(writer, depth, childCount, config.toString());
		writer.append("}}");
		writer.close();
	}

	private static void writeBindings(BufferedWriter writer, int depth,
			int childCount, String name) throws IOException {

		writer.append("bind(" + name + ".class).to(" + name + "Impl.class);\n");
		if (depth > 0)
			for (int i = 0; i < childCount; i++) {
				writeBindings(writer, depth - 1, childCount, name + i);
			}
	}

	private static void generateTree(Path target, int depth, int childCount,
			String name, TreeConfig config) throws IOException {

		if (depth > 0)
			for (int i = 0; i < childCount; i++) {
				generateTree(target, depth - 1, childCount, name + i, config);
			}

		String implName = name + (config.interfaces ? "Impl" : "");

		BufferedWriter writer = Files.newBufferedWriter(
				target.resolve(implName + ".java"), Charset.forName("UTF-8"),
				StandardOpenOption.CREATE);

		writer.append("package com.github.ruediste.salta.benchmark.tree;\n");
		writer.append("public class " + implName
				+ (config.interfaces ? " implements " + name : "") + " {");

		if (depth > 0)
			switch (config.injection) {
			case CONSTRUCTOR:
				for (int i = 0; i < childCount; i++) {
					writer.append(config.visibility.keyword + " " + name + i
							+ " field" + i + ";\n");
				}

				writer.append("@javax.inject.Inject\n");
				writer.append(config.visibility.keyword + " " + implName + "(");
				for (int i = 0; i < childCount; i++) {
					if (i > 0)
						writer.append(",\n");
					writer.append(name + i + " arg" + i);
				}
				writer.append("){\n");
				for (int i = 0; i < childCount; i++) {
					writer.append("field" + i + "=arg" + i + ";\n");
				}
				writer.append("}\n");
				break;
			case FIELD:
				for (int i = 0; i < childCount; i++) {
					writer.append("@javax.inject.Inject\n");
					writer.append(config.visibility.keyword + " " + name + i
							+ " field" + i + ";\n");
				}
				break;
			case METHOD:
				for (int i = 0; i < childCount; i++) {
					writer.append(config.visibility.keyword + " " + name + i
							+ " field" + i + ";\n");
					writer.append("@javax.inject.Inject\n");
					writer.append(config.visibility.keyword + " void method"
							+ i + "(" + name + i + " arg){\n" + "field" + i
							+ "=arg;}\n");
				}
				break;
			default:
				throw new UnsupportedOperationException();

			}
		writer.append("}");

		writer.close();

		if (config.interfaces) {
			writer = Files.newBufferedWriter(target.resolve(name + ".java"),
					Charset.forName("UTF-8"), StandardOpenOption.CREATE);

			writer.append("package com.github.ruediste.salta.benchmark.tree;\n");
			writer.append("public interface " + name + " {}\n");
			writer.close();
		}
	}

	private static Path initDirectory(String pckg) {
		Path target = Paths.get("../generated/src/main/java/"
				+ pckg.replace('.', '/'));
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
