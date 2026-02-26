package com.notepad;

import javax.swing.*;
import java.awt.*;

/**
 * Modal dialog for Find and Replace operations.
 */
public class FindReplaceDialog extends JDialog {
    private final JTextArea textArea;
    private final JTextField findField;
    private final JTextField replaceField;
    private final JCheckBox caseSensitiveCheckBox;

    /**
     * Creates the dialog in find-only or replace mode.
     */
    public FindReplaceDialog(Frame owner, JTextArea textArea, boolean replaceMode) {
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

    /**
     * Finds and selects next match from caret position, wrapping to top if needed.
     */
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

        // Wrap and search from beginning if not found after current caret.
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

    /**
     * Replaces current selection if it matches, then finds the next occurrence.
     */
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

    /**
     * Replaces all occurrences and reports replacement count.
     */
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

    /**
     * Case-sensitive or case-insensitive comparison helper.
     */
    private boolean matches(String value, String target, boolean caseSensitive) {
        return caseSensitive ? value.equals(target) : value.equalsIgnoreCase(target);
    }

    /**
     * Shows informational message in this dialog context.
     */
    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Find / Replace", JOptionPane.INFORMATION_MESSAGE);
    }
}
