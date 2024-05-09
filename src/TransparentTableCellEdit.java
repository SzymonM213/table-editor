import javax.swing.*;
import javax.swing.table.*;

public class TransparentTableCellEdit {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Transparent TableCell Edit");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Sample data
        Object[][] data = {
                {"Row 1, Col 1", "Row 1, Col 2", "Row 1, Col 3"},
                {"Row 2, Col 1", "Row 2, Col 2", "Row 2, Col 3"},
                {"Row 3, Col 1", "Row 3, Col 2", "Row 3, Col 3"}
        };

        // Column headers
        Object[] columnHeaders = {"Column 1", "Column 2", "Column 3"};

        // Create JTable with custom cell editor
        JTable table = new JTable(data, columnHeaders) {
            @Override
            public TableCellEditor getCellEditor(int row, int column) {
                return new DefaultCellEditor(new JTextField()) {
                    @Override
                    public boolean stopCellEditing() {
                        // Prevents the value from being saved
                        return true;
                    }
                };
            }
        };

        // Set cell renderer to display empty cells
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public void setValue(Object value) {
                setText("chuj"); // Display empty cell
            }
        });

        // Set table properties
        table.setRowHeight(30);

        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane);
        frame.pack();
        frame.setVisible(true);
    }
}
