package tictactoe;

import java.util.*;

class FieldState {
    char[][] state;
    int xNumber;
    int oNumber;

    FieldState(char[][] s, int x, int o) {
        state = s;
        xNumber = x;
        oNumber = o;
    }
}

class CheckResult {
    int winCombinations;
    boolean xWin;

    CheckResult(int win, boolean x) {
        winCombinations = win;
        xWin = x;
    }
}

class Move {
    int[] index;
    int score;

    Move() {
    }

    Move(int s) {
        score = s;
    }
}

public class Main {
    final static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        int s = 3;
        boolean end = false;
        char[] signs = {'X', 'O'};
        int i = 0;

        System.out.println("Input command:");
        String[] inputParams = scanner.nextLine().split("\\s+");

        while (inputParams.length != 3) {
            System.out.println("Bad parameters!");
            System.out.println("Input command:");

            inputParams = scanner.nextLine().split("\\s+");
        }

        String input = "         ";

        FieldState field = getFieldState(input, s);
        printField(field.state, s);

        while (!end) {
            String player = inputParams[i % 2 + 1];

            switch (player) {
                case "hard":
                    field = nextHardTurn(field, signs[i % 2], signs[i % 2]);
                    break;
                case "medium":
                    field = nextMediumTurn(field, signs[i % 2]);
                    break;
                case "easy":
                    field = nextEasyTurn(field, signs[i % 2]);
                    break;
                default:
                    field = nextTurn(field, signs[i % 2]);
                    break;
            }

            printField(field.state, s);
            CheckResult result = checkResult(field.state, s);
            end = outputResult(field.oNumber, field.xNumber, result.winCombinations, result.xWin);
            i++;
        }
    }

    private static FieldState nextTurn(FieldState field, char sign) {
        System.out.print("Enter the coordinates: ");

        int x;
        int y;

        String input = scanner.nextLine();

        input = input.replaceAll(" ", "");

        if (input.length() > 2) {
            System.out.println("You should enter numbers!");

            return nextTurn(field, sign);
        } else {
            x = Character.getNumericValue(input.charAt(0)) - 1;
            y = 3 - Character.getNumericValue(input.charAt(1));

            if (x > 2 || y > 2 || x < 0 || y < 0) {
                System.out.println("Coordinates should be from 1 to 3!");

                return nextTurn(field, sign);
            }

            if (field.state[y][x] == 'X' || field.state[y][x] == 'O') {
                System.out.println("This cell is occupied! Choose another one!");

                return nextTurn(field, sign);
            } else {
                field.state[y][x] = sign;

                if (sign == 'O') {
                    field.oNumber++;
                } else {
                    field.xNumber++;
                }
            }
        }

        return field;
    }

    private static FieldState nextEasyTurn(FieldState field, char sign) {
        System.out.print("Making move level \"easy\"");

        int x;
        int y;

        Random random = new Random();

        x = random.nextInt(3);
        y = random.nextInt(3);

        while (field.state[y][x] == 'X' || field.state[y][x] == 'O') {
            x = random.nextInt(3);
            y = random.nextInt(3);
        }

        field.state[y][x] = sign;

        if (sign == 'O') {
            field.oNumber++;
        } else {
            field.xNumber++;
        }

        return field;
    }

    private static FieldState nextMediumTurn(FieldState field, char sign) {
        System.out.print("Making move level \"medium\"");

        int x;
        int y;

        Random random = new Random();

        x = random.nextInt(3);
        y = random.nextInt(3);

        for (int i = 0; i < 3; i++) {
            if (field.state[i][0] == field.state[i][1] && field.state[i][2] == ' ') {
                x = i;
                y = 2;
            } else if (field.state[i][2] == field.state[i][1] && field.state[i][0] == ' ') {
                x = i;
                y = 0;
            } else if (field.state[i][0] == field.state[i][2] && field.state[i][1] == ' ') {
                x = i;
                y = 1;
            } else if (field.state[0][i] == field.state[1][i] && field.state[2][i] == ' ') {
                x = i;
                y = 2;
            } else if (field.state[2][i] == field.state[1][i] && field.state[0][i] == ' ') {
                x = i;
                y = 0;
            } else if (field.state[0][i] == field.state[2][i] && field.state[1][i] == ' ') {
                x = i;
                y = 1;
            }
        }

        if ((field.state[0][0] == field.state[2][2] || field.state[0][2] == field.state[2][0]) && field.state[1][1] == ' ') {
            x = 1;
            y = 1;
        } else if (field.state[1][1] == field.state[0][0] && field.state[2][2] == ' ') {
            x = 2;
            y = 2;
        } else if (field.state[1][1] == field.state[2][2] && field.state[0][0] == ' ') {
            x = 0;
            y = 0;
        } else if (field.state[1][1] == field.state[2][0] && field.state[0][2] == ' ') {
            x = 0;
            y = 2;
        } else if (field.state[1][1] == field.state[0][2] && field.state[2][0] == ' ') {
            x = 2;
            y = 0;
        }

        while (field.state[y][x] == 'X' || field.state[y][x] == 'O') {
            x = random.nextInt(3);
            y = random.nextInt(3);
        }

        field.state[y][x] = sign;

        if (sign == 'O') {
            field.oNumber++;
        } else {
            field.xNumber++;
        }

        return field;
    }

    private static FieldState nextHardTurn(FieldState field, char sign, char currentSign) {
        System.out.println("Making move level \"hard\"");

        Move bestMove = minimax(field, sign, currentSign);

        field.state[bestMove.index[0]][bestMove.index[1]] = currentSign;

        if (currentSign == 'O') {
            field.oNumber++;
        } else {
            field.xNumber++;
        }

        return field;
    }

    private static Move minimax(FieldState field, char sign, char currentSign) {
        char symbol = currentSign;
        char enemySymbol = currentSign == 'X' ? 'O' : 'X';

        int[][] availableSpots = emptyIndexes(field.state);

        if (winning(field.state, enemySymbol)) {
            return new Move(-10);
        } else if (winning(field.state, symbol)) {
            return new Move(10);
        } else if (!areThereEmptySpaces(field.state)) {
            return new Move(0);
        }

        List<Move> moves = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (availableSpots[i][j] == 1) {
                    Move move = new Move();
                    move.index = new int[]{i, j};
                    field.state[i][j] = symbol;

                    Move result = minimax(field, sign, enemySymbol);

                    move.score = result.score;

                    field.state[i][j] = ' ';
                    moves.add(move);
                }
            }
        }

        int bestMove = 0;

        if (sign == currentSign) {
            int bestScore = -10000;

            for (int i = 0; i < moves.size(); i++) {
                if (moves.get(i).score > bestScore) {
                    bestScore = moves.get(i).score;
                    bestMove = i;
                }
            }
        } else {
            int bestScore = 10000;

            for (int i = 0; i < moves.size(); i++) {
                if (moves.get(i).score < bestScore) {
                    bestScore = moves.get(i).score;
                    bestMove = i;
                }
            }
        }

        return moves.get(bestMove);
    }

    private static int[][] emptyIndexes(char[][] state) {
        int[][] empties = new int[3][3];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (state[i][j] == ' ') {
                    empties[i][j] = 1;
                } else {
                    empties[i][j] = 0;
                }
            }
        }

        return empties;
    }

    private static boolean areThereEmptySpaces(char[][] state) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (state[i][j] == ' ') {
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean winning(char[][] state, char sign) {
        return (state[0][0] == sign && state[0][1] == sign && state[0][2] == sign) ||
                (state[1][0] == sign && state[1][1] == sign && state[1][2] == sign) ||
                (state[2][0] == sign && state[2][1] == sign && state[2][2] == sign) ||
                (state[0][0] == sign && state[1][0] == sign && state[2][0] == sign) ||
                (state[0][1] == sign && state[1][1] == sign && state[2][1] == sign) ||
                (state[0][2] == sign && state[1][2] == sign && state[2][2] == sign) ||
                (state[0][0] == sign && state[1][1] == sign && state[2][2] == sign) ||
                (state[0][2] == sign && state[1][1] == sign && state[2][0] == sign);
    }

    private static FieldState getFieldState(String fieldInput, int size) {
        int xcount = 0;
        int ocount = 0;
        char[][] state = new char[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                state[i][j] = fieldInput.charAt(i * size + j);

                if (state[i][j] == 'O') {
                    ocount++;
                } else if (state[i][j] == 'X') {
                    xcount++;
                }
            }
        }

        return new FieldState(state, xcount, ocount);
    }

    private static void printField(char[][] state, int size) {
        String border = "---------";

        System.out.println(border);

        for (int i = 0; i < size; i++) {
            System.out.print("| ");

            for (int j = 0; j < size; j++) {
                System.out.print("" + state[i][j] + " ");
            }

            System.out.println("|");
        }

        System.out.println(border);
    }

    private static boolean outputResult(int ocount, int xcount, int win, boolean xwin) {

        if (Math.abs(ocount - xcount) > 1) {
            System.out.println("Impossible");
        } else {
            if (win > 1) {
                System.out.println("Impossible");
            } else if (win == 0) {
                if (ocount + xcount == 9) {
                    System.out.println("Draw");
                } else {
                    return false;
                }
            } else {
                if (xwin) {
                    System.out.println("X wins");
                } else {
                    System.out.println("O wins");
                }
            }
        }

        return true;
    }

    private static CheckResult checkResult(char[][] state, int size) {
        int win = 0;
        boolean xwin = false;

        for (int i = 0; i < size; i++) {
            if (state[i][0] == state[i][1] && state[i][0] == state[i][2] && state[i][0] != ' ') {
                win++;
                if (state[i][0] == 'X') {
                    xwin = true;
                }
            }

            if (state[0][i] == state[1][i] && state[0][i] == state[2][i] && state[0][i] != ' ') {
                win++;
                if (state[0][i] == 'X') {
                    xwin = true;
                }
            }
        }

        if (state[0][0] == state[1][1] && state[1][1] == state[2][2] && state[1][1] != ' ') {
            win++;
            if (state[0][0] == 'X') {
                xwin = true;
            }
        }

        if (state[0][2] == state[1][1] && state[1][1] == state[2][0] && state[1][1] != ' ') {
            win++;
            if (state[1][1] == 'X') {
                xwin = true;
            }
        }

        return new CheckResult(win, xwin);
    }
}