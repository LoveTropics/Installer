package com.lovetropics.installer.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.util.function.Consumer;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.plaf.synth.SynthLookAndFeel;

import com.lovetropics.installer.Installer;
import com.lovetropics.installer.ProgressCallback;
import com.lovetropics.installer.ui.pane.ContentPane;
import com.lovetropics.installer.ui.pane.TitlePane;

public class InstallerGui extends JFrame {

    public static InstallerGui create(Consumer<ProgressCallback> task) {
        try {
            // Try to load our synth look and feel from XML
            SynthLookAndFeel laf = new SynthLookAndFeel();
            laf.load(Installer.class.getResourceAsStream("/laf.xml"), Installer.class);
            UIManager.setLookAndFeel(laf);
        } catch (Exception e) {
            // If that fails, attempt to use the system look and feel as a fallback
            e.printStackTrace();
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

        final InstallerGui gui = new InstallerGui(task);
        EventQueue.invokeLater(() -> {
            try {
                gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                // TODO choose a different size?
                gui.setSize(400, 400);
                // Centers the window on the monitor
                gui.setLocationRelativeTo(null);
                // Remove OS window decorations
                gui.setUndecorated(true);
                // Show the window
                gui.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return gui;
    }

    public InstallerGui(Consumer<ProgressCallback> task) {
        getContentPane().setLayout(new BorderLayout());

        getContentPane().add(new TitlePane(this), BorderLayout.NORTH);
        // You might think SOUTH would make more sense, but this squishes the entire pane into the bottom half of the window
        // CENTER gets the behavior we want (thin title pane at the top, the rest content)
        getContentPane().add(new ContentPane(task), BorderLayout.CENTER);
    }
}
