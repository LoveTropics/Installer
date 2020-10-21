package com.lovetropics.installer.steps;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import com.lovetropics.installer.ProgressCallback;
import com.lovetropics.installer.util.MinecraftInstallationUtils;

public class RunLauncherStep extends SingleTaskStep<Void, Process> {

    @Override
    public String getName() {
        return "Starting Launcher";
    }

    @Override
    protected Future<Process> startTask(Void in, ProgressCallback callback) {
        return CompletableFuture.supplyAsync(MinecraftInstallationUtils::runLauncher);
    }
}
