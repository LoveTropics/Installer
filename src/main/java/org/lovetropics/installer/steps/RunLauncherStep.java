package org.lovetropics.installer.steps;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import org.lovetropics.installer.ProgressCallback;
import org.lovetropics.installer.util.MinecraftInstallationUtils;

public class RunLauncherStep extends SingleTaskStep<Void, String> {

    @Override
    public String getName() {
        return "Starting Launcher";
    }

    @Override
    protected Future<String> startTask(Void in, ProgressCallback callback) {
        return CompletableFuture.supplyAsync(MinecraftInstallationUtils::runLauncher)
                .thenApply(success -> success ?
                        "Click \"Done!\" and then \"PLAY\" on the Minecraft Launcher" :
                        "Open the Minecraft Launcher and press \"PLAY\"");
    }
}
