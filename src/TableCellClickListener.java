import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

public class TableCellClickListener implements MouseListener {
    private JTable table;

    public TableCellClickListener(JTable table) {
        this.table = table;
        this.table.addMouseListener(this);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int row = table.rowAtPoint(e.getPoint());
        int column = table.columnAtPoint(e.getPoint());

        if (row >= 0 && column >= 0) {
            table.editCellAt(row, column);
            Component editor = table.getEditorComponent();
            if (editor != null) {
                editor.requestFocus();
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        DefaultTableModel model = new DefaultTableModel(new Object[][]{{"Value 1", "Value 2"}, {"Value 3", "Value 4"}}, new Object[]{"Column 1", "Column 2"});
        JTable table = new JTable(model);
        new TableCellClickListener(table);
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
