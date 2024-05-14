import java.util.*;

public class Expression {
    private String expression;
    private Result value;
    public boolean hasValue;
    private Map<String, Expression> dependencies; // Cells on which this cell depends
    private final Map<String, Expression> dependents; // Cells which value depends on this cell

    public Expression() {
        this.dependents = new HashMap<>();
        this.dependencies = new HashMap<>();
        this.expression = "";
        this.hasValue = false;
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
        List<String> tokens = new LinkedList<>();
        int i = 1;
        while (i < this.expression.length()) {
            char c = this.expression.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i == this.expression.length() - 1) return null;
                if (this.expression.charAt(i + 1) >= '0' && this.expression.charAt(i + 1) <= '9') {
                    // Cell reference
                    StringBuilder sb = new StringBuilder();
                    sb.append(c);
                    while(i + 1 < this.expression.length() && this.expression.charAt(i + 1) >= '0' && this.expression.charAt(i + 1) <= '9') {
                        sb.append(this.expression.charAt(i + 1));
                        i++;
                    }
                    if (!this.dependencies.containsKey(sb.toString())) return null;
                    if (!this.dependencies.get(sb.toString()).hasValue) return null;
                    tokens.add(this.dependencies.get(sb.toString()).getValue().toString());
                    i++;
                } else {
                    // Function name
                    StringBuilder sb = new StringBuilder();
                    sb.append(c);
                    while (i + 1 < this.expression.length() && Character.isUpperCase(this.expression.charAt(i + 1))) {
                        sb.append(this.expression.charAt(i + 1));
                        i++;
                    }
                    if (!Result.twoArgFunctions.containsKey(sb.toString()) &&
                            !Result.oneArgFunctions.containsKey(sb.toString())) {
                        return null;
                    }
                    tokens.add(sb.toString());
                    i += 1;
                }
            } else if (Character.isDigit(c)) {
                // Number
                StringBuilder sb = new StringBuilder();
                sb.append(c);
                boolean dot = false;
                while (i + 1 < this.expression.length() && (Character.isDigit(this.expression.charAt(i + 1)) ||
                        this.expression.charAt(i + 1) == '.')) {
                    if (this.expression.charAt(i + 1) == '.') {
                        if (dot) return null;
                        dot = true;
                    }
                    sb.append(this.expression.charAt(i + 1));
                    i++;
                }
                i++;
                tokens.add(sb.toString());
            } else if (c == '-' && (i == 1 || this.expression.charAt(i - 1) == '(' || this.expression.charAt(i - 1) == ',')) {
                // Unary minus
                tokens.add("-1");
                tokens.add("*");
                i++;
            } else if (c == '+' || c == '-' || c == '*' || c == '/' || c == '(' || c == ')' || c == ',') {
                // Operator
                if (c != '(' && i == 1) return null;
                if (i > 1) {
                    String last = tokens.get(tokens.size() - 1);
                    if (c == ')' && last.equals("(")) return null;
                    if (!last.matches("-?\\d+(.\\d+)?|\\)")
                            && !Result.twoArgFunctions.containsKey(last)
                            && !Result.oneArgFunctions.containsKey(last)) return null;
                }
                tokens.add(String.valueOf(c));
                i++;
            } else if (c == ' ') {
                i++;
            } else {
                return null;
            }
        }
        if (!tokens.get(tokens.size()-1).matches("-?\\d+(.\\d+)?|\\)")) {
            return null;
        }
        return tokens;
    }

    public void reevaluate(Expression caller) {
        if (this.expression.isEmpty()) {
            this.value = new Result(0);
        } else if (!this.expression.startsWith("=") && !this.expression.matches("\\d+")) {
            this.hasValue = false;
        } else if (this.expression.matches("-?\\d+")) {
            this.hasValue = true;
            this.value = new Result(Integer.parseInt(this.expression));
        } else {
            List<String> tokens = validateAndTokenize();
            this.value = new Result(tokens);
            this.hasValue = true;

        }
        reevaluateDependents(caller);
    }

    public void reevaluateDependents(Expression caller) {
        for (Expression dependent : dependents.values()) {
            if (dependent != caller) {
                dependent.reevaluate(caller);
            }
        }
    }

    public Result getValue() {
        if (this.expression.isEmpty()) {
            return new Result("");
        }
        return this.value;
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

    public void addDependent(String key, Expression dependent) {
        this.dependents.put(key, dependent);
    }

    public void addDependency(String key, Expression dependency) {
        this.dependencies.put(key, dependency);
    }

    public List<Expression> getDependencies() {
        return new ArrayList<>(this.dependencies.values());
    }

    public static List<String> getExpressionDependencies(String expression) {
        if (!expression.startsWith("=")) {
            return new ArrayList<>();
        }
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
                String cell = sb.toString();
                if (cell.charAt(0) - 'A' < SpreadsheetEditor.COLS &&
                        Integer.parseInt(cell.substring(1)) <= SpreadsheetEditor.ROWS) {
                    dependencies.add(sb.toString());
                }
            }
        }
        return dependencies;
    }
}
