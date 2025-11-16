package org.lovetropics.installer.ui.pane;

import java.io.File;
import java.util.EnumMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.Function;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.text.JTextComponent;

import org.lovetropics.installer.Installer;
import org.lovetropics.installer.ProgressCallback;
import org.lovetropics.installer.config.InstallerConfig;
import org.lovetropics.installer.ui.UIConfig;
import org.lovetropics.installer.ui.UIElement;
import org.lovetropics.installer.ui.component.ProgressPanel;
import org.lovetropics.installer.ui.repack.darrylbu.ShrinkIcon;
import org.lovetropics.installer.ui.util.BrowseListener;
import org.lovetropics.installer.ui.util.SimpleDocumentListener;

public class ContentPane extends JPanel {

    private final JFrame root;
    private final JButton btnInstall;
    private final ProgressPanel progress;
    private Future<Void> future;
    
    private final EnumMap<UIElement, JTextComponent> configElements = new EnumMap<>(UIElement.class);

    public ContentPane(JFrame frame, InstallerConfig config, Function<ProgressCallback, String> task) {
        this.root = frame;

        setBorder(new EmptyBorder(0, 20, 20, 20));
        // Just use WindowBuilder for UI design, please        
        setLayout(null);
        
        JTextField gameDir = new JTextField();
        gameDir.setBounds(175, 162, 405, 20);
        configElements.put(UIElement.GAME_DIR, gameDir);
        add(gameDir);

        btnInstall = new JButton("Install");
        btnInstall.setBounds(250, 230, 100, 40);
        btnInstall.setOpaque(true);
        add(btnInstall);

        JLabel logo = new JLabel("");
        logo.setBounds(200, 0, 200, 150);
        logo.setIcon(new ShrinkIcon(Installer.class.getResource("/logo.png")));
        add(logo);
        
        progress = new ProgressPanel();
        progress.setBounds(20, 292, 560, 65);
        add(progress);
        
        JLabel lblNewLabel = new JLabel("Installation Directory:");
        lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        lblNewLabel.setBounds(20, 162, 150, 20);
        add(lblNewLabel);
        
        JButton btnBrowse = new JButton("Browse");
        btnBrowse.setBounds(506, 193, 74, 30);
        add(btnBrowse);
        btnBrowse.addActionListener(new BrowseListener(this, true, gameDir, config));

        btnInstall.addActionListener(e -> {
            if (future == null) {
                File installDir = new File(gameDir.getText());
                if (installDir.exists()) {
                    if (!installDir.isDirectory()) {
                        JOptionPane.showMessageDialog(this, "Install target must be a folder!", "Install Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    } else if (installDir.list().length > 0) {
                        int res = JOptionPane.showConfirmDialog(this, "Install target is not empty, continue?", "Install Warning", JOptionPane.WARNING_MESSAGE);
                        if (res == JOptionPane.CANCEL_OPTION) {
                            return;
                        }
                    }
                }
                future = CompletableFuture.supplyAsync(() -> task.apply(progress))
                    .thenAccept(msg -> {
                        btnInstall.setText("Done!");
                        progress.push("Installation complete.");
                        progress.push(msg);
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                        root.toFront();
                        root.repaint();
                    })
                    .exceptionally(t -> {
                        t.printStackTrace();
                        btnInstall.setText("Error!");
                        progress.pop();
                        Throwable root = t;
                        while (root.getCause() != null) {
                            root = root.getCause();
                        }
                        progress.push(root.toString());
                        return null;
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
