import javax.swing.table.DefaultTableModel;

public class Expression {
    private String expression;
    private Object value;
    private DefaultTableModel model;

    public Expression() {
        update_expression("");
    }

    public void update_expression(String expression) {
        this.expression = expression;
        if (expression.isEmpty()) {
            this.value = "";
            return;
        }
        String[] tokens = expression.split("\\+");
        int sum = 0;
        for (String token : tokens) {
            sum += Integer.parseInt(token);
        }
        this.value = sum;
    }

    public boolean error() {
        return this.value instanceof String;
    }

    public Object getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return this.expression;
    }


}
