package org.lovetropics.installer.ui.component;

import java.awt.Window;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class CloseButton extends JButton {
    
    public CloseButton(JFrame root) {
        setName("Close");
        addActionListener(e -> {
            // dispose() is not sufficient to end the process, so we send the window event that the OS close button would send 
            Window window = SwingUtilities.getWindowAncestor(CloseButton.this);
            window.dispatchEvent(new WindowEvent(root, WindowEvent.WINDOW_CLOSING));
        });
    }
}
