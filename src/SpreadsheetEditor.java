import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.*;

public class SpreadsheetEditor extends JFrame {
    public static final int COLS = 5;
    public static final int ROWS = 10;
    private JTable table;
    private CustomTableModel model;
    private Expression[][] expressions;
    private boolean isUpdating = false;
    private int selectedRow = 0;
    private int selectedCol = 0;

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
                if (expressions[row][column].hasValue) {
                    value = expressions[row][column].getValue();
                }
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
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
                    System.out.println("Previously Selected " + selectedRow + " " + selectedCol);
                    selectedCol = table.getSelectedColumn();
                    selectedRow = table.getSelectedRow();
                    System.out.println("Selected " + selectedRow + " " + selectedCol);
            }
        });

//        table.getColumnModel().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
//            @Override
//            public void valueChanged(ListSelectionEvent e) {
//                int row = selectedRow;
//                int col = selectedCol;
//                if ((selectedCol != table.getSelectedRow() || selectedRow != table.getSelectedColumn()) && row >= 0 && col >= 0) {
//                    String value = (String) model.getValueAt(row, col);
//                    if (value != null) {
//                        updateCell(row, col, value);
//                    }
//                    updateTableAppearance(expressions[row][col], row, col);
//                }
//                selectedRow = table.getSelectedRow();
//                selectedCol = table.getSelectedColumn();
//            }
//        });
//
//        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
//            @Override
//            public void valueChanged(ListSelectionEvent e) {
//                int row = selectedRow;
//                int col = selectedCol;
//                if ((selectedCol != table.getSelectedRow() || selectedRow != table.getSelectedColumn()) && row >= 0 && col >= 0) {
//                    String value = (String) model.getValueAt(row, col);
//                    if (value != null) {
//                        updateCell(row, col, value);
//                    }
//                    updateTableAppearance(expressions[row][col], row, col);
//                }
//                selectedRow = table.getSelectedRow();
//                selectedCol = table.getSelectedColumn();
//            }
//        });

    }

    private void updateTableAppearance(Expression caller, int row, int col) {
//        if (expressions[row][col] != caller) {
//            model.setValueAt(expressions[row][col].getValue(), row, col);
//            for (Expression dependent : expressions[row][col].getDependents()) {
//                updateTableAppearance(expressions[row][col], dependent.row, dependent.col);
//            }
//        }
    }

//    private List<String> getDependencies(String expression) {
//        if (!expression.startsWith("=")) {
//            return new ArrayList<>();
//        }
//        expression = expression.substring(1);
//        List<String> dependencies = new ArrayList<>();
//        String[] tokens = expression.split("\\+");
//        for (String token : tokens) {
//            if (token.matches("[A-Z]+\\d+")) {
//                System.out.println("Dependency: " + token);
//                dependencies.add(token);
//            }
//        }
//        return dependencies;
//    }

    private boolean isCyclicDependency(Expression cell, Expression dependent, boolean firstCall) {
        System.out.println("Checking " + cell.row + " " + cell.col + ", " + dependent.row + " " + dependent.col);
        if (cell == dependent && !firstCall) {
            System.out.println("chuj");
            return true;
        }
        for (Expression dependency : cell.getDependencies()) {
            System.out.println("checkingDependency: " + dependency.row + " " + dependency.col);
            if (isCyclicDependency(dependency, dependent, false)) {
                System.out.println("chuj2");
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
            System.out.println("New Dependency: " + dependency);
            int depCol = dependency.charAt(0) - 'A';
            int depRow = Integer.parseInt(dependency.substring(1)) - 1;
            expressions[depRow][depCol].addDependent(coordinatesToCell(row, col), expressions[row][col]);
            expressions[row][col].addDependency(dependency, expressions[depRow][depCol]);
        }
        if (isCyclicDependency(expressions[row][col], expressions[row][col], true)) {
            System.out.println("Cyclic dependency");
            expressions[row][col].updateExpression(text, "#REF!");
        } else {
//            System.out.println("No cyclic dependency");
            expressions[row][col].updateExpression(text);
        }
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
