import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        String[] columnNames = {"Column 1", "Column 2", "Column 3"};
        Object[][] data = {
                {"Row 1, Col 1", "Row 1, Col 2", "Row 1, Col 3"},
                {"Row 2, Col 1", "Row 2, Col 2", "Row 2, Col 3"},
                {"Row 3, Col 1", "Row 3, Col 2", "Row 3, Col 3"}
        };

        JTable table = new JTable(data, columnNames);
        frame.add(new JScrollPane(table));
        frame.pack();
        frame.setVisible(true);

        // Add listener to individual cells
        CellListener cellListener = new CellListener(table, value -> {
            System.out.println("Cell value changed: " + value);
        });
    }
}