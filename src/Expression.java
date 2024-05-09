import javax.swing.table.DefaultTableModel;
import java.util.*;

public class Expression {
    public final int row;
    public final int col;
    private String expression;
    private Object value;
    private DefaultTableModel model;
    private Map<String, Expression> dependencies; // Cells on which this cell depends
    private Map<String, Expression> dependents; // Cells which value depends on this cell

    public Expression(int row, int col) {
        this.row = row;
        this.col = col;
        this.dependents = new HashMap<>();
        this.dependencies = new HashMap<>();
        updateExpression("");
    }

    public void updateExpression(String expression) {
        this.expression = expression;
        reevaluate(this);
    }

    public void updateExpression(String expression, String exception) {
        this.expression = expression;
        this.value = exception;
        reevaluateDependents(this);
    }

    public void reevaluate(Expression caller) {
        System.out.println("Reevaluating " + this.row + " " + this.col);
        if (expression.isEmpty()) {
            this.value = "";
        } else if (expression.matches("[A-Z]+\\d+")) {
            this.value = dependencies.get(expression).getValue();
        } else {
            String[] tokens = expression.split("\\+");
            int sum = 0;
            for (String token : tokens) {
                sum += Integer.parseInt(token);
            }
            this.value = sum;
        }
        reevaluateDependents(caller);
    }

//    public void reevaluateDependents() {
//        System.out.println("Reevaluating dependents of " + this.row + " " + this.col);
//        for (Expression dependent : dependents.values()) {
//            dependent.reevaluate(false);
//        }
//    }

    public void reevaluateDependents(Expression caller) {
        System.out.println("Reevaluating dependents of " + this.row + " " + this.col);
        for (Expression dependent : dependents.values()) {
            if (dependent != caller) {
                dependent.reevaluate(caller);
            }
        }
    }

    public boolean error() {
        return this.value instanceof String;
    }

    public Object getValue() {
        return this.value;
    }

    public void setValue(Object value) {
        this.value = value;
        reevaluateDependents(this);
    }

    @Override
    public String toString() {
        return this.expression;
    }

    public void removeDependent(Expression dependent) {
        this.dependents.values().remove(dependent);
    }

    public void clearDependencies() {
        this.dependencies = new HashMap<>();
    }

    public void printDependents() {
        System.out.println("Dependents of " + this.row + " " + this.col);
        for (Expression dependent : this.dependents.values()) {
            System.out.println(dependent.row + " " + dependent.col);
        }
    }

//    private void setExpression(String expression) {
//        this.expression = expression;
//        for (Expression dependency : this.dependencies.values()) {
//            dependency.removeDependent(this);
//        }
//        this.dependencies = new HashMap<>();
//    }

    public void addDependent(String key, Expression dependent) {
        System.out.println("localization: " + key);
        this.dependents.put(key, dependent);
    }

    public void addDependency(String key, Expression dependency) {
        this.dependencies.put(key, dependency);
    }

    public List<Expression> getDependencies() {
        return new ArrayList<>(this.dependencies.values());
    }


}
