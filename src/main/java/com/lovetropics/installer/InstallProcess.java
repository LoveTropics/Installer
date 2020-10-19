package com.lovetropics.installer;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ExecutionException;

import com.lovetropics.installer.steps.InstallStep;

public class InstallProcess {

    private final Deque<InstallStep> steps = new ArrayDeque<>();
    private InstallStep currentStep;
    private boolean canceled;

    public InstallProcess then(InstallStep step) {
        steps.add(step);
        return this;
    }

    public void run(ProgressCallback callback) {
        while (!canceled && !steps.isEmpty()) {
            try {
                currentStep = steps.remove();
                callback.push(currentStep.getName(), currentStep.getMaxProgress());
                currentStep.start(callback).get();
                callback.pop();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            } finally {
                currentStep = null;
            }
        }
        steps.clear();
    }

    public void cancel() {
        canceled = true;
        if (currentStep != null) {
            currentStep.cancel();
        }
        while (!steps.isEmpty()) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
