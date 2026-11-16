package com.example.recursion;

import java.util.Arrays;
import java.util.List;

/**
 * Task 2 enhancement - performance comparison of the recursive and iterative
 * permutation generators.
 *
 * <p>Each measurement is the median of several timed runs taken after JIT warm-up
 * runs, so the first method measured is not penalised for compiling the code.
 * Inputs use distinct characters so both methods produce exactly n! strings.</p>
 *
 * <p>Run with a larger heap for n = 10, e.g. {@code -Xmx2g}.</p>
 */
public final class PermutationBenchmark {

    private static final int WARMUP_RUNS = 3;
    private static final int TIMED_RUNS = 5;
    private static final String ALPHABET = "ABCDEFGHIJ";

    private PermutationBenchmark() {
    }

    public static void main(String[] args) {
        int maxN = args.length > 0 ? Integer.parseInt(args[0]) : StringPermutations.MAX_LENGTH;

        System.out.printf("%-4s %-12s %-16s %-16s %s%n", "n", "n!", "Recursive (ms)", "Iterative (ms)", "Iter/Rec");
        System.out.println("-".repeat(62));
        for (int n = 6; n <= maxN; n++) {
            String input = ALPHABET.substring(0, n);
            double recursiveMs = medianMillis(() -> StringPermutations.generatePermutations(input, false));
            double iterativeMs = medianMillis(() -> StringPermutations.generatePermutationsIterative(input, false));
            System.out.printf("%-4d %-12d %-16.2f %-16.2f %.2f%n",
                    n, StringPermutations.factorial(n), recursiveMs, iterativeMs, iterativeMs / recursiveMs);
        }
    }

    private interface Generator {
        List<String> run();
    }

    private static double medianMillis(Generator generator) {
        for (int i = 0; i < WARMUP_RUNS; i++) {
            consume(generator.run());
        }
        double[] times = new double[TIMED_RUNS];
        for (int i = 0; i < TIMED_RUNS; i++) {
            System.gc();
            long start = System.nanoTime();
            List<String> result = generator.run();
            times[i] = (System.nanoTime() - start) / 1_000_000.0;
            consume(result);
        }
        Arrays.sort(times);
        return times[TIMED_RUNS / 2];
    }

    /** Uses the result so the JIT cannot optimise the generation away. */
    private static volatile int sink;

    private static void consume(List<String> result) {
        sink += result.size();
    }
}
