package com.example.hamminggolay.core;

import java.sql.SQLOutput;

public class HammingDecrypter {
    private static int n;
    private static int k;
    private static int [][] parityMatrix;

    //Default 7,4
    public HammingDecrypter(){
        this.n=7;
        this.k=4;
        this.parityMatrix= new int[][]{{1, 1, 0, 1, 0, 0, 0},
                                       {1, 0, 1, 1, 1, 0, 0},
                                       {1, 0, 0, 0, 1, 1, 0},
                                       {0, 1, 1, 1, 1, 1, 0}};
    }

    public HammingDecrypter(int n, int k){
        this.n=n;
        this.k=k;
        this.parityMatrix=  genParityMatrix(n,k);
    }

    public int getN(){
        return n;
    }

    public int getK(){
        return k;
    }

    public int[][] getH(){
        return parityMatrix;
    }

    public void setN(int n){
        this.n=n;
        parityMatrix=genParityMatrix(n,k);
        System.out.println("n is "+n);
    }

    public void setK(int k){
        this.k=k;
        parityMatrix=genParityMatrix(n,k);
        System.out.println("k is "+k);
    }

    public static int[][] genParityMatrix(int n, int k) {
        int p = 0;
        while (Math.pow(2, p) < n - k + p + 1) {
            p++;
        }
        int[][] H = new int[p + k][n - k];
        for (int i = 0; i < p; i++) {
            for (int j = 0; j < n - k; j++) {
                H[i][j] = (j == i) ? 1 : 0;
            }
        }
        for (int i = p; i < p + k; i++) {
            int pos = i - p;
            for (int j = 0; j < n - k; j++) {
                H[i][j] = (pos & (1 << (p - 1 - j))) != 0 ? 1 : 0;
            }
        }
        System.out.println("The new parity matrix is: "+H);
        return H;
    }

    // Algoritmo de corrección de errores para Hamming binario (n,k)
    private static int correct(int[] code) {
        int[] syndrome = new int[n-k];
        int errorIndex = 0;

        // Calcular el síndrome
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 7; j++) {
                syndrome[i] ^= code[j] * parityMatrix[j][i];
            }
        }

        // Determinar el índice del error
        errorIndex = syndrome[0] + syndrome[1]*2 + syndrome[2]*4;

        // Corregir el error
        if (errorIndex > 0) {
            code[errorIndex-1] ^= 1;
        }

        // Retornar el índice del error
        return errorIndex;
    }

    // Método para decodificar una palabra de código de Hamming binario (7,4)
    public static int[] decode(int[] code) {
        int errorIndex = correct(code);
        int[] data = new int[k];

        // Extraer los datos si no hay errores
        if (errorIndex == 0) {
            data[0] = code[2];
            data[1] = code[4];
            data[2] = code[5];
            data[3] = code[6];
        }

        // Retornar los datos
        System.out.println(data);
        return data;
    }
}
