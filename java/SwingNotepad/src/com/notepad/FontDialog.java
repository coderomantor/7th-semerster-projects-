package com.notepad;

import javax.swing.*;
import java.awt.*;

/**
 * Modal dialog for selecting font family, size, and style.
 */
public class FontDialog extends JDialog {
    private final JList<String> familyList;
    private final JSpinner sizeSpinner;
    private final JCheckBox boldCheckBox;
    private final JCheckBox italicCheckBox;
    private final JLabel previewLabel;

    private Font selectedFont;

    /**
     * Creates a font picker initialized from current editor font.
     */
    public FontDialog(Frame owner, Font currentFont) {
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

    /**
     * Returns selected font or null if user canceled.
     */
    public Font getSelectedFont() {
        return selectedFont;
    }

    /**
     * Builds font object from current control values.
     */
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

    /**
     * Refreshes preview text with currently selected font settings.
     */
    private void updatePreview() {
        previewLabel.setFont(buildSelectedFont());
    }
}
