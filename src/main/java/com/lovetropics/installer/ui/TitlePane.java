package com.lovetropics.installer.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.lovetropics.installer.Installer;

public class TitlePane extends JPanel implements MouseMotionListener {

    private final JFrame root;
    int posX = 0, posY = 0;

    public TitlePane(Installer root) {
        this.root = root;

        setLayout(new GridBagLayout());
        
        // Add custom close button
        GridBagConstraints gbc = new GridBagConstraints();
        // Give the button some forced size since it has no text
        gbc.ipady = 20;
        gbc.ipadx = 20;
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.EAST;
        // Fill the vertical height of the title bar no matter what
        gbc.fill = GridBagConstraints.VERTICAL;
        add(new CloseButton(root), gbc);

        // Add handling for window dragging, without OS window decorations we have to do this ourselves
        addMouseMotionListener(this);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        this.posX = e.getX();
        this.posY = e.getY();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        root.setLocation(e.getXOnScreen() - posX, e.getYOnScreen() - posY);
    }
}
