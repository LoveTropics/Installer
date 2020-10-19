package com.lovetropics.installer.ui.pane;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.lovetropics.installer.Installer;
import com.lovetropics.installer.ProgressCallback;
import com.lovetropics.installer.ui.component.ProgressPanel;
import com.lovetropics.installer.ui.repack.darrylbu.ShrinkIcon;

import net.miginfocom.swing.MigLayout;

public class ContentPane extends JPanel {

    private final JButton btnInstall;
    private final ProgressPanel progress;
    private Future<Void> future;

    public ContentPane(Consumer<ProgressCallback> task) {
        // Just use WindowBuilder for UI design, please
        setLayout(new MigLayout("", "[grow][grow][60.00][100.00][60.00][grow][grow]", "[][120.00,grow][53.00][20:40:40,grow][20px:20px,grow][60px:n][20px:n,grow]"));

        btnInstall = new JButton("Install");
        btnInstall.setOpaque(true);
        add(btnInstall, "cell 3 3,grow");

        JLabel logo = new JLabel("");
        logo.setIcon(new ShrinkIcon(Installer.class.getResource("/logo.png")));
        add(logo, "cell 1 1 5 1,grow");

        progress = new ProgressPanel();
        add(progress, "cell 1 5 5 1,growx");

        btnInstall.addActionListener(e -> {
            if (future == null) {
                future = CompletableFuture.runAsync(() -> task.accept(progress))
                    .thenRun(() -> btnInstall.setText("Done!"));
                btnInstall.setText("Installing...");
            } else if (future.isDone()) {
                System.exit(0); // TODO temp?
            }
        });
    }

    public ProgressCallback getProgressCallback() {
        return progress;
    }

    public void setActionString(String string) {
        btnInstall.setText(string);
    }
}
