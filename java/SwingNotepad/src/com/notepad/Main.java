package com.notepad;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Entry point for the Swing Notepad application.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Use the system look and feel for a native desktop appearance.
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // If this fails, Swing will use its default look and feel.
            }

            NotepadFrame frame = new NotepadFrame();
            frame.setVisible(true);
        });
    }
}
