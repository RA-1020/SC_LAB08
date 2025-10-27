package com.example.recursion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class StringPermutations {

    /**
     * Public wrapper method to generate permutations[cite: 38].
     *
     * @param str   The input string.
     * @param unique If true, returns only unique permutations (using a Set).
     * If false, returns all permutations (using a List).
     * @return A List of all permutations.
     */
    public static List<String> generatePermutationsRecursive(String str, boolean unique) {
        // Error handling for null or empty input [cite: 41]
        if (str == null) {
            throw new IllegalArgumentException("Input string cannot be null.");
        }
        if (str.isEmpty()) {
            // Base case: permutation of an empty string is just an empty list
            return Collections.emptyList();
        }

        // Use a Set to handle duplicates if 'unique' is true 
        Collection<String> results = unique ? new HashSet<>() : new ArrayList<>();
        
        permuteRecursiveHelper(str.toCharArray(), 0, results);
        
        return new ArrayList<>(results);
    }

    /**
     * The core recursive helper function[cite: 39].
     * It uses the "fix a character and permute the rest" approach with backtracking.
     *
     * @param chars   The string as a character array.
     * @param index   The current index (character) being fixed.
     * @param results The collection to add permutations to.
     */
    private static void permuteRecursiveHelper(char[] chars, int index, Collection<String> results) {
        // Base Case: If we've fixed all characters, we have a complete permutation [cite: 13]
        if (index == chars.length - 1) {
            results.add(new String(chars));
            return;
        }

        // Recursive Step: [cite: 13]
        for (int i = index; i < chars.length; i++) {
            // 1. Swap the current index with the loop character
            swap(chars, index, i);
            
            // 2. Recurse for the rest of the string
            permuteRecursiveHelper(chars, index + 1, results);
            
            // 3. Backtrack: Swap back to restore the original state for the next loop iteration
            swap(chars, index, i);
        }
    }

    /**
     * Helper method to swap two characters in an array[cite: 14].
     */
    private static void swap(char[] chars, int i, int j) {
        char temp = chars[i];
        chars[i] = chars[j];
        chars[j] = temp;
    }

    // --- Mandatory Enhancement: Non-Recursive Solution --- 

    /**
     * Generates permutations using a non-recursive (iterative) algorithm.
     * This implementation uses Heap's Algorithm.
     *
     * @param str   The input string.
     * @param unique If true, returns only unique permutations.
     * @return A List of all permutations.
     */
    public static List<String> generatePermutationsIterative(String str, boolean unique) {
        // Error handling [cite: 41]
        if (str == null) {
            throw new IllegalArgumentException("Input string cannot be null.");
        }
        if (str.isEmpty()) {
            return Collections.emptyList();
        }

        Collection<String> results = unique ? new HashSet<>() : new ArrayList<>();
        char[] a = str.toCharArray();
        int n = a.length;
        int[] c = new int[n]; // State array for Heap's algorithm

        results.add(new String(a)); // Add the initial permutation

        int i = 0;
        while (i < n) {
            if (c[i] < i) {
                if (i % 2 == 0) {
                    // Even
                    swap(a, 0, i);
                } else {
                    // Odd
                    swap(a, c[i], i);
                }
                results.add(new String(a));
                c[i]++;
                i = 0;
            } else {
                c[i] = 0;
                i++;
            }
        }
        return new ArrayList<>(results);
    }

    /**
     * Main method for simple testing and performance comparison.
     */
    public static void main(String[] args) {
        String s = "AAB";
        System.out.println("--- Recursive Permutations of '" + s + "' ---");
        List<String> permsRecUnique = generatePermutationsRecursive(s, true);
        System.out.println("Unique: " + permsRecUnique.size() + " -> " + permsRecUnique);

        List<String> permsRecAll = generatePermutationsRecursive(s, false);
        System.out.println("All:    " + permsRecAll.size() + " -> " + permsRecAll);
        
        System.out.println("\n--- Iterative Permutations of '" + s + "' ---");
        List<String> permsIterUnique = generatePermutationsIterative(s, true);
        System.out.println("Unique: " + permsIterUnique.size() + " -> " + permsIterUnique);

        // Performance Comparison 
        System.out.println("\n--- Performance Comparison ---");
        String largeString = "ABCDEFGHIJ"; // n=10 (3,628,800 permutations)
        
        long startTime = System.nanoTime();
        generatePermutationsRecursive(largeString, true);
        long endTime = System.nanoTime();
        System.out.println("Recursive (n=10): " + (endTime - startTime) / 1_000_000 + " ms");

        startTime = System.nanoTime();
        generatePermutationsIterative(largeString, true);
        endTime = System.nanoTime();
        System.out.println("Iterative (n=10): " + (endTime - startTime) / 1_000_000 + " ms");
    }

    /*
     * == Time Complexity Analysis == 
     *
     * Let N be the length of the input string.
     *
     * 1. Recursive Solution:
     * - The number of permutations for a string of length N is N! (N-factorial).
     * - In our recursive algorithm, the `permuteRecursiveHelper` function is called at each step
     * of building the permutation. The total number of calls follows the permutation tree,
     * leading to O(N!) leaf nodes (actual permutations).
     * - At each leaf node (base case), we create a new String from the char array, which takes O(N) time.
     * - Therefore, the total time complexity is O(N * N!).
     *
     * 2. Iterative Solution (Heap's Algorithm):
     * - Heap's algorithm is designed to generate each permutation by performing a constant
     * number of swaps (O(1)) from the previous permutation.
     * - It generates all N! permutations.
     * - However, just like the recursive solution, we still need to create a new String from
     * the char array for *each* of the N! permutations to store it in our results list.
     * This string creation takes O(N) time.
     * - Thus, the time complexity is also O(N * N!).
     *
     * 3. Comparison:
     * - Asymptotically (in Big O notation), both solutions have the same time complexity of O(N * N!).
     * - In practice, the iterative solution (Heap's) is often faster. This is because it avoids
     * the overhead of recursive function calls (managing the call stack, passing parameters, etc.).
     * Our `main` method's performance test demonstrates this: the iterative version typically
     * runs significantly faster than the recursive one for large strings (e.g., N > 9).
     */
}