package com.example.recursion;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Task 2 - Recursive String Permutations.
 *
 * <p>Generates every permutation of a string, recursively (swap and backtrack) and
 * iteratively (Heap's algorithm), with an option to exclude duplicate permutations
 * that arise when the input contains repeated characters.</p>
 *
 * <p><b>Complexity.</b> A string of length n has n! orderings and building each result
 * string costs O(n), so both versions run in O(n &middot; n!) time and need O(n &middot; n!)
 * space for the output list. The recursive version also uses O(n) call-stack frames;
 * the iterative version replaces them with an O(n) counter array.</p>
 */
public final class StringPermutations {

    /**
     * Longest input accepted. 10! = 3,628,800 strings already needs a few hundred MB;
     * 11! would be about 40 million and 13! no longer fits in a Java list.
     */
    public static final int MAX_LENGTH = 10;

    private StringPermutations() {
        // utility class, not meant to be instantiated
    }

    // ------------------------------------------------------------------
    // Recursive solution
    // ------------------------------------------------------------------

    /**
     * Returns all permutations of {@code input}, including repeated ones.
     *
     * <p><b>Precondition:</b> {@code input} is non-null, non-empty and at most
     * {@link #MAX_LENGTH} characters long.</p>
     * <p><b>Postcondition:</b> returns a list of exactly n! strings, each a rearrangement
     * of all the characters of {@code input}.</p>
     *
     * @param input the string to permute
     * @return every permutation of {@code input}
     * @throws IllegalArgumentException if the precondition is violated
     */
    public static List<String> generatePermutations(String input) {
        return generatePermutations(input, false);
    }

    /**
     * Returns the permutations of {@code input}, optionally without duplicates
     * (duplicates enhancement).
     *
     * <p><b>Precondition:</b> as for {@link #generatePermutations(String)}.</p>
     * <p><b>Postcondition:</b> if {@code excludeDuplicates} is false, returns all n!
     * permutations; if true, returns each distinct permutation exactly once, i.e.
     * n! / (k1! &middot; k2! &middot; ...) strings, where ki is the number of times
     * each distinct character occurs.</p>
     *
     * @param input             the string to permute
     * @param excludeDuplicates true to return each distinct permutation once
     * @return the permutations of {@code input}
     * @throws IllegalArgumentException if the precondition is violated
     */
    public static List<String> generatePermutations(String input, boolean excludeDuplicates) {
        validate(input);
        List<String> results = new ArrayList<>(factorial(input.length()));
        permute(input.toCharArray(), 0, excludeDuplicates, results);
        return results;
    }

    /**
     * Recursive helper: positions {@code 0 .. index-1} are already fixed; fill the rest.
     *
     * <ul>
     *   <li><b>Base case:</b> {@code index == chars.length - 1}. Only one character is
     *       left, so the array is a complete permutation and is recorded.</li>
     *   <li><b>Recursive step:</b> each remaining character is swapped into position
     *       {@code index}, the rest is permuted by a recursive call with
     *       {@code index + 1} (one fewer free position, so the recursion terminates),
     *       and the swap is undone (backtracking).</li>
     * </ul>
     *
     * <p>When duplicates are excluded, a character value that has already been placed at
     * this position is skipped, so repeated permutations are never generated at all
     * (pruning), rather than generated and filtered out afterwards.</p>
     */
    private static void permute(char[] chars, int index, boolean excludeDuplicates, List<String> results) {
        if (index == chars.length - 1) {
            results.add(new String(chars));
            return;
        }

        Set<Character> usedAtThisPosition = excludeDuplicates ? new HashSet<>() : null;
        for (int i = index; i < chars.length; i++) {
            if (excludeDuplicates && !usedAtThisPosition.add(chars[i])) {
                continue; // this character value was already tried here
            }
            swap(chars, index, i);
            permute(chars, index + 1, excludeDuplicates, results);
            swap(chars, index, i); // backtrack
        }
    }

    // ------------------------------------------------------------------
    // Non-recursive solution (enhancement)
    // ------------------------------------------------------------------

    /**
     * Iterative version using Heap's algorithm: each permutation is produced from the
     * previous one with a single swap, and an explicit counter array replaces the
     * call stack.
     *
     * <p><b>Precondition / postcondition:</b> identical to
     * {@link #generatePermutations(String, boolean)} (the order of the results differs).</p>
     *
     * @param input             the string to permute
     * @param excludeDuplicates true to return each distinct permutation once
     * @return the permutations of {@code input}
     * @throws IllegalArgumentException if the precondition is violated
     */
    public static List<String> generatePermutationsIterative(String input, boolean excludeDuplicates) {
        validate(input);
        char[] chars = input.toCharArray();
        int n = chars.length;

        List<String> all = new ArrayList<>(factorial(n));
        all.add(new String(chars));

        int[] counters = new int[n];
        int i = 1;
        while (i < n) {
            if (counters[i] < i) {
                int j = (i % 2 == 0) ? 0 : counters[i];
                swap(chars, j, i);
                all.add(new String(chars));
                counters[i]++;
                i = 1;
            } else {
                counters[i] = 0;
                i++;
            }
        }

        return excludeDuplicates ? new ArrayList<>(new LinkedHashSet<>(all)) : all;
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private static void validate(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Input string must not be null.");
        }
        if (input.isEmpty()) {
            throw new IllegalArgumentException("Input string must not be empty.");
        }
        if (input.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Input is too long (" + input.length()
                    + " characters); the maximum is " + MAX_LENGTH + ".");
        }
    }

    private static void swap(char[] chars, int i, int j) {
        char tmp = chars[i];
        chars[i] = chars[j];
        chars[j] = tmp;
    }

    /** n! for 0 <= n <= MAX_LENGTH, used to size result lists up front. */
    static int factorial(int n) {
        int result = 1;
        for (int k = 2; k <= n; k++) {
            result *= k;
        }
        return result;
    }

    /**
     * Small demo: prints the permutations of a string. Pass the string as the first
     * argument and optionally {@code --unique} to exclude duplicates.
     */
    public static void main(String[] args) {
        String input = args.length > 0 ? args[0] : "AAB";
        boolean unique = args.length > 1 && args[1].equalsIgnoreCase("--unique");
        try {
            List<String> recursive = generatePermutations(input, unique);
            List<String> iterative = generatePermutationsIterative(input, unique);
            System.out.println("Input        : \"" + input + "\"  (duplicates "
                    + (unique ? "excluded" : "included") + ")");
            System.out.println("Recursive    : " + recursive.size() + " -> " + recursive);
            System.out.println("Iterative    : " + iterative.size() + " -> " + iterative);
            System.out.println("Same results : " + new HashSet<>(recursive).equals(new HashSet<>(iterative)));
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
