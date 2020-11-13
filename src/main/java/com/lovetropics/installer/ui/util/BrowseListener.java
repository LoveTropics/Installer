package com.lovetropics.installer.ui.util;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JTextField;

import com.lovetropics.installer.ui.pane.ContentPane;
import com.lovetropics.installer.util.JSystemFileChooser;

public class BrowseListener extends MouseAdapter {
    private ContentPane parent;
    private boolean isOpen;
    private JTextField field;
    private JFileChooser fileChooser;

    public BrowseListener(ContentPane parent, boolean isOpen, JTextField field) {
        this.parent = parent;
        this.isOpen = isOpen;
        this.field = field;
        this.fileChooser = new JSystemFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setFileHidingEnabled(false);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        File currentDir = new File(field.getText().replaceAll("[\\/]LoveTropics2020", ""));
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
            
            field.setText(path + File.separator + "LoveTropics2020");
        }
    }
}
