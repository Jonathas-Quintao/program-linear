import java.util.*;
import java.io.*;

public class SimplexBigM {
    static boolean tipoProblema; // true = maximização, false = minimização
    static List<Float> C = new ArrayList<>();
    static List<Float> diferenca = new ArrayList<>();
    static List<String> B = new ArrayList<>();
    static List<Float> C_B = new ArrayList<>();
    static List<Float> X_B = new ArrayList<>();
    static List<List<Float>> A = new ArrayList<>();
    static List<String> variaveis_artificiais = new ArrayList<>();

    public static void inicializar() {

        // Configurar os dados fixos

        tipoProblema = true; // true para maximização, false para minimização

        // Coeficientes da função objetivo
        C = Arrays.asList(22.5f, 35f, 26.4f, 35f, 43.5f, 50f, 46.3f, 27f, 29.5f, 31f, 22.5f, 84f, 53f, 29.6f, 26.7f, 42f, 48f, 44.9f, 52.5f, 44.8f, 39.7f, 62f, 23f, 28f, 26.50f, 28f, 52.9f, 40.1f, 199f, 47.8f); // Exemplo: maximizar 3x1 + 2x2

        // Variáveis básicas iniciais
        B = Arrays.asList("f1", "f2", "f3", "f4", "f5", "f6", "f7", "f8", "f9", "f10", "f11", "f12", "f13", "f14", "f15", "f16", "f17"); // Variáveis de folga ou artificiais

        // Coeficientes de C_B (valores iniciais das variáveis básicas na função objetivo)
        C_B = Arrays.asList(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f);

        // Soluções básicas iniciais (lado direito das restrições)
        X_B = Arrays.asList(500000f, 70000f, 10000f, 20000f, 4000f, 1700f, 2500f, 18000f, 2000f, 8000f, 13500f, 3000f, 800f, 380f, 1000f, 480f, 720f); // Valores correspondentes ao lado direito das restrições

        // Matriz de restrições
        A = Arrays.asList(
                Arrays.asList(9.1f, 5.3f, 9.2f, 0f, 0f, 0f, 0f, 0f, 8.6f, 9.1f, 9.2f, 7f, 9.2f, 9.2f, 10f, 9.9f, 8.9f, 10f, 10f, 10f, 9.9f, 10f, 10f, 10f, 10f, 10f, 10f, 10f, 11f, 10f, 12f),
                Arrays.asList(0f, 0f, 0f, 5f, 5.6f, 6.5f, 6.4f, 1.6f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f),
                Arrays.asList(0f, 0f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 1f, 0f, 1f, 1f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f),
                Arrays.asList(1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f),
                Arrays.asList(0f, 0f, 0f, 0f, 0f, 1f, 0f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f),
                Arrays.asList(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 1f),
                Arrays.asList(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 1f, 0f, 0f),
                Arrays.asList(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 1f, 1f, 0f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 0f, 1f, 0f),
                Arrays.asList(0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f),
                Arrays.asList(0f, 0f, 0f, 0f,0f, 0f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f ,0f),
                Arrays.asList(0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f ,0f),
                Arrays.asList(0f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f),
                Arrays.asList(0f, 0f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f),
                Arrays.asList(0f, 0f, 0f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f),
                Arrays.asList(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f),
                Arrays.asList(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f),
                Arrays.asList(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 1f)
        );

        // Variáveis artificiais (se existirem)
        variaveis_artificiais = Arrays.asList("a1", "a2", "a3", "a4", "a5");

        simplex();
    }

    public static void simplex() {
        // Cálculo inicial de Zj - Cj
        calcularDiferenca();

        while (existeMin()) {
            int indiceEntrada = indiceMin();
            int indiceSaida = indiceSaida(indiceEntrada);

            if (indiceSaida == -1) {
                System.out.println("*********** Problema sem solução viável ***********");
                return;
            }

            atualizarBase(indiceEntrada, indiceSaida);
            calcularDiferenca();
        }

        calcularResultado();
    }

    public static void calcularDiferenca() {
        diferenca.clear();
        for (int i = 0; i < C.size(); i++) {
            float soma = 0;
            for (int j = 0; j < B.size(); j++) {
                soma += C_B.get(j) * A.get(j).get(i);
            }
            diferenca.add(soma - C.get(i));
        }
    }

    public static int indiceSaida(int indiceEntrada) {
        float menorRazao = Float.MAX_VALUE;
        int indice = -1;

        for (int i = 0; i < X_B.size(); i++) {
            float elemento = A.get(i).get(indiceEntrada);

            if (elemento > 0) {
                float razao = X_B.get(i) / elemento;
                if (razao < menorRazao) {
                    menorRazao = razao;
                    indice = i;
                }
            }
        }
        return indice;
    }

    public static void atualizarBase(int indiceEntrada, int indiceSaida) {
        String variavelEntrada = "x" + (indiceEntrada + 1);
        String variavelSaida = B.get(indiceSaida);

        B.set(indiceSaida, variavelEntrada);
        C_B.set(indiceSaida, C.get(indiceEntrada));

        float elementoPivo = A.get(indiceSaida).get(indiceEntrada);

        for (int i = 0; i < A.get(indiceSaida).size(); i++) {
            A.get(indiceSaida).set(i, A.get(indiceSaida).get(i) / elementoPivo);
        }
        X_B.set(indiceSaida, X_B.get(indiceSaida) / elementoPivo);

        for (int i = 0; i < A.size(); i++) {
            if (i != indiceSaida) {
                float fator = A.get(i).get(indiceEntrada);
                for (int j = 0; j < A.get(i).size(); j++) {
                    A.get(i).set(j, A.get(i).get(j) - fator * A.get(indiceSaida).get(j));
                }
                X_B.set(i, X_B.get(i) - fator * X_B.get(indiceSaida));
            }
        }
    }

    public static void calcularResultado() {
        float resultado = 0;

        // Verifica se há variáveis artificiais na base (indicando ausência de solução viável)
        for (int i = 0; i < B.size(); i++) {
            if (variaveis_artificiais.contains(B.get(i))) {
                System.out.println("*********** Não existe solução viável para este problema ***********");
                return;
            }
            resultado += C_B.get(i) * X_B.get(i);
        }

        // Ajusta o resultado final para problemas de minimização
        if (!tipoProblema) {
            resultado *= -1;
        }

        // Separar variáveis principais (x) e auxiliares (f)
        List<String> variaveisPrincipais = new ArrayList<>();
        List<Float> valoresPrincipais = new ArrayList<>();
        List<String> variaveisAuxiliares = new ArrayList<>();
        List<Float> valoresAuxiliares = new ArrayList<>();

        for (int i = 0; i < B.size(); i++) {
            String variavel = B.get(i);
            if (variavel.startsWith("x")) {
                variaveisPrincipais.add(variavel);
                valoresPrincipais.add(X_B.get(i));
            } else if (variavel.startsWith("f")) {
                variaveisAuxiliares.add(variavel);
                valoresAuxiliares.add(X_B.get(i));
            }
        }

        // Ordenar as listas de variáveis
        List<Integer> indicesOrdenadosX = getIndicesOrdenados(variaveisPrincipais);
        List<Integer> indicesOrdenadosF = getIndicesOrdenados(variaveisAuxiliares);

        // Exibe o resultado de forma organizada
        System.out.println("=== Resultado da Otimização ===");
        System.out.println("Solução Final (valores das variáveis na base):");
        System.out.println("---------------------------------------------");
        System.out.printf("%-15s %-15s%n", "Variável", "Valor");

        // Imprimir variáveis principais (x)
        for (int idx : indicesOrdenadosX) {
            System.out.printf("%-15s %-15.2f%n", variaveisPrincipais.get(idx), valoresPrincipais.get(idx));
        }

        // Imprimir variáveis auxiliares (f)
        for (int idx : indicesOrdenadosF) {
            System.out.printf("%-15s %-15.2f%n", variaveisAuxiliares.get(idx), valoresAuxiliares.get(idx));
        }

        System.out.println("---------------------------------------------");
        System.out.printf("Valor da Função Objetiva: %.2f%n", resultado);
        System.out.println("=============================================");
    }

    // Método auxiliar para ordenar índices com base no número na variável
    private static List<Integer> getIndicesOrdenados(List<String> variaveis) {
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < variaveis.size(); i++) {
            indices.add(i);
        }

        indices.sort((i1, i2) -> {
            int num1 = Integer.parseInt(variaveis.get(i1).substring(1));
            int num2 = Integer.parseInt(variaveis.get(i2).substring(1));
            return Integer.compare(num1, num2);
        });

        return indices;
    }



    public static boolean existeMin() {
        for (float valor : diferenca) {
            if (valor < 0) return true;
        }
        return false;
    }

    public static int indiceMin() {
        int indice = 0;
        for (int i = 1; i < diferenca.size(); i++) {
            if (diferenca.get(i) < diferenca.get(indice)) {
                indice = i;
            }
        }
        return indice;
    }

    public static void main(String[] args) {
        inicializar();
    }
}