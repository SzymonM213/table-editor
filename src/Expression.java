import javax.swing.table.DefaultTableModel;
import java.util.*;
import java.util.function.BinaryOperator;

public class Expression {
    public final int row;
    public final int col;
    private String expression;
    private Result value;
    public boolean hasValue;
    private DefaultTableModel model;
    private Map<String, Expression> dependencies; // Cells on which this cell depends
    private Map<String, Expression> dependents; // Cells which value depends on this cell

    public Expression(int row, int col) {
        this.row = row;
        this.col = col;
        this.dependents = new HashMap<>();
        this.dependencies = new HashMap<>();
        this.expression = "";
        this.hasValue = false;
//        updateExpression("");
    }

    public void updateExpression(String expression) {
        this.expression = expression;
        reevaluate(this);
    }

    public void updateExpression(String expression, String exception) {
        this.expression = expression;
        this.value = new Result(exception);
        this.hasValue = true;
        reevaluateDependents(this);
    }

    // returns null if the expression is invalid
    private List<String> validateAndTokenize() {
        List<String> functions = Arrays.asList("ADD", "SUB", "MUL", "DIV", "NEG", "ABS", "POW", "MOD");
        List<String> tokens = new LinkedList<>();
        int i = 1;
        while (i < this.expression.length()) {
            char c = this.expression.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i == this.expression.length() - 1) return null;
                if (this.expression.charAt(i + 1) >= '0' && this.expression.charAt(i + 1) <= '9') {
                    StringBuilder sb = new StringBuilder();
                    sb.append(c);
                    while(i + 1 < this.expression.length() && this.expression.charAt(i + 1) >= '0' && this.expression.charAt(i + 1) <= '9') {
                        sb.append(this.expression.charAt(i + 1));
                        i++;
                    }
                    System.out.println("Dependency: " + sb.toString());
                    tokens.add(this.dependencies.get(sb.toString()).getValue().toString());
                    i++;
                } else {
                    StringBuilder sb = new StringBuilder();
                    sb.append(c);
                    while (i + 1 < this.expression.length() && Character.isLetter(this.expression.charAt(i + 1))) {
                        sb.append(this.expression.charAt(i + 1));
                        i++;
                    }
                    if (!functions.contains(sb.toString())) {
                        return null;
                    }
                    tokens.add(sb.toString());
                    i += 1;
                }
            } else if (Character.isDigit(c)) {
                StringBuilder sb = new StringBuilder();
                sb.append(c);
                while (i + 1 < this.expression.length() && Character.isDigit(this.expression.charAt(i + 1))) {
                    sb.append(this.expression.charAt(i + 1));
                    i++;
                }
                i++;
                tokens.add(sb.toString());
                System.out.println("Number: " + sb.toString());
            } else if (c == '+' || c == '-' || c == '*' || c == '/' || c == '(' || c == ')' || c == ',') {
                tokens.add(String.valueOf(c));
                i++;
            } else if (c == ' ') {
                i++;
            } else {
                return null;
            }
        }
        return tokens;
    }

    public void reevaluate(Expression caller) {
        System.out.println("Reevaluating " + this.row + " " + this.col + " caller: " + caller.row + " " + caller.col);
        System.out.println("Expression: " + this.expression);
        if (this.expression.isEmpty()) {
            this.value = new Result(0);
        } else if (!this.expression.startsWith("=") && !this.expression.matches("\\d+")) {
            this.hasValue = false;
        } else if (this.expression.matches("\\d+")) {
            this.hasValue = true;
            this.value = new Result(Integer.parseInt(this.expression));
        } else {
//            System.out.println("Reevaluating " + this.row + " " + this.col + " caller: " + caller.row + " " + caller.col);
//            this.hasValue = true;
//            String exp = this.expression.substring(1);
//            if (exp.matches("[A-Z]+\\d+")) {
////            this.value = dependencies.get(expression).getValue();
//                System.out.println(dependencies.get(exp));
//                this.value = dependencies.get(exp).getValue();
//            } else {
//                String[] tokens = exp.split("\\+");
//                int sum = 0;
//                for (String token : tokens) {
//                    sum += Integer.parseInt(token);
//                }
//                this.value = new Result(sum);
//            }
            List<String> tokens = validateAndTokenize();
            this.value = new Result(tokens);
            this.hasValue = true;

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
        return this.value.getTextValue() != null;
    }

    public Result getValue() {
        if (this.expression.isEmpty()) {
            return new Result("");
        }
        return this.value;
    }

    public void setValue(Result value) {
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

    public List<Expression> getDependents() {
        return new ArrayList<>(this.dependents.values());
    }

    public static List<String> getExpressionDependencies(String expression) {
        if (!expression.startsWith("=")) {
            return new ArrayList<>();
        }
        // exclude all 'UpperCaseLetter' + 'Digit' cells from the expression
        expression = expression.substring(1);
        List<String> dependencies = new ArrayList<>();
        for (int i = 0; i < expression.length(); i++) {
            if (Character.isUpperCase(expression.charAt(i))) {
                StringBuilder sb = new StringBuilder();
                sb.append(expression.charAt(i));
                i++;
                if (i == expression.length() || !Character.isDigit(expression.charAt(i))) {
                    continue;
                }
                while (i < expression.length() && Character.isDigit(expression.charAt(i))) {
                    sb.append(expression.charAt(i));
                    i++;
                }
                System.out.println("chuj Dependency: " + sb.toString());
                dependencies.add(sb.toString());
            }
        }
        return dependencies;
    }

//    @Override
//    public boolean equals(Object obj) {
//        if (obj == this) return true;
//        if (!(obj instanceof Expression)) return false;
//        Expression e = (Expression) obj;
//        return e.row == this.row && e.col == this.col;
//    }




}
