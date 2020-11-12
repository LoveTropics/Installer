package com.lovetropics.installer.steps;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import com.lovetropics.installer.ProgressCallback;
import com.lovetropics.installer.util.MinecraftInstallationUtils;

public class RunLauncherStep extends SingleTaskStep<Void, String> {

    @Override
    public String getName() {
        return "Starting Launcher";
    }

    @Override
    protected Future<String> startTask(Void in, ProgressCallback callback) {
        return CompletableFuture.supplyAsync(MinecraftInstallationUtils::runLauncher)
                .thenApply($ -> "Click \"Done!\" and then \"PLAY\" on the Minecraft Launcher")
                .exceptionally($ -> "Open the Minecraft Launcher and press \"PLAY\"");
    }
}
