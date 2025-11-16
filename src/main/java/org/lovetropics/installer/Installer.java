package org.lovetropics.installer;

import java.io.File;

import org.lovetropics.installer.config.InstallerConfig;
import org.lovetropics.installer.steps.CopyModpack;
import org.lovetropics.installer.steps.ForgeInstallerStep;
import org.lovetropics.installer.steps.LauncherStep;
import org.lovetropics.installer.steps.RunLauncherStep;
import org.lovetropics.installer.ui.InstallerGui;
import org.lovetropics.installer.ui.UIConfig;
import org.lovetropics.installer.ui.UIElement;

public class Installer {

    public static void run(InstallerConfig config) {

        final UIConfig gameDir = new UIConfig(System.getProperty("user.home") + File.separator + config.gameDir) {

            @Override
            public synchronized String get() {
                return new File(super.get()).getAbsolutePath();
            }
        };

        InstallProcess<String> process = InstallProcess.create()
                .then(new ForgeInstallerStep(config))
                .then(new LauncherStep(config.profileName, gameDir))
                .then(new CopyModpack(config.modpackZip, gameDir))
                .then(new RunLauncherStep());/*
                .then(new InstallStep<Void, Void>() {

            Future<Void> task;
            int progress;
            boolean canceled;

            @Override
            public Future<Void> start(Void in, ProgressCallback callback) {
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
*/
        InstallerGui.create(config, process)
            .bind(UIElement.GAME_DIR, gameDir);
    }
}
