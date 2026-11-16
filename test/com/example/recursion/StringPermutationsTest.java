package com.example.recursion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

/**
 * Task 3 - JUnit 5 tests for {@link StringPermutations}.
 */
class StringPermutationsTest {

    /** Checks that two lists hold the same strings with the same multiplicities. */
    private static void assertSameElements(List<String> expected, List<String> actual) {
        String[] e = expected.toArray(new String[0]);
        String[] a = actual.toArray(new String[0]);
        Arrays.sort(e);
        Arrays.sort(a);
        assertEquals(Arrays.asList(e), Arrays.asList(a));
    }

    private static String sortedChars(String s) {
        char[] c = s.toCharArray();
        Arrays.sort(c);
        return new String(c);
    }

    // --- Base case ----------------------------------------------------

    @Test
    void baseCase_singleCharacter() {
        assertEquals(List.of("A"), StringPermutations.generatePermutations("A"));
        assertEquals(List.of("A"), StringPermutations.generatePermutationsIterative("A", false));
    }

    // --- Recursive steps ----------------------------------------------

    @Test
    void recursiveStep_twoCharacters() {
        assertSameElements(List.of("AB", "BA"), StringPermutations.generatePermutations("AB"));
    }

    @Test
    void recursiveStep_threeCharacters() {
        assertSameElements(List.of("ABC", "ACB", "BAC", "BCA", "CAB", "CBA"),
                StringPermutations.generatePermutations("ABC"));
    }

    @Test
    void recursiveStep_countIsNFactorialAndAllDistinct() {
        String alphabet = "ABCDEFG";
        for (int n = 1; n <= alphabet.length(); n++) {
            List<String> perms = StringPermutations.generatePermutations(alphabet.substring(0, n));
            assertEquals(StringPermutations.factorial(n), perms.size(), "n = " + n);
            assertEquals(perms.size(), new HashSet<>(perms).size(), "duplicates for n = " + n);
        }
    }

    @Test
    void everyResultIsARearrangementOfTheInput() {
        String input = "LAB08";
        String signature = sortedChars(input);
        for (String p : StringPermutations.generatePermutations(input)) {
            assertEquals(signature, sortedChars(p));
        }
    }

    // --- Duplicates enhancement ---------------------------------------

    @Test
    void duplicates_includedByDefault() {
        List<String> perms = StringPermutations.generatePermutations("AAB");
        assertEquals(6, perms.size());
        assertEquals(2, perms.stream().filter("AAB"::equals).count());
    }

    @Test
    void duplicates_excludedOnRequest() {
        assertSameElements(List.of("AAB", "ABA", "BAA"), StringPermutations.generatePermutations("AAB", true));
        assertEquals(6, StringPermutations.generatePermutations("AABB", true).size());  // 4!/(2!2!)
        assertEquals(1, StringPermutations.generatePermutations("AAAA", true).size());
        assertEquals(60, StringPermutations.generatePermutations("BANANA", true).size()); // 6!/(3!2!)
    }

    // --- Iterative version agrees with recursive version --------------

    @Test
    void iterative_matchesRecursive() {
        for (String input : List.of("A", "AB", "ABC", "ABCD", "AAB", "MISSISS", "ABCDEFG")) {
            for (boolean unique : new boolean[] {false, true}) {
                assertSameElements(StringPermutations.generatePermutations(input, unique),
                        StringPermutations.generatePermutationsIterative(input, unique));
            }
        }
    }

    @Test
    void iterative_excludesDuplicates() {
        Set<String> expected = Set.of("AAB", "ABA", "BAA");
        List<String> actual = StringPermutations.generatePermutationsIterative("AAB", true);
        assertEquals(3, actual.size());
        assertEquals(expected, new HashSet<>(actual));
    }

    // --- Invalid input ------------------------------------------------

    @Test
    void invalid_nullThrows() {
        assertThrows(IllegalArgumentException.class, () -> StringPermutations.generatePermutations(null));
        assertThrows(IllegalArgumentException.class,
                () -> StringPermutations.generatePermutationsIterative(null, false));
    }

    @Test
    void invalid_emptyStringThrows() {
        assertThrows(IllegalArgumentException.class, () -> StringPermutations.generatePermutations(""));
        assertThrows(IllegalArgumentException.class,
                () -> StringPermutations.generatePermutationsIterative("", true));
    }

    @Test
    void invalid_tooLongThrows() {
        String tooLong = "ABCDEFGHIJK"; // 11 characters, 11! = 39,916,800 permutations
        assertThrows(IllegalArgumentException.class, () -> StringPermutations.generatePermutations(tooLong));
        assertThrows(IllegalArgumentException.class,
                () -> StringPermutations.generatePermutationsIterative(tooLong, false));
    }

    @Test
    void returnedListIsIndependentPerCall() {
        List<String> first = StringPermutations.generatePermutations("AB");
        first.clear();
        assertEquals(2, StringPermutations.generatePermutations("AB").size());
    }
}
