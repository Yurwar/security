package com.yurwar;

import com.yurwar.solution.Solution;
import com.yurwar.solution.task1.CaesarCipherSolution;

/**
 * Hello world!
 */
public class App {

    public static void main(String[] args) {
        Solution caesarSolution = new CaesarCipherSolution("One byte encrypted Caesar cipher");
        caesarSolution.executeTask();
    }

}
