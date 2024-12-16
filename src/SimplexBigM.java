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
        C = Arrays.asList(3f, 2f, 0f, 0f, 0f); // Exemplo: maximizar 3x1 + 2x2

        // Variáveis básicas iniciais
        B = Arrays.asList("x3", "x4", "x5"); // Variáveis de folga ou artificiais

        // Coeficientes de C_B (valores iniciais das variáveis básicas na função objetivo)
        C_B = Arrays.asList(0f, 0f, 0f);

        // Soluções básicas iniciais (lado direito das restrições)
        X_B = Arrays.asList(4f, 6f, 5f); // Valores correspondentes ao lado direito das restrições

        // Matriz de restrições
        A = Arrays.asList(
                Arrays.asList(2f, 1f, 1f, 0f, 0f),
                Arrays.asList(1f, 2f, 0f, 1f, 0f),
                Arrays.asList(1f, 1f, 0f, 0f, 1f)
        );

        // Variáveis artificiais (se existirem)
        variaveis_artificiais = new ArrayList<>();

        System.out.println("Dados inicializados com valores fixos.");
        System.out.println("Executando o método simplex...");

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

        for (int i = 0; i < B.size(); i++) {
            if (variaveis_artificiais.contains(B.get(i))) {
                System.out.println("*********** Não existe solução para este problema ***********");
                return;
            }
            resultado += C_B.get(i) * X_B.get(i);
        }

        if (!tipoProblema) { // Solução final para minimização
            resultado *= -1;
        }

        System.out.println("A solução final é: ");

        for (int i = 0; i < B.size(); i++) {
            System.out.println(B.get(i) + " = " + X_B.get(i));
        }

        System.out.println("\nA solução final é " + resultado + "\n");
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
