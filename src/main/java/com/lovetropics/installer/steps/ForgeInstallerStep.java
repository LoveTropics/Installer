package com.lovetropics.installer.steps;

import java.io.File;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.lovetropics.installer.ProgressCallback;

import net.minecraftforge.installer.actions.Action;
import net.minecraftforge.installer.actions.ActionCanceledException;
import net.minecraftforge.installer.actions.Actions;
import net.minecraftforge.installer.json.Install;
import net.minecraftforge.installer.json.Util;

public class ForgeInstallerStep implements InstallStep {

    private static class ForgeProgressCallbackAdapter implements net.minecraftforge.installer.actions.ProgressCallback {

        private final ProgressCallback callback;
        private boolean empty = true;
        private boolean hasSubMessage = false;

        ForgeProgressCallbackAdapter(ProgressCallback callback) {
            this.callback = callback;
        }

        private void reset() {
            if (empty) return;
            if (hasSubMessage) {
                callback.pop();
                hasSubMessage = false;
            }
            callback.pop();
        }

        private String sanitize(String label) {
            int maxlen = 60;
            if (label.length() > maxlen) {
                label = label.substring(0, maxlen - 3) + "...";
            }
            return label;
        }

        @Override
        public void start(String label) {
            reset();
            callback.push(sanitize(label), 100);
            empty = false;
        }

        @Override
        public void progress(double progress) {
            callback.setProgress((int) progress * 100);
        }

        @Override
        public void stage(String message) {
            reset();
            callback.push(sanitize(message));
            empty = false;
        }

        @Override
        public void message(String message, MessagePriority priority) {
            if (hasSubMessage) {
                callback.pop();
            }
            callback.push(sanitize(message));
            hasSubMessage = true;
        }
    }

    private Future<Void> task;

    @Override
    public Future<Void> start(ProgressCallback callback) {
        task = CompletableFuture.runAsync(() -> {
            try {
                System.out.println(new File("").getAbsolutePath());
                Install profile = Util.loadInstallProfile();
                Action action = Actions.CLIENT.getAction(profile, new ForgeProgressCallbackAdapter(callback));
                if (!action.run(getMCDir(), $ -> true)) {
                    throw new RuntimeException("Failed to install forge");
                }
            } catch (ActionCanceledException e) {
                e.printStackTrace();
            } finally {
                callback.pop();
            }
        });
        return task;
    }

    private static File getMCDir() {
        String userHomeDir = System.getProperty("user.home", ".");
        String osType = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
        String mcDir = ".minecraft";
        if (osType.contains("win") && System.getenv("APPDATA") != null)
            return new File(System.getenv("APPDATA"), mcDir);
        else if (osType.contains("mac"))
            return new File(new File(new File(userHomeDir, "Library"), "Application Support"), "minecraft");
        return new File(userHomeDir, mcDir);
    }

    @Override
    public String getName() {
        return "Installing Forge";
    }

    @Override
    public int getMaxProgress() {
        return 0; // TODO can we extract progress information from the sub process?
    }

    @Override
    public void cancel() {
        if (task != null) {
            try {
                task.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }
}
