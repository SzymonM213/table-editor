public class Main {
    public static void main(String[] args) {
        SpreadsheetEditor editor = new SpreadsheetEditor();
        editor.setVisible(true);

        editor.setCellValue(0, 0, "=5");
        editor.setCellValue(0, 1, "=3");
        editor.setCellValue(1, 0, "=MUL(ADD(A1, B1), POW(A1,B1)) + 0"); // (5 + 3) * 5^3 + 0 = 1000
        editor.setCellValue(1, 1, "=ADD(   1, ADD(2, 3+ADD(4   , 5  + ADD(6, 7+ ADD(8, 9)))   ))"); // 45

        // Invalid expressions
        editor.setCellValue(0, 3, "Cause");
        editor.setCellValue(0, 4, "Error message");

        editor.setCellValue(1, 3, "Cyclic dependency");
        editor.setCellValue(1, 4, "=E2");

        editor.setCellValue(2, 3, "Division by zero");
        editor.setCellValue(2, 4, "=DIV(5, 0)");

        editor.setCellValue(3, 3, "Parenthesis mismatch");
        editor.setCellValue(3, 4, "=ADD(5, 0");

    }
}
