import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

public class TestSpreadsheet extends JFrame {
    private JTable table;
    private JScrollPane scrollPane;

    public TestSpreadsheet() {
        setTitle("Java Spreadsheet Editor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);

        // Create sample data for the table
        String[][] data = new String[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                data[i][j] = "";
            }
        }

        // Create the table
        table = new JTable(data, new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"});

        // Set row height
        table.setRowHeight(30);

        // Set table cell font
        table.setFont(new Font("Arial", Font.PLAIN, 16));

        // Enable horizontal and vertical scroll bars
        scrollPane = new JScrollPane(table);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Add focus listener to the table
        table.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                JTable source = (JTable) e.getSource();
                int row = source.getEditingRow();
                int column = source.getEditingColumn();
                if (row != -1 && column != -1) {
                    evaluateCell(row, column);
                }
            }
        });

        // Add components to the frame
        getContentPane().add(scrollPane);

        setVisible(true);
    }

    // Method to evaluate the cell when it loses focus
    private void evaluateCell(int row, int column) {
        // Here you can add your logic to evaluate the cell value
        // For simplicity, let's just display a message
//        JOptionPane.showMessageDialog(this, "Cell (" + (row + 1) + ", " + (char) ('A' + column) + ") has lost focus");
        table.setValueAt("Hello", row, column);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TestSpreadsheet::new);
    }
}
