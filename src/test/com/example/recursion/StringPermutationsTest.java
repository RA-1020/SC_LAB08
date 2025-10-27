package com.example.recursion;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import static org.junit.jupiter.api.Assertions.*;

class StringPermutationsTest {

    // Helper to check if two lists are permutations of each other (contain same elements)
    private <T> void assertListsEqualIgnoringOrder(List<T> l1, List<T> l2) {
        assertNotNull(l1);
        assertNotNull(l2);
        assertEquals(l1.size(), l2.size());
        assertTrue(new HashSet<>(l1).equals(new HashSet<>(l2)));
    }

    // --- Recursive Solution Tests ---

    @Test
    void testRecursive_BaseCaseEmptyString() {
        // Edge case: Empty data
        List<String> perms = StringPermutations.generatePermutationsRecursive("", true);
        assertTrue(perms.isEmpty());
    }

    @Test
    void testRecursive_BaseCaseSingleChar() {
        // Base case: Single character
        List<String> expected = List.of("A");
        List<String> actual = StringPermutations.generatePermutationsRecursive("A", true);
        assertListsEqualIgnoringOrder(expected, actual);
    }

    @Test
    void testRecursive_SimpleString() {
        // Recursive step: "AB"
        List<String> expected = List.of("AB", "BA");
        List<String> actual = StringPermutations.generatePermutationsRecursive("AB", true);
        assertListsEqualIgnoringOrder(expected, actual);
    }

    @Test
    void testRecursive_ThreeCharString() {
        // Recursive step: "ABC" -> 3! = 6 permutations
        List<String> expected = List.of("ABC", "ACB", "BAC", "BCA", "CAB", "CBA");
        List<String> actual = StringPermutations.generatePermutationsRecursive("ABC", true);
        assertEquals(6, actual.size());
        assertListsEqualIgnoringOrder(expected, actual);
    }

    @Test
    void testRecursive_WithDuplicates_UniqueOnly() {
        // Enhancement test: Handle duplicates (unique)
        List<String> expected = List.of("AAB", "ABA", "BAA");
        List<String> actual = StringPermutations.generatePermutationsRecursive("AAB", true);
        assertEquals(3, actual.size());
        assertListsEqualIgnoringOrder(expected, actual);
    }

    @Test
    void testRecursive_WithDuplicates_AllowAll() {
        // Enhancement test: Handle duplicates (all)
        // "AAB" should produce 6 total permutations (3! = 6)
        List<String> actual = StringPermutations.generatePermutationsRecursive("AAB", false);
        assertEquals(6, actual.size());
        // Check counts
        assertEquals(2, actual.stream().filter(s -> s.equals("AAB")).count());
        assertEquals(2, actual.stream().filter(s -> s.equals("ABA")).count());
        assertEquals(2, actual.stream().filter(s -> s.equals("BAA")).count());
    }

    @Test
    void testRecursive_NullInput() {
        // Edge case: Invalid input
        assertThrows(IllegalArgumentException.class, () -> {
            StringPermutations.generatePermutationsRecursive(null, true);
        });
    }
    
    // --- Iterative Solution Tests ---

    @Test
    void testIterative_SimpleString() {
        List<String> expected = List.of("AB", "BA");
        List<String> actual = StringPermutations.generatePermutationsIterative("AB", true);
        assertListsEqualIgnoringOrder(expected, actual);
    }

    @Test
    void testIterative_WithDuplicates_UniqueOnly() {
        List<String> expected = List.of("AAB", "ABA", "BAA");
        List<String> actual = StringPermutations.generatePermutationsIterative("AAB", true);
        assertEquals(3, actual.size());
        assertListsEqualIgnoringOrder(expected, actual);
    }

    @Test
    void testIterative_NullInput() {
        // Edge case: Invalid input
        assertThrows(IllegalArgumentException.class, () -> {
            StringPermutations.generatePermutationsIterative(null, true);
        });
    }
}