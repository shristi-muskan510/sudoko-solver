//Importing necessary libraries
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.MatteBorder;

public class game extends JFrame {

    private JTextField[][] sudokuFields = new JTextField[9][9]; //Creates a 9x9 textfield for sudoko
    private int selectedNumber = 0; // Keeps track of the currently selected number

    public game() {
        //Frame properties
        setTitle("Sudoku Solver");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create the Sudoku grid panel
        JPanel gridPanel = new JPanel(new GridLayout(9, 9));

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                JTextField textField = new JTextField();
                textField.setHorizontalAlignment(JTextField.CENTER);

                // Add thicker borders for 3x3 grid separation
                int top = (row % 3 == 0) ? 3 : 1;
                int left = (col % 3 == 0) ? 3 : 1;
                int bottom = (row == 8) ? 3 : 1;
                int right = (col == 8) ? 3 : 1;
                textField.setBorder(new MatteBorder(top, left, bottom, right, Color.BLACK));

                // Allow users to "paint" cells with the selected number
                textField.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent e) {
                        if (selectedNumber > 0) {
                            textField.setText(String.valueOf(selectedNumber)); //Inputs the number into cell on selecting
                        }
                    }
                });

                sudokuFields[row][col] = textField; //store the textfield into 2D array
                gridPanel.add(textField); //add textfield into grid panel
            }
        }

        // Panel for number buttons (1-9)
        JPanel numberPanel = new JPanel(new GridLayout(1, 9));
        for (int i = 1; i <= 9; i++) {
            int number = i;
            JButton numberButton = new JButton(String.valueOf(i));
            numberButton.setFont(new Font("Arial", Font.BOLD, 16));
            numberButton.addActionListener(e -> selectedNumber = number); // Set the selected number
            numberPanel.add(numberButton); //add button to number panel
        }

        // Reset Button
        JButton resetButton = new JButton("Reset");
        resetButton.setFont(new Font("Arial", Font.BOLD, 16));
        resetButton.addActionListener(e -> {
            // Clear all cells and reset the selected number
            for (int row = 0; row < 9; row++) {
                for (int col = 0; col < 9; col++) {
                    sudokuFields[row][col].setText(""); //clear each cell
                }
            }
            selectedNumber = 0;
        });

        // Solve Button
        JButton solveButton = new JButton("Solve");
        solveButton.setFont(new Font("Arial", Font.BOLD, 16));
        solveButton.addActionListener(new SolveButtonListener());

        // Layout for control panel
        JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.add(numberPanel, BorderLayout.NORTH); // add numbers button to top of control panel
        controlPanel.add(resetButton, BorderLayout.CENTER); // add reset button to center of control panel
        controlPanel.add(solveButton, BorderLayout.SOUTH); // add solve button to end of control panel

        // Add components to the frame
        add(gridPanel, BorderLayout.CENTER); // add grid panel to center of frame
        add(controlPanel, BorderLayout.SOUTH); // add control panel to end of frame
    }

    // Event handler for the Solve button
    private class SolveButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            ArrayList<ArrayList<Integer>> board = new ArrayList<>();

            // Get values from JTextFields and populate the ArrayList board
            for (int row = 0; row < 9; row++) {
                ArrayList<Integer> rowList = new ArrayList<>();
                for (int col = 0; col < 9; col++) {
                    String value = sudokuFields[row][col].getText();
                    if (value.isEmpty()) {
                        rowList.add(0);  // Empty cell
                    } else {
                        rowList.add(Integer.parseInt(value));
                    }
                }
                board.add(rowList);
            }

            //Validate the sudoko board before solving
            if (!validateBoard(board)) {
                JOptionPane.showMessageDialog(null, "Invalid Sudoku puzzle!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Solve the sudoko puzzle
            if (solveSudoku(board)) {
                // Update the grid with the solved puzzle
                for (int row = 0; row < 9; row++) {
                    for (int col = 0; col < 9; col++) {
                        sudokuFields[row][col].setText(String.valueOf(board.get(row).get(col)));
                    }
                }
                JOptionPane.showMessageDialog(null, "Sudoku Solved!");
            }
        }
    }

    // Validate if the current board is valid
    private boolean validateBoard(ArrayList<ArrayList<Integer>> board) {
        // Check rows
        for (int row = 0; row < 9; row++) {
            boolean[] seen = new boolean[10];
            for (int col = 0; col < 9; col++) {
                int value = board.get(row).get(col);
                if (value != 0) {
                    if (seen[value]) {
                        return false;
                    }
                    seen[value] = true;
                }
            }
        }

        // Check columns
        for (int col = 0; col < 9; col++) {
            boolean[] seen = new boolean[10];
            for (int row = 0; row < 9; row++) {
                int value = board.get(row).get(col);
                if (value != 0) {
                    if (seen[value]) {
                        return false;
                    }
                    seen[value] = true;
                }
            }
        }

        // Check 3x3 subgrids
        for (int boxRow = 0; boxRow < 3; boxRow++) {
            for (int boxCol = 0; boxCol < 3; boxCol++) {
                boolean[] seen = new boolean[10];
                for (int row = boxRow * 3; row < boxRow * 3 + 3; row++) {
                    for (int col = boxCol * 3; col < boxCol * 3 + 3; col++) {
                        int value = board.get(row).get(col);
                        if (value != 0) {
                            if (seen[value]) {
                                return false;
                            }
                            seen[value] = true;
                        }
                    }
                }
            }
        }

        return true;
    }

    // Solve the Sudoku puzzle using backtracking
    private boolean solveSudoku(ArrayList<ArrayList<Integer>> board) {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (board.get(row).get(col) == 0) {  // Empty cell
                    for (int value = 1; value <= 9; value++) {
                        if (isSafe(board, row, col, value)) {
                            board.get(row).set(col, value);
                            if (solveSudoku(board)) {
                                return true;
                            }
                            board.get(row).set(col, 0);  // Backtracking (Reset cell)
                        }
                    }
                    return false;
                }
            }
        }
        return true;  // Solved
    }

    // Check if it's safe to place a value in a given cell
    private boolean isSafe(ArrayList<ArrayList<Integer>> board, int row, int col, int value) {
        // Check row
        for (int i = 0; i < 9; i++) {
            if (board.get(row).get(i) == value) {
                return false;
            }
        }

        // Check column
        for (int i = 0; i < 9; i++) {
            if (board.get(i).get(col) == value) {
                return false;
            }
        }

        // Check 3x3 subgrid
        int startRow = row - row % 3;
        int startCol = col - col % 3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board.get(i + startRow).get(j + startCol) == value) {
                    return false;
                }
            }
        }

        return true;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            game frame = new game();
            frame.setVisible(true); // To make game window visible to user
        });
    }
}
