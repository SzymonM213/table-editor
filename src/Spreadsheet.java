import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.HashMap;
import java.util.Map;

public class Spreadsheet extends JFrame implements Observer {

    private final int ROWS = 20;
    private final int COLS = 10;
    private final JTextField[][] cells;
    private final Integer[][] values;

    public Spreadsheet() {
        setTitle("Simple Spreadsheet Editor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(ROWS, COLS));

        cells = new Cell[ROWS][COLS];
        values = new Integer[ROWS][COLS];

        Border border = BorderFactory.createLineBorder(Color.BLACK);

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                final Cell cell = new Cell(row, col);
                cell.setBorder(border);
                cell.setHorizontalAlignment(JTextField.LEFT);
                cells[row][col] = cell;
                add(cell);
            }
        }

        setSize(800, 600);
        setVisible(true);
    }

    @Override
    public void update(int row, int col, int value) {}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Spreadsheet());
    }
}
