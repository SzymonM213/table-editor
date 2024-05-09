import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.util.function.Consumer;

public class CellListener implements TableModelListener {
    private JTable table;
    private Consumer<String> listener;

    public CellListener(JTable table, Consumer<String> listener) {
        this.table = table;
        this.listener = listener;
        this.table.getModel().addTableModelListener(this);
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        int row = e.getFirstRow();
        int column = e.getColumn();
        if (row >= 0 && column >= 0) {
            TableModel model = (TableModel) e.getSource();
            String value = (String) model.getValueAt(row, column);
            listener.accept(value);
        }
    }
}
