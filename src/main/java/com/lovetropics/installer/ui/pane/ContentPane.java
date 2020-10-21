package com.lovetropics.installer.ui.pane;

import java.util.EnumMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.text.JTextComponent;

import com.lovetropics.installer.Installer;
import com.lovetropics.installer.ProgressCallback;
import com.lovetropics.installer.ui.UIConfig;
import com.lovetropics.installer.ui.UIElement;
import com.lovetropics.installer.ui.component.ProgressPanel;
import com.lovetropics.installer.ui.repack.darrylbu.ShrinkIcon;
import com.lovetropics.installer.ui.util.SimpleDocumentListener;

public class ContentPane extends JPanel {

    private final JFrame root;
    private final JButton btnInstall;
    private final ProgressPanel progress;
    private Future<Void> future;
    
    private final EnumMap<UIElement, JTextComponent> configElements = new EnumMap<>(UIElement.class);

    public ContentPane(JFrame frame, Consumer<ProgressCallback> task) {
        this.root = frame;

        setBorder(new EmptyBorder(0, 20, 20, 20));
        // Just use WindowBuilder for UI design, please        
        setLayout(null);
        
        JTextField gameDir = new JTextField();
        gameDir.setBounds(175, 200, 400, 20);
        configElements.put(UIElement.GAME_DIR, gameDir);
        add(gameDir);

        btnInstall = new JButton("Install");
        btnInstall.setBounds(250, 230, 100, 40);
        btnInstall.setOpaque(true);
        add(btnInstall);

        JLabel logo = new JLabel("");
        logo.setBounds(200, 20, 200, 150);
        logo.setIcon(new ShrinkIcon(Installer.class.getResource("/logo.png")));
        add(logo);
        
        progress = new ProgressPanel();
        progress.setBounds(20, 292, 560, 65);
        add(progress);
        
        JLabel lblNewLabel = new JLabel("Installation Directory:");
        lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        lblNewLabel.setBounds(20, 200, 150, 20);
        add(lblNewLabel);

        btnInstall.addActionListener(e -> {
            if (future == null) {
                future = CompletableFuture.runAsync(() -> task.accept(progress))
                    .exceptionally(t -> {
                        t.printStackTrace();
                        btnInstall.setText("Error!");
                        progress.pop();
                        Throwable root = t;
                        while (t.getCause() != null) {
                            root = t.getCause();
                        }
                        progress.push(root.toString());
                        return null;
                    })
                    .thenRun(() -> {
                        btnInstall.setText("Done!");
                        progress.push("Installation complete.");
                        progress.push("Click \"Done!\" and then \"PLAY\" on the Minecraft Launcher");
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                        root.toFront();
                        root.repaint();
                    });
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

    public void bind(UIElement element, UIConfig config) {
        JTextComponent component = configElements.get(element);
        if (component != null) {
            component.setText(config.get());
            component.getDocument().addDocumentListener((SimpleDocumentListener) $ -> config.update(component.getText()));
        } else {
            throw new IllegalArgumentException("Could not find component for UIElement: " + element);
        }
    }
}
