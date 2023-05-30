package com.example.hamminggolay.ui;

import java.util.Arrays;

public class GolayDecoder24 {

    //Matriz de paridad 4*24
    private static final int[][] H = {
            {1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0},
            {0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0},
            {0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0},
            {0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1}
    };

    private static final int[][] A = {
            {0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 0, 1, 1, 1, 0, 0, 0, 1, 0},
            {1, 1, 0, 1, 1, 1, 0, 0, 0, 1, 0, 1},
            {1, 0, 1, 1, 1, 0, 0, 0, 1, 0, 1, 1},
            {1, 1, 1, 1, 0, 0, 0, 1, 0, 1, 1, 0},
            {1, 1, 1, 0, 0, 0, 1, 0, 1, 1, 0, 1},
            {1, 1, 0, 0, 0, 1, 0, 1, 1, 0, 1, 1},
            {1, 0, 0, 0, 1, 0, 1, 1, 0, 1, 1, 1},
            {1, 0, 0, 1, 0, 1, 1, 0, 1, 1, 1, 0},
            {1, 0, 1, 0, 1, 1, 0, 1, 1, 1, 0, 0},
            {1, 1, 0, 1, 1, 0, 1, 1, 1, 0, 0, 0},
            {1, 0, 1, 1, 0, 1, 1, 1, 0, 0, 0, 1}
    };

    private static int[][] ATR= new int[12][12];

    private static final int[][] I = {
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}
    };


    private static int[][] G= new int[12][24];
    private static int[][] GTR= new int[24][12];



    public static int[] decode(int[] received) {
        int[] syndrome= multiplyMatrix(received);
        int[] secondSyndrome= new int[12];

        int[] decodedWord= new int[24];

        System.out.print("Syndrome: ");
        printArray(syndrome);

        int[] error = new int[24];

        //Segundo paso
        System.out.println("El peso del sindrome es "+weight(syndrome));

        if (weight(syndrome) <= 3) {
            for (int i = 0; i < 12; i++) {
                error[i] = syndrome[i];
            }
            for(int i=12;i<24;i++){
                error[i]=0;
                System.out.print("Vector Error: ");
                printArray(error);
            }

            System.out.print("Error con peso < 3: ");
            printArray(error);
        }else{
            //Tercer paso
            error= calculateError(syndrome, A);

            if(error.length==0){
                //NO hay vector error
                //Paso 4. segundo síndrome sA
                secondSyndrome= calculateSecondSyndrome(syndrome,A);
                System.out.print("Calculado segundo sindrome: ");
                printArray(secondSyndrome);

                //Quinto paso
                if (weight(syndrome) <= 3) {
                    for (int i = 0; i < 12; i++) {
                        error[i] = syndrome[i];
                    }
                    for(int i=12;i<24;i++){
                        error[i]=0;
                        System.out.print("Vector Error: ");
                        printArray(error);
                    }

                    System.out.print("Error con peso < 3: ");
                    printArray(error);
                }else{
                    //Sexto paso
                    error= calculateErrorSS(secondSyndrome, A);

                    if(error.length==0){
                        //Más de 3 errores
                        System.out.println("Más de 3 errores, retransmision obligada");
                    }else{
                        decodedWord=obtainDecodedWord(received, error);
                    }
                }
            }else{
                decodedWord=obtainDecodedWord(received, error);
            }
        }

        return decodedWord;
    }

    //Paso 1. multiplicamos r*Gtr
    public static int[] multiplyMatrix(int[] input) {
        int[] r = new int[12];
        int[] g = new int[12];
        int[] result = new int[12];

        System.out.println("Primer paso");

        for (int i = 0; i < 12; i++) {
            int p_i = i + 12;

            System.out.println(i + " " + p_i + " " + input[i] + " " + input[p_i]);
            r[i] = input[i];
            g[i] = input[p_i];

            System.out.println(r[i] + " " + input[p_i]);
            int sum = r[i] ^ g[i]; // Realizar suma binaria utilizando XOR
            result[i] = sum & 0xFFF; // Ajustar el resultado a 12 bits utilizando una máscara
        }

        printArray(r);
        printArray(g);
        printArray(result);

        return result;
    }

    public static int[] calculateSecondSyndrome(int[] word, int[][] parityMatrix) {
        int[] secondSyndrome = new int[parityMatrix.length];

        for (int i = 0; i < parityMatrix.length; i++) {
            int sum = 0;
            for (int j = 0; j < word.length; j++) {
                sum += word[j] * parityMatrix[i][j];
            }
            secondSyndrome[i] = sum % 2;
        }

        return secondSyndrome;
    }

    //Calcula peso
    private static int weight(int[] input) {
        int weight = 0;
        for (int i = 0; i < input.length; i++) {
            if (input[i] == 1) {
                weight++;
            }
        }
        return weight;
    }

    //Paso 3. Comprobamos filas
    public static int[] calculateError(int[] syndrome, int[][] A) {
        int[] error = new int[syndrome.length*2];
        int[] ui = new int[A[0].length];

        for (int[] ai : A) {
            int[] sum = new int[syndrome.length];

            // Calcula la suma del síndrome y la fila ai
            for (int i = 0; i < syndrome.length; i++) {
                sum[i] = (syndrome[i] + ai[i]) % 2;
            }

            // Calcula el peso de Hamming del vector sum
            int weight = weight(sum);

            System.out.print("Fila ");
            printArray(ai);
            System.out.print("con peso "+weight+" con s+ ai= ");
            printArray(sum);

            // Si el peso es menor o igual a 2, actualiza el vector error y rompe el bucle
            if (weight <= 2) {
                //Obtenemos la fila en la que salio
                int pos=0;
                for(int c=0;c<12;c++){
                    if(A[c].equals(ai)){
                        System.out.println("Fila encontrada: "+c+" en matriz real "+(c+1));
                        pos=c;
                    }
                }

                for (int i = 0; i < sum.length; i++) {
                    error[i] = sum[i];
                }

                ui=I[pos];
                System.out.print("ui: ");
                printArray(ui);

                for(int i=0;i<12;i++){
                    int p_i=i+12;
                    error[p_i]=ui[i];
                }

                System.out.print("Vector error actualizado: ");
                printArray(error);
                break;
            }
        }

        return error;
    }

    //Paso 6 sA, (ui,sA+ai)
    public static int[] calculateErrorSS(int[] syndrome, int[][] A) {
        int[] error = new int[syndrome.length*2];
        int[] ui = new int[A[0].length];

        for (int[] ai : A) {
            int[] sum = new int[syndrome.length];

            // Calcula la suma del síndrome y la fila ai
            for (int i = 0; i < syndrome.length; i++) {
                sum[i] = (syndrome[i] + ai[i]) % 2;
            }

            // Calcula el peso de Hamming del vector sum
            int weight = weight(sum);

            System.out.print("Fila ");
            printArray(ai);
            System.out.print("con peso "+weight+" con s+ ai= ");
            printArray(sum);

            // Si el peso es menor o igual a 2, actualiza el vector error y rompe el bucle
            if (weight <= 2) {
                //Obtenemos la fila en la que salio
                int pos=0;
                for(int c=0;c<12;c++){
                    if(A[c].equals(ai)){
                        System.out.println("Fila encontrada: "+c+" en matriz real "+(c+1));
                        pos=c;
                    }
                }

                for (int i = 0; i < sum.length; i++) {
                    error[i+12] = sum[i];
                }

                ui=I[pos];
                System.out.print("ui: ");
                printArray(ui);

                for(int i=0;i<12;i++){
                    error[i]=ui[i];
                }

                System.out.print("Vector error actualizado: ");
                printArray(error);
                break;
            }
        }

        return error;
    }

    //Calcula la posible palabra
    public static int[] obtainDecodedWord(int[] word, int[] error) {
        int[] result = new int[word.length];
        printArray(word);
        printArray(error);

        for (int i = 0; i < word.length; i++) {
            result[i] = (word[i] + error[i]) % 2;
        }
        return result;
    }

    //Genera la matriz generadora I12.A
    public static int[][] generate(int[][]A) {
        int[][] G = new int[12][24];
        for (int i = 0; i < 12; i++) {
            G[i][i] = 1;
        }

        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 12; j++) {
                int jp=j+12;
                G[i][jp] = A[i][j];
            }
        }
        return G;
    }

    //Hace la transpuesta
    public static int[][] transpose(int[][] matrix) {
        int n = matrix.length;
        int m = matrix[0].length;
        int[][] transposed = new int[m][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                transposed[j][i] = matrix[i][j];
            }
        }
        return transposed;
    }

    public static void printMatrix(int[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }

    public static String printArray(int[] array) {
        String toret= "";
        for (int i = 0; i < array.length; i++) {
            System.out.print(array[i]);
            toret+=array[i];
        }
        System.out.println();
        toret+="\n";
        return toret;
    }

    public static void main(String[] args) {
        int[] result= new int[12];
        int [] y=  {1,0,1,1,1,1,1,0,1,1,1,1,0,1,0,0,1,1,1,1,1,0,1,1};

        System.out.println("A");
        printMatrix(A);

        G= generate(A);
        System.out.println("G");
        printMatrix(G);

        GTR= transpose(G);
        System.out.println("GTR");
        printMatrix(GTR);

        ATR= transpose(A);
        System.out.println("ATR");
        printMatrix(ATR);

        int[] x = GolayDecoder24.decode(y);

        System.out.println("\nReceived codeword: " + Arrays.toString(y));
        System.out.println("Decoded full message: " + Arrays.toString(x));

        System.out.print("Decoded 12 bits message: ");

        for(int i=0;i<12;i++){
            result[i]=x[i];
        }

        printArray(result);
    }

    public String decodeWord(int[] word) {
        int[] result= new int[12];

        System.out.println("A");
        printMatrix(A);

        G= generate(A);
        System.out.println("G");
        printMatrix(G);

        GTR= transpose(G);
        System.out.println("GTR");
        printMatrix(GTR);

        ATR= transpose(A);
        System.out.println("ATR");
        printMatrix(ATR);

        int[] x = GolayDecoder24.decode(word);

        System.out.println("\nReceived codeword: " + Arrays.toString(word));
        System.out.println("Decoded full message: " + Arrays.toString(x));

        System.out.print("Decoded 12 bits message: ");

        for(int i=0;i<12;i++){
            result[i]=x[i];
        }

        printArray(result);
        return printArray(result);
    }
}

