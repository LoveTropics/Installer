package com.lovetropics.installer;

import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.google.gson.Gson;
import com.lovetropics.installer.config.InstallerConfig;
import com.lovetropics.installer.util.ThrowingFunction;

public class LaunchWrapper {

    private static class Arguments {

        @Parameter(names = { "--bypass" }, description = "Bypass the wrapper and launch the installer directly")
        private boolean bypass = false;

        @Parameter(names = { "--config" }, description = "Provide an alternate path to a config file, defaults to installer.json")
        private String config = "installer.json";
    }

    public static void main(String[] argv) throws Exception {
        PrintStream out = new PrintStream("ltinstaller.log");
        System.setOut(out);
        System.setErr(out);
        Arguments args = new Arguments();
        JCommander.newBuilder().addObject(args).build().parse(argv);

        File configFile = new File(args.config);
        ThrowingFunction<Class<?>, Object> deserializer;
        if (!configFile.exists()) {
            System.out.println("Could not find config file at " + configFile.getAbsolutePath() + ", using defaults.");
            deserializer = cls -> cls.newInstance();
        } else {
            deserializer = cls -> new Gson().fromJson(new FileReader(configFile), cls);
        }
        InstallerConfig config = (InstallerConfig) deserializer.apply(InstallerConfig.class);

        if (args.bypass) {
            Installer.run(config);
            return;
        }
        if (config.forgeInstallerPath == null) {
            throw new IllegalArgumentException("Missing forgeInstallerPath config");
        }

        String classpath = System.getProperty("java.class.path");
        classpath += ";" + Paths.get(config.forgeInstallerPath).toAbsolutePath();

        System.out.println("Loading installer with modified classpath: " + classpath);
        // Create a new classloader with the appended classpath by splitting the files apart and converting to URLs
        ClassLoader classLoader = new URLClassLoader(Arrays.stream(classpath.split(";"))
                .map(LaunchWrapper::makeURL)
                .filter(Objects::nonNull)
                .toArray(URL[]::new),
            getParentClassloader());

        /*
         * We must remain at arms length to use an alternate classloader.
         * 
         * Passing the reflectively loaded class into Gson seems to work, probably because Gson is itself
         * reflectively initializing the object via the class.
         */
        Class<?> installerClass = Class.forName(Installer.class.getCanonicalName(), true, classLoader);
        Class<?> configClass = Class.forName(InstallerConfig.class.getCanonicalName(), true, classLoader);
        Method run = installerClass.getDeclaredMethod("run", configClass);
        run.invoke(null, deserializer.apply(configClass));
    }

    private static URL makeURL(String s) {
        try {
            return new File(s).toURI().toURL();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // From Forge Installer
    private static boolean clChecked = false;
    private static ClassLoader parentClassLoader = null;

    private static synchronized ClassLoader getParentClassloader() { // Reflectively try and get the platform classloader, done this way to prevent hard dep on J9.
        if (!clChecked) {
            clChecked = true;
            if (!System.getProperty("java.version").startsWith("1.")) { // in 9+ the changed from 1.8 to just 9. So this essentially detects if we're <9
                try {
                    Method getPlatform = ClassLoader.class.getDeclaredMethod("getPlatformClassLoader");
                    parentClassLoader = (ClassLoader) getPlatform.invoke(null);
                } catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    System.out.println("No platform classloader: " + System.getProperty("java.version"));
                }
            }
        }
        return parentClassLoader;
    }
}
