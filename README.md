## Table Editor
Simple spreadsheet-like table editor. It evaluates every correct
expression that starts with '='. It supports:
- basic arithmetic operations (+, -, *, /) 
- parentheses
- unary minus
- two-argument functions:
  - ADD (adds two numbers)  
  - SUB (subtracts two numbers)
  - MUL (multiplies two numbers)
  - DIV (divides two numbers)
  - POW (raises the first number to the power of the second number)
- and one-argument functions:
  - NEG (negates a number)
  - ABS (returns the absolute value of a number)
  - SQRT (returns the square root of a number)
- cell references in expressions, which can be done by
    using the cell's name (e.g. A1, B2, C3 where A, B, C are columns and 
    1, 2, 3 are rows).

It's best to navigate the table using mouse only (you can also use arrow keys,
but some cells won't refresh its value until you click on them).

Some basic examples of usage are provided in the Main class. You can
also run the main method of the SpreadsheetEditor class to display 
an empty table (and modify it on your own). 