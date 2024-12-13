import java.util.Arrays;

public class SimplexMethod {
    public static void main(String[] args) {
        double[][] A = {{1, 1}, {1, -1}, {-1, 0}};
        double[] b = {6, 2, -1};
        double[] c = {2, 3};
        boolean isMaximization = true;
        char[] constraintTypes = {'≤', '≤', '≤'}; // Mudando '≥' para '≤' para garantir viabilidade

        SimplexMethod simplex = new SimplexMethod(A, b, c, isMaximization, constraintTypes);
        simplex.solve();

        System.out.println("Solution: " + Arrays.toString(simplex.getSolution()));
        System.out.println("Objective value: " + simplex.getObjectiveValue());
    }

    private double[] b;
    private double[] c;
    private double[][] table;
    private int numberOfConstraints;
    private int numberOfOriginalVariables;
    private int totalVariables;
    private static final double BIG_M = 1e6;
    private boolean isMaximization;
    private char[] constraintTypes;

    public SimplexMethod(double[][] A, double[] b, double[] c, boolean isMaximization, char[] constraintTypes) {
        this.b = b;
        this.c = c;
        this.isMaximization = isMaximization;
        this.constraintTypes = constraintTypes;
        this.numberOfConstraints = b.length;
        this.numberOfOriginalVariables = c.length;
        int slackOrSurplus = 0, artificial = 0;

        for (char type : constraintTypes) {
            if (type == '≤') slackOrSurplus++;
            if (type == '≥' || type == '=') artificial++;
        }

        totalVariables = numberOfOriginalVariables + slackOrSurplus + artificial;

        table = new double[numberOfConstraints + 1][totalVariables + 1];

        int slackIndex = numberOfOriginalVariables, artificialIndex = slackOrSurplus + numberOfOriginalVariables;
        for (int i = 0; i < numberOfConstraints; i++) {
            for (int j = 0; j < numberOfOriginalVariables; j++) {
                table[i][j] = A[i][j];
            }

            switch (constraintTypes[i]) {
                case '≤':
                    table[i][slackIndex++] = 1.0;
                    break;
                case '≥':
                    table[i][slackIndex++] = -1.0;
                    table[i][artificialIndex++] = 1.0;
                    break;
                case '=':
                    table[i][artificialIndex++] = 1.0;
                    break;
            }

            table[i][totalVariables] = b[i];
        }

        // Objective function
        for (int j = 0; j < numberOfOriginalVariables; j++) {
            table[numberOfConstraints][j] = isMaximization ? -c[j] : c[j];
        }
        for (int j = numberOfOriginalVariables + slackOrSurplus; j < totalVariables; j++) {
            table[numberOfConstraints][j] = isMaximization ? BIG_M : -BIG_M;
        }
    }

    public void solve() {
        boolean inPhaseOne = true;
        int iteration = 0;

        while (true) {
            System.out.println("Iteration " + iteration);
            printTable();

            int pivotColumn = getPivotColumn(inPhaseOne);
            System.out.println("Pivot Column: " + pivotColumn);

            if (pivotColumn == -1) {
                // Lógica para Fase 1 e Fase 2
                if (inPhaseOne) {
                    if (isPhaseOneOver()) {
                        inPhaseOne = false;
                        removeArtificialVariables();
                        for (int j = 0; j < numberOfOriginalVariables; j++) {
                            table[numberOfConstraints][j] = isMaximization ? -c[j] : c[j];
                        }
                        for (int j = numberOfOriginalVariables; j < totalVariables; j++) {
                            table[numberOfConstraints][j] = 0;
                        }
                        System.out.println("Moving to Phase 2");
                    } else {
                        throw new ArithmeticException("No feasible solution");
                    }
                } else {
                    System.out.println("Optimal solution reached");
                    break;
                }
            } else {
                int pivotRow = getPivotRow(pivotColumn);
                if (pivotRow == -1) {
                    throw new ArithmeticException("Unbounded solution");
                }
                pivot(pivotRow, pivotColumn);
                iteration++;
            }
        }
        System.out.println("Final Tableau:");
        printTable();
    }

    private void printTable() {
        for (int i = 0; i <= numberOfConstraints; i++) {
            for (int j = 0; j <= totalVariables; j++) {
                System.out.printf("%8.2f ", table[i][j]);
            }
            System.out.println();
        }
        System.out.println();
    }

    private int getPivotColumn(boolean inPhaseOne) {
        int pivotColumn = -1;
        double worstValue = inPhaseOne ? Double.POSITIVE_INFINITY : 0; // Use infinity for phase 1

        for (int j = 0; j < totalVariables; j++) {
            double value = table[numberOfConstraints][j];
            if (inPhaseOne) {
                // In Phase 1, we want to find a column where the artificial variable can be made zero
                if (value > 0 && (pivotColumn == -1 || value < worstValue)) {
                    worstValue = value;
                    pivotColumn = j;
                }
            } else {
                // In Phase 2, for maximization, look for negative values to improve
                if (value < worstValue) {
                    worstValue = value;
                    pivotColumn = j;
                }
            }
        }

        return pivotColumn;
    }

    private int getPivotRow(int pivotColumn) {
        int pivotRow = -1;
        double minRatio = Double.POSITIVE_INFINITY;  // Iniciar com infinito positivo

        for (int i = 0; i < numberOfConstraints; i++) {
            if (table[i][pivotColumn] > 0) {  // Só consideramos linhas onde o elemento da coluna pivô é positivo
                double ratio = table[i][totalVariables] / table[i][pivotColumn];
                if (ratio < minRatio) {
                    minRatio = ratio;
                    pivotRow = i;
                }
            }
        }

        return pivotRow;
    }

    private void pivot(int pivotRow, int pivotColumn) {
        double pivotValue = table[pivotRow][pivotColumn];

        // Normalize pivot row
        for (int j = 0; j <= totalVariables; j++) {
            table[pivotRow][j] /= pivotValue;
        }

        // Update other rows
        for (int i = 0; i <= numberOfConstraints; i++) {
            if (i != pivotRow) {
                double factor = table[i][pivotColumn];
                for (int j = 0; j <= totalVariables; j++) {
                    table[i][j] -= factor * table[pivotRow][j];
                }
            }
        }
    }

    private boolean isPhaseOneOver() {
        for (int j = numberOfOriginalVariables + (numberOfConstraints - numberOfArtificialVariables()); j < totalVariables; j++) {
            if (table[numberOfConstraints][j] > 1e-6) return false; // Check if all artificial variables in the objective function are zero
        }
        return true;
    }

    private int numberOfArtificialVariables() {
        int count = 0;
        for (char type : constraintTypes) {
            if (type == '≥' || type == '=') count++;
        }
        return count;
    }

    private void removeArtificialVariables() {
        int numberOfArtificial = numberOfArtificialVariables();
        int newVariablesCount = totalVariables - numberOfArtificial;
        double[][] newTable = new double[numberOfConstraints + 1][newVariablesCount + 1];

        for (int i = 0; i <= numberOfConstraints; i++) {
            int originalVarCount = numberOfOriginalVariables;
            int newColIndex = 0;
            for (int j = 0; j < totalVariables; j++) {
                if (j < originalVarCount || (j >= originalVarCount + numberOfConstraints && j < originalVarCount + numberOfConstraints + (newVariablesCount - originalVarCount))) {
                    newTable[i][newColIndex] = table[i][j];
                    newColIndex++;
                }
            }
            newTable[i][newVariablesCount] = table[i][totalVariables];
        }
        this.table = newTable;
        this.totalVariables = newVariablesCount;
    }

    public double[] getSolution() {
        double[] solution = new double[numberOfOriginalVariables];
        Arrays.fill(solution, 0.0);

        for (int i = 0; i < numberOfConstraints; i++) {
            boolean isBasic = true;
            int basicVarIndex = -1;

            // Identificar a variável básica para cada linha
            for (int j = 0; j < numberOfOriginalVariables; j++) {
                if (Math.abs(table[i][j]) > 1e-6) {
                    if (basicVarIndex != -1) {
                        isBasic = false;  // Se houver mais de um valor não zero, não é uma variável básica
                        break;
                    }
                    basicVarIndex = j;
                }
            }

            // Se for uma variável básica, atribua o valor correspondente
            if (isBasic && basicVarIndex != -1) {
                solution[basicVarIndex] = table[i][totalVariables];
            }
        }

        return solution;
    }

    public double getObjectiveValue() {
        return table[numberOfConstraints][totalVariables];
    }
}