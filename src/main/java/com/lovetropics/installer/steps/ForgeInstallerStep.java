package com.lovetropics.installer.steps;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.lovetropics.installer.ProgressCallback;
import com.lovetropics.installer.config.InstallerConfig;
import com.lovetropics.installer.util.MinecraftInstallationUtils;

import net.minecraftforge.installer.SimpleInstaller;
import net.minecraftforge.installer.actions.Action;
import net.minecraftforge.installer.actions.ActionCanceledException;
import net.minecraftforge.installer.actions.Actions;
import net.minecraftforge.installer.json.InstallV1;
import net.minecraftforge.installer.json.Util;

public class ForgeInstallerStep extends SingleTaskStep<Void, InstallV1> {

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
    
    private final InstallerConfig config;

    public ForgeInstallerStep(InstallerConfig config) {
        this.config = config;
    }

    @Override
    public Future<InstallV1> startTask(Void in, ProgressCallback callback) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                System.out.println(new File("").getAbsolutePath());
                InstallV1 forgeProfile = Util.loadInstallProfile();
                InstallV1 customProfile = new InstallV1(forgeProfile) {{
                    this.serverJarPath = forgeProfile.getServerJarPath();
                    this.version = this.version + "-" + config.profileName.replace(" ", "");
                }};
                Action action = Actions.CLIENT.getAction(customProfile, new ForgeProgressCallbackAdapter(callback));
                File installer = new File(SimpleInstaller.class.getProtectionDomain().getCodeSource().getLocation().toURI());
                File mcRoot = MinecraftInstallationUtils.getMCDir();
                if (!action.run(mcRoot, $ -> true, installer)) {
                    throw new RuntimeException("Failed to install forge");
                }
                File versionJsonFile = new File(mcRoot, "versions/" + customProfile.getVersion() + "/" + customProfile.getVersion() + ".json");
                JsonObject json = JsonParser.parseReader(new FileReader(versionJsonFile, StandardCharsets.UTF_8)).getAsJsonObject();
                json.addProperty("id", customProfile.getVersion());
                if (config.serverIp != null) {
                    JsonArray args = json.getAsJsonObject("arguments").getAsJsonArray("game");
                    args.add("--quickPlayMultiplayer");
                    args.add(config.serverIp);
                }
                FileUtils.write(versionJsonFile, new Gson().toJson(json), StandardCharsets.UTF_8, false);
                return customProfile;
            } catch (ActionCanceledException | URISyntaxException | JsonIOException | JsonSyntaxException | IOException e) {
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
    }
}