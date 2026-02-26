package com.notepad;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Main application window for Swing Notepad.
 */
public class NotepadFrame extends JFrame {
    private final JTextArea textArea;
    private final JFileChooser fileChooser;

    private Path currentFile;
    private boolean modified;
    private boolean suppressModifiedFlag;

    /**
     * Creates and configures the notepad frame.
     */
    public NotepadFrame() {
        super("Swing Notepad");

        textArea = new JTextArea();
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        textArea.setLineWrap(false);
        textArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(textArea);

        fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files (*.txt)", "txt"));
        fileChooser.setAcceptAllFileFilterUsed(true);

        setJMenuBar(createMenuBar());
        add(scrollPane, BorderLayout.CENTER);

        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                markModified();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                markModified();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                markModified();
            }
        });

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitApplication();
            }
        });

        setSize(900, 650);
        setLocationRelativeTo(null);
        updateTitle();
    }

    /**
     * Builds the full menu bar.
     */
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem newItem = new JMenuItem("New");
        JMenuItem openItem = new JMenuItem("Open");
        JMenuItem saveItem = new JMenuItem("Save");
        JMenuItem saveAsItem = new JMenuItem("Save As");
        JMenuItem exitItem = new JMenuItem("Exit");

        newItem.addActionListener(e -> newFile());
        openItem.addActionListener(e -> openFile());
        saveItem.addActionListener(e -> saveFile());
        saveAsItem.addActionListener(e -> saveFileAs());
        exitItem.addActionListener(e -> exitApplication());

        fileMenu.add(newItem);
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.add(saveAsItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        JMenu editMenu = new JMenu("Edit");
        JMenuItem cutItem = new JMenuItem("Cut");
        JMenuItem copyItem = new JMenuItem("Copy");
        JMenuItem pasteItem = new JMenuItem("Paste");
        JMenuItem selectAllItem = new JMenuItem("Select All");
        JMenuItem findItem = new JMenuItem("Find");
        JMenuItem replaceItem = new JMenuItem("Replace");

        cutItem.addActionListener(e -> textArea.cut());
        copyItem.addActionListener(e -> textArea.copy());
        pasteItem.addActionListener(e -> textArea.paste());
        selectAllItem.addActionListener(e -> textArea.selectAll());
        findItem.addActionListener(e -> openFindReplaceDialog(false));
        replaceItem.addActionListener(e -> openFindReplaceDialog(true));

        editMenu.add(cutItem);
        editMenu.add(copyItem);
        editMenu.add(pasteItem);
        editMenu.add(selectAllItem);
        editMenu.addSeparator();
        editMenu.add(findItem);
        editMenu.add(replaceItem);

        JMenu formatMenu = new JMenu("Format");
        JCheckBoxMenuItem wordWrapItem = new JCheckBoxMenuItem("Word Wrap");
        JMenuItem fontItem = new JMenuItem("Font");

        wordWrapItem.addActionListener(e -> {
            boolean wrap = wordWrapItem.isSelected();
            textArea.setLineWrap(wrap);
            textArea.setWrapStyleWord(true);
        });

        fontItem.addActionListener(e -> {
            FontDialog dialog = new FontDialog(this, textArea.getFont());
            dialog.setVisible(true);
            Font selectedFont = dialog.getSelectedFont();
            if (selectedFont != null) {
                textArea.setFont(selectedFont);
            }
        });

        formatMenu.add(wordWrapItem);
        formatMenu.add(fontItem);

        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(formatMenu);
        menuBar.add(helpMenu);

        return menuBar;
    }

    /**
     * Resets editor content to a new, untitled file.
     */
    private void newFile() {
        if (!confirmDiscardOrSave()) {
            return;
        }

        suppressModifiedFlag = true;
        textArea.setText("");
        suppressModifiedFlag = false;

        currentFile = null;
        modified = false;
        updateTitle();
    }

    /**
     * Opens and reads a file with UTF-8 encoding.
     */
    private void openFile() {
        if (!confirmDiscardOrSave()) {
            return;
        }

        int result = fileChooser.showOpenDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        Path filePath = fileChooser.getSelectedFile().toPath();
        try {
            String content = Files.readString(filePath, StandardCharsets.UTF_8);

            suppressModifiedFlag = true;
            textArea.setText(content);
            textArea.setCaretPosition(0);
            suppressModifiedFlag = false;

            currentFile = filePath;
            modified = false;
            updateTitle();
        } catch (IOException ex) {
            showError("Failed to open file:\n" + ex.getMessage());
        }
    }

    /**
     * Saves to current file or invokes Save As if no current file exists.
     */
    private boolean saveFile() {
        if (currentFile == null) {
            return saveFileAs();
        }

        return writeToPath(currentFile);
    }

    /**
     * Prompts user for target file path and saves content there.
     */
    private boolean saveFileAs() {
        int result = fileChooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            return false;
        }

        Path selectedPath = fileChooser.getSelectedFile().toPath();

        // If no extension provided, default to .txt.
        if (!selectedPath.getFileName().toString().contains(".")) {
            selectedPath = selectedPath.resolveSibling(selectedPath.getFileName() + ".txt");
        }

        if (Files.exists(selectedPath)) {
            int overwrite = JOptionPane.showConfirmDialog(
                    this,
                    "File already exists. Overwrite?",
                    "Confirm Overwrite",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
            if (overwrite != JOptionPane.YES_OPTION) {
                return false;
            }
        }

        currentFile = selectedPath;
        return writeToPath(selectedPath);
    }

    /**
     * Writes editor text to a file using UTF-8.
     */
    private boolean writeToPath(Path path) {
        try {
            Files.writeString(path, textArea.getText(), StandardCharsets.UTF_8);
            modified = false;
            updateTitle();
            return true;
        } catch (IOException ex) {
            showError("Failed to save file:\n" + ex.getMessage());
            return false;
        }
    }

    /**
     * Asks user what to do with unsaved changes.
     */
    private boolean confirmDiscardOrSave() {
        if (!modified) {
            return true;
        }

        Object[] options = {"Save", "Don't Save", "Cancel"};
        int choice = JOptionPane.showOptionDialog(
                this,
                "You have unsaved changes. Save before continuing?",
                "Unsaved Changes",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice == JOptionPane.YES_OPTION) {
            return saveFile();
        }
        if (choice == JOptionPane.NO_OPTION) {
            return true;
        }
        return false;
    }

    /**
     * Exits the application after unsaved changes handling.
     */
    private void exitApplication() {
        if (confirmDiscardOrSave()) {
            dispose();
        }
    }

    /**
     * Opens modal find/replace dialog.
     */
    private void openFindReplaceDialog(boolean replaceMode) {
        FindReplaceDialog dialog = new FindReplaceDialog(this, textArea, replaceMode);
        dialog.setVisible(true);
    }

    /**
     * Shows About dialog with project information.
     */
    private void showAboutDialog() {
        String message = "Swing Notepad\n"
                + "Developer: Your Name Here\n"
                + "A lightweight text editor built with Java Swing.";

        JOptionPane.showMessageDialog(
                this,
                message,
                "About",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Updates frame title with file state.
     */
    private void updateTitle() {
        String fileName = (currentFile == null) ? "Untitled" : currentFile.getFileName().toString();
        String prefix = modified ? "*" : "";
        setTitle(prefix + fileName + " - Swing Notepad");
    }

    /**
     * Marks document as changed by user edits.
     */
    private void markModified() {
        if (suppressModifiedFlag) {
            return;
        }
        if (!modified) {
            modified = true;
            updateTitle();
        }
    }

    /**
     * Shows an error message dialog.
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
