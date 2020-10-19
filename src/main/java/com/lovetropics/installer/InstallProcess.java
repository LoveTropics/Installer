package com.lovetropics.installer;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ExecutionException;

import com.lovetropics.installer.steps.InstallStep;

public class InstallProcess {

    private final Deque<InstallStep> steps = new ArrayDeque<>();

    public InstallProcess then(InstallStep step) {
        steps.add(step);
        return this;
    }

    public void run(ProgressCallback callback) {
        while (!steps.isEmpty()) {
            InstallStep step = steps.remove();
            try {
                callback.push(step.getName(), step.getMaxProgress());
                step.start(callback).get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
