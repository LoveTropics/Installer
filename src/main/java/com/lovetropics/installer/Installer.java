package com.lovetropics.installer;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.lovetropics.installer.steps.InstallStep;
import com.lovetropics.installer.ui.InstallerGui;

public class Installer {

    private static class Arguments {

        @Parameter(names = { "--config" }, description = "Provide an alternate path to a config file, defaults to installer.json")
        private String config = "installer.json";
    }

    public static void main(String[] argv) {
        Arguments args = new Arguments();
        JCommander.newBuilder().addObject(args).build().parse(argv);

        InstallProcess process = new InstallProcess().then(new InstallStep() {

            int progress;

            @Override
            public Future<Void> start(ProgressCallback callback) {
                return CompletableFuture.runAsync(() -> {
                    while (progress < getMaxProgress()) {
                        try {
                            Thread.sleep(new Random().nextInt(500) + 500);
                            if (new Random().nextInt(4) == 0) {
                                callback.push("Doing a Smaller Thing");
                                Thread.sleep(1000);
                                callback.pop();
                            }
                            progress++;
                            callback.addProgress(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public String getName() {
                return "Doing Things";
            }

            @Override
            public int getMaxProgress() {
                return 20;
            }
        });

        InstallerGui.create(process::run);
    }
}
