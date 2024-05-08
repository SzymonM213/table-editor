import javax.swing.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class Cell extends JTextField {
    private final int row;
    private final int column;
    private int value;
    private String text;

    public Cell(int row, int column) {
        this.row = row;
        this.column = column;
        this.text = "";
        this.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                setText(text);
            }

            @Override
            public void focusLost(FocusEvent e) {
                String new_text = getText();
                updateText(new_text);
            }
        });
    }

    private void updateText(String text) {
        this.text = text;
        if (!text.isEmpty() && text.charAt(0) == '=') {
            try {
                int expression_value = evaluate_expression(text.substring(1));
                this.value = expression_value;
                setText(String.valueOf(expression_value));
            } catch (NumberFormatException e) {
                this.value = 0;
                setText("ERROR");
            }
        } else {
            try {
                this.value = Integer.parseInt(text);
            } catch (NumberFormatException e) {
                this.value = 0;
            }
        }
    }

    private int evaluate_expression(String expression) {
        if (expression.isEmpty()) {
            return 0;
        }
        String[] tokens = expression.split("\\+");
        int sum = 0;
        for (String token : tokens) {
            sum += Integer.parseInt(token);
        }
        return sum;
    }
}
