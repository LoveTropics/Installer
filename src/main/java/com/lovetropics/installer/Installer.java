package com.lovetropics.installer;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.plaf.synth.SynthLookAndFeel;

import com.lovetropics.installer.ui.ContentPane;
import com.lovetropics.installer.ui.TitlePane;

public class Installer extends JFrame {

    private static final long serialVersionUID = 423454770460261455L;

    public static void main(String[] args) {        
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

        EventQueue.invokeLater(() -> {
            try {
                Installer installer = new Installer();
                installer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                // TODO choose a different size?
                installer.setSize(400, 400);
                // Centers the window on the monitor
                installer.setLocationRelativeTo(null);
                // Remove OS window decorations
                installer.setUndecorated(true);
                // Show the window
                installer.setVisible(true);
            } catch(Exception e) {
                e.printStackTrace();
            }
        });
    }
    
    public Installer() {
        getContentPane().setLayout(new BorderLayout());

        getContentPane().add(new TitlePane(this), BorderLayout.NORTH);
        // You might think SOUTH would make more sense, but this squishes the entire pane into the bottom half of the window
        // CENTER gets the behavior we want (thin title pane at the top, the rest content)
        getContentPane().add(new ContentPane(), BorderLayout.CENTER);
    }
}
