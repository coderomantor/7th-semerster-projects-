package com.notepad;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;

/**
 * Compact dialogs bundle: font picker and find/replace dialog with unique class names
 * to avoid colliding with the original project classes.
 */
public class CompactNotepadDialogs {
    // This class intentionally left empty; it only groups the nested classes below.
}

class CompactFontDialog extends JDialog {
    private final JList<String> familyList;
    private final JSpinner sizeSpinner;
    private final JCheckBox boldCheckBox;
    private final JCheckBox italicCheckBox;
    private final JLabel previewLabel;

    private Font selectedFont;

    public CompactFontDialog(Frame owner, Font currentFont) {
        super(owner, "Choose Font", true);

        String[] families = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getAvailableFontFamilyNames();

        familyList = new JList<>(families);
        familyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        if (currentFont != null) {
            familyList.setSelectedValue(currentFont.getFamily(), true);
        } else {
            familyList.setSelectedIndex(0);
        }

        int initialSize = (currentFont != null) ? currentFont.getSize() : 14;
        sizeSpinner = new JSpinner(new SpinnerNumberModel(initialSize, 8, 96, 1));

        boldCheckBox = new JCheckBox("Bold", currentFont != null && currentFont.isBold());
        italicCheckBox = new JCheckBox("Italic", currentFont != null && currentFont.isItalic());

        previewLabel = new JLabel("The quick brown fox jumps over the lazy dog.");
        previewLabel.setBorder(BorderFactory.createTitledBorder("Preview"));

        JScrollPane listScrollPane = new JScrollPane(familyList);
        listScrollPane.setPreferredSize(new Dimension(220, 170));

        JPanel controlsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        controlsPanel.add(new JLabel("Size:"), gbc);

        gbc.gridx = 1;
        controlsPanel.add(sizeSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        controlsPanel.add(boldCheckBox, gbc);

        gbc.gridx = 1;
        controlsPanel.add(italicCheckBox, gbc);

        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> {
            selectedFont = buildSelectedFont();
            dispose();
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> {
            selectedFont = null;
            dispose();
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.add(listScrollPane, BorderLayout.CENTER);
        topPanel.add(controlsPanel, BorderLayout.EAST);

        setLayout(new BorderLayout(10, 10));
        add(topPanel, BorderLayout.CENTER);
        add(previewLabel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);

        familyList.addListSelectionListener(e -> updatePreview());
        sizeSpinner.addChangeListener(e -> updatePreview());
        boldCheckBox.addActionListener(e -> updatePreview());
        italicCheckBox.addActionListener(e -> updatePreview());

        updatePreview();
        pack();
        setLocationRelativeTo(owner);
    }

    public Font getSelectedFont() {
        return selectedFont;
    }

    private Font buildSelectedFont() {
        String family = familyList.getSelectedValue();
        if (family == null) {
            family = Font.MONOSPACED;
        }

        int style = Font.PLAIN;
        if (boldCheckBox.isSelected()) {
            style |= Font.BOLD;
        }
        if (italicCheckBox.isSelected()) {
            style |= Font.ITALIC;
        }

        int size = (Integer) sizeSpinner.getValue();
        return new Font(family, style, size);
    }

    private void updatePreview() {
        previewLabel.setFont(buildSelectedFont());
    }
}

class CompactFindReplaceDialog extends JDialog {
    private final JTextArea textArea;
    private final JTextField findField;
    private final JTextField replaceField;
    private final JCheckBox caseSensitiveCheckBox;

    public CompactFindReplaceDialog(Frame owner, JTextArea textArea, boolean replaceMode) {
        super(owner, replaceMode ? "Find / Replace" : "Find", true);
        this.textArea = textArea;

        findField = new JTextField(20);
        replaceField = new JTextField(20);
        caseSensitiveCheckBox = new JCheckBox("Case Sensitive");

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("Find:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        inputPanel.add(findField, gbc);

        if (replaceMode) {
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.weightx = 0;
            inputPanel.add(new JLabel("Replace:"), gbc);

            gbc.gridx = 1;
            gbc.weightx = 1.0;
            inputPanel.add(replaceField, gbc);

            gbc.gridx = 1;
            gbc.gridy = 2;
            gbc.weightx = 0;
            inputPanel.add(caseSensitiveCheckBox, gbc);
        } else {
            gbc.gridx = 1;
            gbc.gridy = 1;
            gbc.weightx = 0;
            inputPanel.add(caseSensitiveCheckBox, gbc);
        }

        JButton findNextButton = new JButton("Find Next");
        findNextButton.addActionListener(e -> findNext());

        JButton replaceButton = new JButton("Replace");
        replaceButton.addActionListener(e -> replaceCurrent());

        JButton replaceAllButton = new JButton("Replace All");
        replaceAllButton.addActionListener(e -> replaceAll());

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(findNextButton);

        if (replaceMode) {
            buttonPanel.add(replaceButton);
            buttonPanel.add(replaceAllButton);
        }

        buttonPanel.add(closeButton);

        setLayout(new BorderLayout());
        add(inputPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(findNextButton);
        pack();
        setLocationRelativeTo(owner);
    }

    private void findNext() {
        String findText = findField.getText();
        if (findText == null || findText.isEmpty()) {
            showInfo("Enter text to find.");
            return;
        }

        String fullText = textArea.getText();
        String source = fullText;
        String target = findText;

        if (!caseSensitiveCheckBox.isSelected()) {
            source = fullText.toLowerCase();
            target = findText.toLowerCase();
        }

        int start = textArea.getSelectionEnd();
        int index = source.indexOf(target, start);

        if (index < 0 && start > 0) {
            index = source.indexOf(target, 0);
        }

        if (index >= 0) {
            textArea.requestFocusInWindow();
            textArea.select(index, index + findText.length());
        } else {
            showInfo("Text not found.");
        }
    }

    private void replaceCurrent() {
        String findText = findField.getText();
        if (findText == null || findText.isEmpty()) {
            showInfo("Enter text to find.");
            return;
        }

        String selected = textArea.getSelectedText();
        boolean caseSensitive = caseSensitiveCheckBox.isSelected();

        if (selected != null && matches(selected, findText, caseSensitive)) {
            textArea.replaceSelection(replaceField.getText());
        }

        findNext();
    }

    private void replaceAll() {
        String findText = findField.getText();
        if (findText == null || findText.isEmpty()) {
            showInfo("Enter text to find.");
            return;
        }

        String replaceText = replaceField.getText();
        String fullText = textArea.getText();

        String source = caseSensitiveCheckBox.isSelected() ? fullText : fullText.toLowerCase();
        String target = caseSensitiveCheckBox.isSelected() ? findText : findText.toLowerCase();

        int count = 0;
        int fromIndex = 0;
        int found;

        while ((found = source.indexOf(target, fromIndex)) >= 0) {
            count++;
            fromIndex = found + target.length();
        }

        if (count == 0) {
            showInfo("No matches found.");
            return;
        }

        if (caseSensitiveCheckBox.isSelected()) {
            textArea.setText(fullText.replace(findText, replaceText));
        } else {
            StringBuilder builder = new StringBuilder();
            int last = 0;
            fromIndex = 0;

            while ((found = source.indexOf(target, fromIndex)) >= 0) {
                builder.append(fullText, last, found);
                builder.append(replaceText);
                fromIndex = found + target.length();
                last = fromIndex;
            }
            builder.append(fullText.substring(last));
            textArea.setText(builder.toString());
        }

        showInfo("Replaced " + count + " occurrence(s).");
    }

    private boolean matches(String value, String target, boolean caseSensitive) {
        return caseSensitive ? value.equals(target) : value.equalsIgnoreCase(target);
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Find / Replace", JOptionPane.INFORMATION_MESSAGE);
    }
}
