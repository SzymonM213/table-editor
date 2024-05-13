import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public class Spreadsheet extends JFrame {
    private JTable table;
    private DefaultTableModel model;

    public Spreadsheet() {
        setTitle("Java Spreadsheet Editor");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Create a sample table model with row numbers as the first column
        model = new DefaultTableModel(10, 5) {
            @Override
            public Object getValueAt(int row, int column) {
                // Display row numbers in the first column
                if (column == 0) {
                    return row + 1; // Adding 1 to start row numbering from 1
                } else {
                    return super.getValueAt(row, column);
                }
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                // Make the row numbers non-editable
                return column != 0;
            }
        };

        // Set column headers
        model.setColumnIdentifiers(new String[]{"", "A", "B", "C", "D"});

        // Populate sample data
        for (int row = 0; row < 10; row++) {
            for (int col = 1; col < 5; col++) { // Start from column 1
                model.setValueAt("Data " + (row + 1) + "-" + col, row, col);
            }
        }

        // Create the JTable
        table = new JTable(model);

        // Adjust width of the row number column header
        TableColumn column = table.getColumnModel().getColumn(0);
        column.setHeaderValue("");
        column.setPreferredWidth(50); // Adjust this value as needed

        // Refresh the table to reflect changes in column headers
        table.getTableHeader().repaint();

        // Add the table to a scroll pane
        JScrollPane scrollPane = new JScrollPane(table);
        getContentPane().add(scrollPane);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Spreadsheet::new);
    }
}
