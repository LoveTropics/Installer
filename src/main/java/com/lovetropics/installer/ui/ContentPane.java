package com.lovetropics.installer.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.lovetropics.installer.Installer;
import com.lovetropics.installer.ui.darrylbu.ShrinkIcon;

import net.miginfocom.swing.MigLayout;

public class ContentPane extends JPanel {
    
    public ContentPane() {
        // Just use WindowBuilder for UI design, please
        setLayout(new MigLayout("", "[grow][grow][grow][grow][grow][grow][grow]", "[][120.00,grow][53.00][20:40:40,grow][36.00,grow]"));
        
        JButton btnInstall = new JButton("Install");
        btnInstall.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // TODO everything
            }
        });
        btnInstall.setOpaque(true);
        add(btnInstall, "cell 3 3,grow");
        
        JLabel logo = new JLabel("");
        logo.setIcon(new ShrinkIcon(Installer.class.getResource("/logo.png")));
        add(logo, "cell 1 1 5 1,grow");
    }
}
