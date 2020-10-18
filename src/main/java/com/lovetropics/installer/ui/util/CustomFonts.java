package com.lovetropics.installer.ui.util;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.plaf.FontUIResource;

import com.lovetropics.installer.Installer;

public class CustomFonts {

    public static FontUIResource createFont(String path, final int size) throws FileNotFoundException, FontFormatException, IOException {
        Font font = Font.createFont(Font.TRUETYPE_FONT, Installer.class.getResourceAsStream(path));

        FontUIResource fontResource = new FontUIResource(font.deriveFont(Font.PLAIN, size));
        return fontResource;
    }
}
