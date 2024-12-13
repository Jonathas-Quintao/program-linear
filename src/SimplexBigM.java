public class SimplexBigM {

    // Método que implementa o algoritmo Simplex com o método Big M
    public static void simplexBigM(double[][] A, double[] b, double[] c) {
        int numVariables = c.length;
        int numConstraints = A.length;

        // Inicializando a matriz aumentada
        double[][] tableau = new double[numConstraints + 1][numVariables + numConstraints + 1];

        // Adicionando variáveis de folga e artificiais
        for (int i = 0; i < numConstraints; i++) {
            for (int j = 0; j < numVariables; j++) {
                tableau[i][j] = A[i][j];
            }
            tableau[i][numVariables + i] = 1; // Variáveis de folga
            tableau[i][tableau[0].length - 1] = b[i];
        }

        // Adicionando a função objetivo com as variáveis artificiais
        for (int i = 0; i < numVariables; i++) {
            tableau[numConstraints][i] = c[i];
        }

        // Usando um grande M para penalizar variáveis artificiais
        double M = 10000;
        for (int i = 0; i < numConstraints; i++) {
            tableau[numConstraints][numVariables + i] = -M; // Coeficientes para variáveis artificiais
        }

        // Exibe a tabela inicial
        System.out.println("Tabela inicial:");
        printTableau(tableau);

        // Loop do Simplex
        while (true) {
            // Verifica se há coeficientes negativos na linha da função objetivo
            int pivotCol = findPivotColumn(tableau);
            if (pivotCol == -1) break; // Solução ótima encontrada

            // Verifica se existe uma solução viável
            int pivotRow = findPivotRow(tableau, pivotCol);
            if (pivotRow == -1) {
                System.out.println("O problema não tem solução viável.");
                return;
            }

            // Faz o pivô
            pivot(tableau, pivotRow, pivotCol);

            // Exibe a tabela após a iteração
            System.out.println("Tabela após iteração:");
            printTableau(tableau);
        }

        // Exibindo a solução final
        printSolution(tableau);
    }

    // Encontrar a coluna pivô (com o coeficiente mais negativo na função objetivo)
    public static int findPivotColumn(double[][] tableau) {
        int pivotCol = -1;
        double minVal = 0;
        for (int i = 0; i < tableau[0].length - 1; i++) {
            if (tableau[tableau.length - 1][i] < minVal) {
                minVal = tableau[tableau.length - 1][i];
                pivotCol = i;
            }
        }
        return pivotCol;
    }

    // Encontrar a linha pivô usando o critério da razão mínima
    public static int findPivotRow(double[][] tableau, int pivotCol) {
        int pivotRow = -1;
        double minRatio = Double.MAX_VALUE;
        for (int i = 0; i < tableau.length - 1; i++) {
            if (tableau[i][pivotCol] > 0) {
                double ratio = tableau[i][tableau[0].length - 1] / tableau[i][pivotCol];
                if (ratio < minRatio) {
                    minRatio = ratio;
                    pivotRow = i;
                }
            }
        }
        return pivotRow;
    }

    // Realiza a operação de pivô (transformação da tabela)
    public static void pivot(double[][] tableau, int pivotRow, int pivotCol) {
        double pivotValue = tableau[pivotRow][pivotCol];

        // Normaliza a linha pivô
        for (int i = 0; i < tableau[0].length; i++) {
            tableau[pivotRow][i] /= pivotValue;
        }

        // Elimina os outros valores na coluna pivô
        for (int i = 0; i < tableau.length; i++) {
            if (i != pivotRow) {
                double factor = tableau[i][pivotCol];
                for (int j = 0; j < tableau[0].length; j++) {
                    tableau[i][j] -= factor * tableau[pivotRow][j];
                }
            }
        }
    }

    // Exibe a matriz tableau para visualização
    public static void printTableau(double[][] tableau) {
        for (int i = 0; i < tableau.length; i++) {
            for (int j = 0; j < tableau[i].length; j++) {
                System.out.print(String.format("%.2f", tableau[i][j]) + "\t");
            }
            System.out.println();
        }
    }

    // Exibe a solução final
    public static void printSolution(double[][] tableau) {
        System.out.println("Solução ótima encontrada:");

        // Imprimir as variáveis de decisão (sem contar as artificiais ou de folga)
        for (int i = 0; i < tableau[0].length - 1; i++) {
            if (i < tableau.length - 1) { // Não tentar acessar linhas fora do alcance
                System.out.print("x" + (i + 1) + " = " + tableau[i][tableau[0].length - 1] + " ");
            }
        }

        // Valor da função objetivo (última linha, última coluna)
        System.out.println("\nValor da função objetivo Z = " + tableau[tableau.length - 1][tableau[0].length - 1]);
    }

    public static void main(String[] args) {
        // Exemplo de dados para o problema
        double[][] A = {
                {2, 1},
                {1, 2}
        };

        double[] b = {6, 8};
        double[] c = {3, 5};

        // Chama o método Simplex com o método Big M
        simplexBigM(A, b, c);
    }
}
