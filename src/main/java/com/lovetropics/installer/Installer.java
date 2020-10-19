package com.lovetropics.installer;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.lovetropics.installer.config.InstallerConfig;
import com.lovetropics.installer.steps.ForgeInstallerStep;
import com.lovetropics.installer.steps.InstallStep;
import com.lovetropics.installer.ui.InstallerGui;

public class Installer {

    public static void run(InstallerConfig config) {

        InstallProcess process = new InstallProcess()
                .then(new ForgeInstallerStep())
                .then(new InstallStep() {

            Future<Void> task;
            int progress;
            boolean canceled;

            @Override
            public Future<Void> start(ProgressCallback callback) {
                task = CompletableFuture.runAsync(() -> {
                    while (!canceled && progress < getMaxProgress()) {
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
                return task;
            }

            @Override
            public String getName() {
                return "Doing Things";
            }

            @Override
            public int getMaxProgress() {
                return 20;
            }

            @Override
            public void cancel() {
                this.canceled = true;
                try {
                    task.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });

        InstallerGui.create(process);
    }
}
