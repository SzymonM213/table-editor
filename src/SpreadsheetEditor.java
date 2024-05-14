import javax.swing.*;
import javax.swing.table.*;
import java.util.List;
import java.awt.*;

public class SpreadsheetEditor extends JFrame {
    public static final int COLS = 5;
    public static final int ROWS = 10;
    private final DefaultTableModel model;
    private final Expression[][] expressions;

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

        model = new DefaultTableModel(ROWS, COLS);
        JTable table = new JTable(model);
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                if (expressions[row][column].hasValue) {
                    value = expressions[row][column].getValue();
                }
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }

        });

        JScrollPane scrollPane = new JScrollPane(table);
        getContentPane().add(scrollPane);

        model.addTableModelListener(e -> {
                int row = e.getFirstRow();
                int col = e.getColumn();
                if (col >= 0) {
                    String value = (String) model.getValueAt(row, col);
                    updateCell(row, col, value);
                }
        });
    }

    private boolean isCyclicDependency(Expression cell, Expression dependent, boolean firstCall) {
        if (cell == dependent && !firstCall) {
            return true;
        }
        for (Expression dependency : cell.getDependencies()) {
            if (isCyclicDependency(dependency, dependent, false)) {
                return true;
            }
        }
        return false;
    }

    private String coordinatesToCell(int row, int col) {
        return (char)('A' + col) + "" + (row + 1);
    }

    private void updateCell(int row, int col, String text) {


        for (Expression dependency : expressions[row][col].getDependencies()) {
            dependency.removeDependent(expressions[row][col]);
        }
        expressions[row][col].clearDependencies();
        List<String> dependencies = Expression.getExpressionDependencies(text);
        for (String dependency : dependencies) {
            int depCol = dependency.charAt(0) - 'A';
            int depRow = Integer.parseInt(dependency.substring(1)) - 1;
            expressions[depRow][depCol].addDependent(coordinatesToCell(row, col), expressions[row][col]);
            expressions[row][col].addDependency(dependency, expressions[depRow][depCol]);
        }
        if (isCyclicDependency(expressions[row][col], expressions[row][col], true)) {
            expressions[row][col].updateExpression(text, "#REF!");
        } else {
            expressions[row][col].updateExpression(text);
        }
    }

    public void setCellValue(int row, int col, String value) {
        model.setValueAt(value, row, col);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SpreadsheetEditor().setVisible(true));
    }
}
