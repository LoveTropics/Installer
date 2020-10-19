package com.lovetropics.installer.steps;

import java.util.concurrent.Future;

import com.lovetropics.installer.ProgressCallback;

public interface InstallStep {

    Future<Void> start(ProgressCallback callback);

    String getName();

    int getMaxProgress();
}
