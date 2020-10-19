package com.lovetropics.installer;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.lovetropics.installer.config.InstallerConfig;

public class LaunchWrapper {

    private static class Arguments {

        @Parameter(names = { "--run" }, description = "Command to execute root installer with")
        private String runCommand = "-jar ltinstaller.jar";

        @Parameter(names = { "--bypass" }, description = "Bypass the wrapper and launch the installer directly")
        private boolean bypass = false;

        @Parameter(names = { "--config" }, description = "Provide an alternate path to a config file, defaults to installer.json")
        private String config = "installer.json";
    }

    public static void main(String[] argv) throws JsonSyntaxException, JsonIOException, InterruptedException, IOException {
        Arguments args = new Arguments();
        JCommander.newBuilder().addObject(args).build().parse(argv);

        File configFile = new File(args.config);
        InstallerConfig config;
        if (!configFile.exists()) {
            System.out.println("Could not find config file at " + configFile.getAbsolutePath() + ", using defaults.");
            config = new InstallerConfig();
        } else {
            config = new Gson().fromJson(new FileReader(configFile), InstallerConfig.class);
        }

        if (args.bypass) {
            Installer.run(config);
            return;
        }

        RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
        // TODO can this be done safely outside dev? not really necessary
        List<String> arguments = new ArrayList<>(/*runtimeMxBean.getInputArguments()*/);

        String classpath = System.getProperty("java.class.path");

        arguments.add(0, "java");
        arguments.add("-cp");
        arguments.add(classpath + ";" + config.forgeInstallerPath);
        arguments.addAll(Arrays.asList(args.runCommand.split("\\s+")));
        arguments.add("--bypass");
        arguments.add("--config");
        arguments.add(args.config);

        new ProcessBuilder(arguments).redirectError(Redirect.INHERIT).redirectOutput(Redirect.INHERIT).start().waitFor();
    }
}
