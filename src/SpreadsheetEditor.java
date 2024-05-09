import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.*;

public class SpreadsheetEditor extends JFrame {
    private JTable table;
    private CustomTableModel model;
    private Expression[][] expressions;
    private boolean isUpdating = false;
    private int COLS = 5;
    private int ROWS = 10;

    public SpreadsheetEditor() {
        setTitle("Spreadsheet Editor");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        expressions = new Expression[ROWS][COLS];
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                expressions[i][j] = new Expression();
            }
        }

        model = new CustomTableModel(ROWS, COLS);
        table = new JTable(model);
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, expressions[row][column].getValue(), isSelected, hasFocus, row, column);

                return c;
            }

        });

        JScrollPane scrollPane = new JScrollPane(table);
        getContentPane().add(scrollPane);

        model.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                    int row = e.getFirstRow();
                    int col = e.getColumn();
                    if (col >= 0) {
                        String value = (String) model.getValueAt(row, col);
                        expressions[row][col].update_expression(value);
                    }
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new SpreadsheetEditor().setVisible(true);
            }
        });
    }
}

class CustomTableModel extends DefaultTableModel {
    public CustomTableModel(int rows, int cols) {
        super(rows, cols);
    }
}
