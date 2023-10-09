package com.lovetropics.installer.ui.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JTextField;

import com.lovetropics.installer.config.InstallerConfig;
import com.lovetropics.installer.ui.pane.ContentPane;
import com.lovetropics.installer.util.JSystemFileChooser;

public class BrowseListener implements ActionListener {
    private ContentPane parent;
    private boolean isOpen;
    private JTextField field;
    private InstallerConfig config;
    private JFileChooser fileChooser;

    public BrowseListener(ContentPane parent, boolean isOpen, JTextField field, InstallerConfig config) {
        this.parent = parent;
        this.isOpen = isOpen;
        this.field = field;
        this.config = config;
        this.fileChooser = new JSystemFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setFileHidingEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        File currentDir = new File(field.getText().replaceAll("[\\/]" + this.config.gameDir, ""));
        fileChooser.setSelectedFile(currentDir);
        int returnState;
        if(isOpen) {
            returnState = fileChooser.showOpenDialog(parent);
        } else {
            returnState = fileChooser.showSaveDialog(parent);
        }
        if(returnState == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String path = null; 
            try {
                path = file.getCanonicalPath();
            } catch (IOException ex) {
                path = file.getAbsolutePath();
            }
            
            field.setText(path + File.separator + this.config.gameDir);
        }
    }
}
