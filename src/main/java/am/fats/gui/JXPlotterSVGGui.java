package am.fats.gui;

import am.fats.FileLineWriter;
import am.fats.Material;
import am.fats.SVGParser;
import am.fats.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Simple GUI for JXPlotterSVG
 */
public class JXPlotterSVGGui extends JFrame {
    private static final Logger logger = LoggerFactory.getLogger(JXPlotterSVGGui.class);
    
    private JTextField svgFileField;
    private JTextField materialFileField;
    private JTextField outputFileField;
    private JCheckBox disablePauseCheckbox;
    private JTextArea logArea;
    private JButton convertButton;
    
    public JXPlotterSVGGui() {
        super("JXPlotterSVG " + Version.version());
        setupUI();
    }
    
    private void setupUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Input panel
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // SVG file selection
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("SVG File:"), gbc);
        
        svgFileField = new JTextField(20);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        inputPanel.add(svgFileField, gbc);
        
        JButton svgBrowseButton = new JButton("Browse");
        svgBrowseButton.addActionListener(this::browseSvgFile);
        gbc.gridx = 2;
        gbc.weightx = 0.0;
        inputPanel.add(svgBrowseButton, gbc);
        
        // Material file selection
        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("Material File:"), gbc);
        
        materialFileField = new JTextField(20);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        inputPanel.add(materialFileField, gbc);
        
        JButton materialBrowseButton = new JButton("Browse");
        materialBrowseButton.addActionListener(this::browseMaterialFile);
        gbc.gridx = 2;
        gbc.weightx = 0.0;
        inputPanel.add(materialBrowseButton, gbc);
        
        // Output file selection
        gbc.gridx = 0;
        gbc.gridy = 2;
        inputPanel.add(new JLabel("Output File:"), gbc);
        
        outputFileField = new JTextField("XPLOTTER.G", 20);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        inputPanel.add(outputFileField, gbc);
        
        JButton outputBrowseButton = new JButton("Browse");
        outputBrowseButton.addActionListener(this::browseOutputFile);
        gbc.gridx = 2;
        gbc.weightx = 0.0;
        inputPanel.add(outputBrowseButton, gbc);
        
        // Options
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        disablePauseCheckbox = new JCheckBox("Disable Pause Button");
        inputPanel.add(disablePauseCheckbox, gbc);
        
        // Convert button
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 3;
        convertButton = new JButton("Convert");
        convertButton.addActionListener(this::convertFile);
        inputPanel.add(convertButton, gbc);
        
        // Log area
        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setPreferredSize(new Dimension(500, 200));
        
        // Add components to main panel
        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Status bar
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.add(new JLabel("JXPlotterSVG " + Version.fullVersion()));
        mainPanel.add(statusBar, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
    }
    
    private void browseSvgFile(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select SVG File");
        fileChooser.setFileFilter(new FileNameExtensionFilter("SVG Files", "svg"));
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            svgFileField.setText(fileChooser.getSelectedFile().getAbsolutePath());
        }
    }
    
    private void browseMaterialFile(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Material File");
        fileChooser.setFileFilter(new FileNameExtensionFilter("XML Files", "xml"));
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            materialFileField.setText(fileChooser.getSelectedFile().getAbsolutePath());
        }
    }
    
    private void browseOutputFile(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Output File");
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            outputFileField.setText(fileChooser.getSelectedFile().getAbsolutePath());
        }
    }
    
    private void convertFile(ActionEvent e) {
        // Disable the convert button while processing
        convertButton.setEnabled(false);
        logArea.setText("");
        
        // Get input values
        String svgFile = svgFileField.getText().trim();
        String materialFile = materialFileField.getText().trim();
        String outputFile = outputFileField.getText().trim();
        boolean disablePause = disablePauseCheckbox.isSelected();
        
        // Validate inputs
        if (svgFile.isEmpty() || materialFile.isEmpty() || outputFile.isEmpty()) {
            logArea.append("Error: All fields must be filled in.\n");
            convertButton.setEnabled(true);
            return;
        }
        
        // Check if files exist
        if (!Files.exists(Paths.get(svgFile))) {
            logArea.append("Error: SVG file not found: " + svgFile + "\n");
            convertButton.setEnabled(true);
            return;
        }
        
        if (!Files.exists(Paths.get(materialFile))) {
            logArea.append("Error: Material file not found: " + materialFile + "\n");
            convertButton.setEnabled(true);
            return;
        }
        
        // Run conversion in a background thread
        SwingWorker<Void, String> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                try {
                    publish("Starting conversion...");
                    publish("Loading material file: " + materialFile);
                    Material.load(materialFile);
                    
                    publish("Creating output file: " + outputFile);
                    try (FileLineWriter gcode = new FileLineWriter(outputFile)) {
                        SVGParser parser = new SVGParser();
                        
                        publish("Processing SVG file: " + svgFile);
                        parser.process(svgFile, gcode, !disablePause);
                        
                        publish("Conversion complete!");
                    }
                } catch (Exception ex) {
                    logger.error("Error during conversion", ex);
                    publish("Error: " + ex.getMessage());
                }
                return null;
            }
            
            @Override
            protected void process(java.util.List<String> chunks) {
                for (String message : chunks) {
                    logArea.append(message + "\n");
                }
            }
            
            @Override
            protected void done() {
                convertButton.setEnabled(true);
            }
        };
        
        worker.execute();
    }
    
    /**
     * Launch the GUI application
     */
    public static void main(String[] args) {
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            logger.warn("Could not set system look and feel", e);
        }
        
        SwingUtilities.invokeLater(() -> {
            JXPlotterSVGGui gui = new JXPlotterSVGGui();
            gui.setVisible(true);
        });
    }
}