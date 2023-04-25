package com.example.hamminggolay.core;

import java.sql.SQLOutput;

public class HammingDecrypter {
    int n;
    int k;
    int [][] parityMatrix;

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
}
