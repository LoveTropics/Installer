package com.lovetropics.installer.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MinecraftInstallationUtils {

    private enum OS {

        WINDOWS("cmd", "/c"),
        MAC("/bin/bash", "-c"),
        LINUX("/bin/bash", "-c"),
        UNKNOWN,;

        private final String[] exec;

        private OS(String... exec) {
            this.exec = exec;
        }

        Process exec(String exe) {
            List<String> command = new ArrayList<>(Arrays.asList(exec));
            command.add(exe);
            try {
                return new ProcessBuilder(command).inheritIO().start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static String osName;
    private static OS os;

    private static String getOSName() {
        if (osName == null) {
            osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        }
        return osName;
    }

    private static OS getOS() {
        if (os == null) {
            String osName = getOSName();
            if (osName.contains("win") && System.getenv("APPDATA") != null) {
                os = OS.WINDOWS;
            } else if (osName.contains("mac")) {
                os = OS.MAC;
            } else {
                os = OS.LINUX;
            }
        }
        return os;
    }

    public static File getMCDir() {
        String userHomeDir = System.getProperty("user.home", ".");
        String mcDir = ".minecraft";
        switch (getOS()) {
            case WINDOWS:
                return new File(System.getenv("APPDATA"), mcDir);
            case MAC:
                new File(new File(new File(userHomeDir, "Library"), "Application Support"), "minecraft");
            case LINUX:
                return new File(userHomeDir, mcDir);
            default:
                throw new IllegalStateException("Unknown OS: " + getOSName());
        }
    }

    private static final String WINDOWS_LAUNCHER_LOC = "C:\\ProgramData\\Microsoft\\Windows\\Start Menu\\Programs\\Minecraft Launcher\\Minecraft Launcher.lnk";

    public static Process runLauncher() {
        switch (getOS()) {
            case WINDOWS:
                return OS.WINDOWS.exec(WINDOWS_LAUNCHER_LOC);
            default:
                throw new UnsupportedOperationException("Only windows support for now");
        }
    }
}
