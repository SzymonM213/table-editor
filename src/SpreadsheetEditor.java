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
                expressions[i][j] = new Expression(i, j);
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
//                        expressions[row][col].update_expression(value);
                        updateCell(row, col, value);
                    }
            }
        });
    }

    private List<String> getDependencies(String expression) {
        List<String> dependencies = new ArrayList<>();
        String[] tokens = expression.split("\\+");
        for (String token : tokens) {
            if (token.matches("[A-Z]+\\d+")) {
                dependencies.add(token);
            }
        }
        return dependencies;
    }

    private boolean isCyclicDependency(Expression cell, Expression dependent, boolean firstCall) {
//        System.out.println("Checking " + cell.row + " " + cell.col + ", " + dependent.row + " " + dependent.col);
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
        List<String> dependencies = getDependencies(text);
        for (String dependency : dependencies) {
            int depCol = dependency.charAt(0) - 'A';
            int depRow = Integer.parseInt(dependency.substring(1)) - 1;
            expressions[depRow][depCol].addDependent(coordinatesToCell(row, col), expressions[row][col]);
            expressions[row][col].addDependency(dependency, expressions[depRow][depCol]);
        }
        if (isCyclicDependency(expressions[row][col], expressions[row][col], true)) {
            expressions[row][col].updateExpression(text, "Cyclic dependency");
        } else {
//            System.out.println("No cyclic dependency");
            expressions[row][col].updateExpression(text);
        }
        expressions[0][0].printDependents();
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
