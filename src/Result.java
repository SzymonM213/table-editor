import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Result {
    private String textValue;
    private double doubleValue;
    private int parseIndex;
    public static final Map<String, Function<Result, Result>> oneArgFunctions = Map.of(
            "NEG", Result::negative,
            "ABS", Result::absolute
    );
    public static final Map<String, BiFunction<Result, Result, Result>> twoArgFunctions = Map.of(
            "ADD", Result::add,
            "SUB", Result::subtract,
            "MUL", Result::multiply,
            "DIV", Result::divide,
            "POW", Result::power
    );

    public Result(String textValue) {
        this.textValue = textValue;
    }

    public Result(double doubleValue) {
        this.doubleValue = doubleValue;
    }

    public Result(List<String> tokens) {
        if (tokens == null) {
            this.textValue = "#NAME?1";
        } else {
            int openedParentheses = 0;
            Stack<Result> values = new Stack<>();
            Stack<Character> operators = new Stack<>();
            this.parseIndex = 0;
            while (this.parseIndex < tokens.size()) {
                if (twoArgFunctions.containsKey(tokens.get(this.parseIndex))) {
                    // two arguments function
                    values.push(twoArgOperation(tokens));
                } else if (oneArgFunctions.containsKey(tokens.get(this.parseIndex))) {
                    // one argument function
                    values.push(oneArgOperation(tokens));
                } else if (tokens.get(this.parseIndex).equals("(")) {
                    // open parentheses
                    openedParentheses++;
                    operators.push('(');
                } else if (tokens.get(this.parseIndex).equals(")")) {
                    // close parentheses
                    openedParentheses--;
                    if (openedParentheses < 0) {
                        this.textValue = "#NAME?2";
                        return;
                    }
                    while (operators.peek() != '(') {
                        values.push(applyOperator(values.pop(), values.pop(), operators.pop()));
                    }
                    operators.pop();
                } else if (tokens.get(this.parseIndex).equals("+") || tokens.get(this.parseIndex).equals("-") ||
                        tokens.get(this.parseIndex).equals("*") || tokens.get(this.parseIndex).equals("/")) {
                    // operators
                    while (!operators.isEmpty() && hasPrecedence(tokens.get(this.parseIndex).charAt(0), operators.peek())) {
                        values.push(applyOperator(values.pop(), values.pop(), operators.pop()));
                    }
                    operators.push(tokens.get(this.parseIndex).charAt(0));
                } else if (tokens.get(this.parseIndex).equals(",")) {
                    // argument separator (should not be here)
                    this.textValue = "#NAME?4";
                    return;
                } else if (tokens.get(this.parseIndex).matches("-?\\d+(\\.\\d+)?")) {
                    values.push(new Result(Double.parseDouble(tokens.get(this.parseIndex))));
                } else {
                    values.push(new Result(tokens.get(this.parseIndex)));
                }
                this.parseIndex++;
            }
            while (!operators.isEmpty()) {
                values.push(applyOperator(values.pop(), values.pop(), operators.pop()));
            }
            if (values.size() != 1) {
                this.textValue = "#NAME?6";
            } else {
                Result result = values.pop();
                this.textValue = result.textValue;
                this.doubleValue = result.doubleValue;
            }
        }
    }

    private static boolean hasPrecedence(char op1, char op2) {
        if (op2 == '(' || op2 == ')') {
            return false;
        }
        return (op1 != '*' && op1 != '/') || (op2 != '+' && op2 != '-');
    }

    private Result applyOperator(Result a, Result b, char op) {
        return switch (op) {
            case '+' -> add(b, a);
            case '-' -> subtract(b, a);
            case '*' -> multiply(b, a);
            case '/' -> divide(b, a);
            default -> new Result("#NAME?7");
        };
    }

    private Result oneArgOperation(List<String> tokens) {
        String operation = tokens.get(this.parseIndex);
        if (this.parseIndex + 1 == tokens.size() ||  !tokens.get(this.parseIndex + 1).equals("(")) {
            return new Result("#NAME?8");
        }
        this.parseIndex += 2;
        List<String> arg = new ArrayList<>();
        int openedParentheses = 0;
        while (openedParentheses >= 0 && this.parseIndex != tokens.size() && !tokens.get(this.parseIndex).equals(")")) {
            openedParentheses = getOpenedParentheses(tokens, arg, openedParentheses);
        }
        if (this.parseIndex == tokens.size() || !tokens.get(this.parseIndex).equals(")")) {
            return new Result("#NAME?9");
        }
        Result result = new Result(arg);
        return oneArgFunctions.get(operation).apply(result);
    }

    private int getOpenedParentheses(List<String> tokens, List<String> arg, int openedParentheses) {
        if (tokens.get(this.parseIndex).equals("(")) {
            openedParentheses++;
        } else if (tokens.get(this.parseIndex).equals(")")) {
            openedParentheses--;
        }
        arg.add(tokens.get(this.parseIndex));
        this.parseIndex++;
        return openedParentheses;
    }

    private Result twoArgOperation(List<String> tokens) {
        String operation = tokens.get(this.parseIndex);
        if (this.parseIndex + 1 == tokens.size() ||  !tokens.get(this.parseIndex + 1).equals("(")) {
            return new Result("#NAME?10.5");
        }
        this.parseIndex += 2;
        List<String> arg1 = new ArrayList<>();
        List<String> arg2 = new ArrayList<>();
        int openedParentheses = 0;
        while (this.parseIndex != tokens.size() && (openedParentheses > 0 || !tokens.get(this.parseIndex).equals(","))) {
            openedParentheses = getOpenedParentheses(tokens, arg1, openedParentheses);
        }
        if (this.parseIndex == tokens.size()) {
            return new Result("#NAME?11");
        }
        this.parseIndex++;
        while (this.parseIndex != tokens.size() && (openedParentheses > 0 || !tokens.get(this.parseIndex).equals(")"))) {
            openedParentheses = getOpenedParentheses(tokens, arg2, openedParentheses);
        }
        if (this.parseIndex == tokens.size()) {
            return new Result("#NAME?12");
        }
        Result result1 = new Result(arg1);
        Result result2 = new Result(arg2);
        return twoArgFunctions.get(operation).apply(result1, result2);
    }

    public static Result add(Result a, Result b) {
        if (a.textValue != null) {
            return new Result(a.textValue);
        } else if (b.textValue != null) {
            return new Result(b.textValue);
        } else {
            return new Result(a.doubleValue + b.doubleValue);
        }
    }

public static Result subtract(Result a, Result b) {
        if (a.textValue != null) {
            return new Result(a.textValue);
        } else if (b.textValue != null) {
            return new Result(b.textValue);
        } else {
            return new Result(a.doubleValue - b.doubleValue);
        }
    }

    public static Result multiply(Result a, Result b) {
        if (a.textValue != null) {
            return new Result(a.textValue);
        } else if (b.textValue != null) {
            return new Result(b.textValue);
        } else {
            return new Result(a.doubleValue * b.doubleValue);
        }
    }

    public static Result divide(Result a, Result b) {
        if (a.textValue != null) {
            return new Result(a.textValue);
        } else if (b.textValue != null) {
            return new Result(b.textValue);
        } else if (a.doubleValue == 0) {
            return new Result("DIV/0!");
        }
        else {
            return new Result(a.doubleValue / b.doubleValue);
        }
    }

    public static Result power(Result a, Result b) {
        if (a.textValue != null) {
            return new Result(a.textValue);
        } else if (b.textValue != null) {
            return new Result(b.textValue);
        } else if (a.doubleValue == 0 && b.doubleValue < 0) {
            return new Result("DIV/0!");
        }
        else {
            return new Result(Math.pow(a.doubleValue, b.doubleValue));
        }
    }

    public static Result negative(Result a) {
        if (a.textValue != null) {
            return new Result(a.textValue);
        } else {
            return new Result(-a.doubleValue);
        }
    }

    public static Result absolute(Result a) {
        if (a.textValue != null) {
            return new Result(a.textValue);
        } else {
            return new Result(Math.abs(a.doubleValue));
        }
    }

    public String toString() {
        if (textValue != null) {
            return textValue;
        } else {
            return Double.toString(this.doubleValue);
        }
    }

}
