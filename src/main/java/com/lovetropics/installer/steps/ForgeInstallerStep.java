package com.lovetropics.installer.steps;

import java.io.File;
import java.net.URISyntaxException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import com.lovetropics.installer.ProgressCallback;
import com.lovetropics.installer.util.MinecraftInstallationUtils;

import net.minecraftforge.installer.SimpleInstaller;
import net.minecraftforge.installer.actions.Action;
import net.minecraftforge.installer.actions.ActionCanceledException;
import net.minecraftforge.installer.actions.Actions;
import net.minecraftforge.installer.json.Install;
import net.minecraftforge.installer.json.InstallV1;
import net.minecraftforge.installer.json.Util;

public class ForgeInstallerStep extends SingleTaskStep<Void, Install> {

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
            callback.setProgress((int) (progress * 100));
        }

        @Override
        public void stage(String message) {
            reset();
            callback.push(sanitize(message));
            empty = false;
        }

        @Override
        public void message(String message, MessagePriority priority) {
            if (priority == MessagePriority.LOW || message.startsWith("=")) return;
            if (hasSubMessage) {
                callback.pop();
            }
            callback.push(sanitize(message));
            hasSubMessage = true;
        }
    }
    
    @Override
    public Future<Install> startTask(Void in, ProgressCallback callback) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                System.out.println(new File("").getAbsolutePath());
                InstallV1 profile = Util.loadInstallProfile();
                Action action = Actions.CLIENT.getAction(profile, new ForgeProgressCallbackAdapter(callback));
                File installer = new File(SimpleInstaller.class.getProtectionDomain().getCodeSource().getLocation().toURI());
                if (!action.run(MinecraftInstallationUtils.getMCDir(), $ -> true, installer)) {
                    throw new RuntimeException("Failed to install forge");
                }
                return profile;
            } catch (ActionCanceledException | URISyntaxException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            } finally {
                callback.pop();
            }
        });
    }

    @Override
    public String getName() {
        return "Installing Forge";
    }}
