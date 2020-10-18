package com.lovetropics.installer.ui;

import java.awt.Window;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.SwingUtilities;

import com.lovetropics.installer.Installer;

public class CloseButton extends JButton {
    
    public CloseButton(Installer root) {
        setName("Close");
        addActionListener(e -> {
            // dispose() is not sufficient to end the process, so we send the window event that the OS close button would send 
            Window window = SwingUtilities.getWindowAncestor(CloseButton.this);
            window.dispatchEvent(new WindowEvent(root, WindowEvent.WINDOW_CLOSING));
        });
    }
}
