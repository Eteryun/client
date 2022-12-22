package com.eteryun.launch.agent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class Agent {
    /**
     * The agents launch instrumentation.
     */
    private static Instrumentation LAUNCH_INSTRUMENTATION = null;

    public static void addTransformer(final ClassFileTransformer transformer) {
        if (LAUNCH_INSTRUMENTATION != null) LAUNCH_INSTRUMENTATION.addTransformer(transformer);
    }

    /**
     * Adds a the specified JAR file to the system class loader.
     *
     * @param path The path to the JAR file
     * @throws IOException If the target cannot be added
     */
    public static void addJar(final Path path) throws IOException {
        final File file = path.toFile();
        if (!file.exists()) throw new FileNotFoundException(file.getAbsolutePath());
        if (file.isDirectory() || !file.getName().endsWith(".jar")) throw new IOException("Provided path is not a jar file: " + path);
        if (LAUNCH_INSTRUMENTATION != null) {
            LAUNCH_INSTRUMENTATION.appendToSystemClassLoaderSearch(new JarFile(file));
            return;
        }
        throw new IllegalStateException("Unable to addUrl for '" + path + "'.");
    }

    /**
     * The agent premain is called by the JRE.
     *
     * @param agentArgs The agent arguments
     * @param instrumentation The instrumentation
     */
    public static void premain(final String agentArgs, final Instrumentation instrumentation) {
        agentmain(agentArgs, instrumentation);
    }

    /**
     * The agent main is called by the JRE.
     *
     * <p>You should launch the agent in premain!</p>
     *
     * @param agentArgs The agent arguments
     * @param instrumentation The instrumentation
     */
    public static void agentmain(final String agentArgs, final Instrumentation instrumentation) {
        if (LAUNCH_INSTRUMENTATION == null) LAUNCH_INSTRUMENTATION = instrumentation;
        if (LAUNCH_INSTRUMENTATION == null) throw new NullPointerException("instrumentation");
    }

    public static void updateSecurity() {
        if (LAUNCH_INSTRUMENTATION == null) return;
        final Set<Module> systemUnnamed = Set.of(ClassLoader.getSystemClassLoader().getUnnamedModule());
        Agent.LAUNCH_INSTRUMENTATION.redefineModule(
                Manifest.class.getModule(),
                Set.of(),
                Map.of("sun.security.util", systemUnnamed), // ModLauncher
                Map.of(
                        // ModLauncher -- needs Manifest.jv, and various JarVerifier methods
                        "java.util.jar", systemUnnamed
                ),
                Set.of(),
                Map.of()
        );
    }

    private Agent() {}
}
