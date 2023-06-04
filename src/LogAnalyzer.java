import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class LogAnalyzer extends JFrame {
    private JTextArea logTextArea;
    private JTextField searchField;
    private volatile boolean monitoringActive;

    public LogAnalyzer() {
        setTitle("Log Analyzer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());
        setLookAndFeel();

        logTextArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(logTextArea);
        add(scrollPane, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel searchLabel = new JLabel("Search:");
        controlPanel.add(searchLabel, BorderLayout.WEST);

        searchField = new JTextField();
        controlPanel.add(searchField, BorderLayout.CENTER);

        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performSearch();
            }
        });
        controlPanel.add(searchButton, BorderLayout.EAST);

        JButton openButton = new JButton("Open Log File");
        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openLogFile();
            }
        });
        controlPanel.add(openButton, BorderLayout.SOUTH);

        JButton generateReportButton = new JButton("Generate Report");
        generateReportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateReport();
            }
        });
        controlPanel.add(generateReportButton, BorderLayout.NORTH);

        add(controlPanel, BorderLayout.NORTH);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton startMonitoringButton = new JButton("Start Monitoring");
        startMonitoringButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startMonitoring();
            }
        });
        bottomPanel.add(startMonitoringButton, BorderLayout.WEST);

        JButton stopMonitoringButton = new JButton("Stop Monitoring");
        stopMonitoringButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopMonitoring();
            }
        });
        bottomPanel.add(stopMonitoringButton, BorderLayout.CENTER);

        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Ignore the exception and use the default look and feel
        }
    }

    private void openLogFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getPath();
            try {
                BufferedReader reader = new BufferedReader(new FileReader(filePath));
                StringBuilder logContent = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    logContent.append(line).append("\n");
                }
                reader.close();

                logTextArea.setText(logContent.toString());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error opening log file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void performSearch() {
        String searchTerm = searchField.getText().trim();
        String logContent = logTextArea.getText();

        if (!searchTerm.isEmpty() && !logContent.isEmpty()) {
            int index = logContent.indexOf(searchTerm);
            if (index != -1) {
                logTextArea.setSelectionStart(index);
                logTextArea.setSelectionEnd(index + searchTerm.length());
                logTextArea.requestFocus();
            } else {
                JOptionPane.showMessageDialog(this, "Search term not found.", "Search", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void generateReport() {
        String logContent = logTextArea.getText();
        if (!logContent.isEmpty()) {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showSaveDialog(this);

            if (result == JFileChooser.APPROVE_OPTION) {
                String filePath = fileChooser.getSelectedFile().getPath();
                try {
                    FileWriter writer = new FileWriter(filePath);
                    writer.write(logContent);
                    writer.close();
                    JOptionPane.showMessageDialog(this, "Log report generated successfully.", "Report Generated", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, "Error generating log report: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void startMonitoring() {
        monitoringActive = true;

        Thread monitoringThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (monitoringActive) {
                    try {
                        // Simulate log entries being added in real-time
                        String newLogEntry = "New log entry: " + System.currentTimeMillis();
                        logTextArea.append(newLogEntry + "\n");

                        // Adjust scroll position to show the latest log entry
                        logTextArea.setCaretPosition(logTextArea.getDocument().getLength());

                        Thread.sleep(1000); // Wait for 1 second before adding the next log entry
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
        });

        monitoringThread.start();
    }

    private void stopMonitoring() {
        monitoringActive = false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new LogAnalyzer();
            }
        });
    }
}
