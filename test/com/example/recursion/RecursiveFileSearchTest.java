package com.example.recursion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Task 3 - JUnit 5 tests for {@link RecursiveFileSearch}.
 *
 * <p>Each test gets a fresh temporary tree (deleted automatically by JUnit):</p>
 * <pre>
 * root/
 *   file1.txt
 *   data.log
 *   empty/
 *   sub1/
 *     file1.txt
 *     notes.txt
 *     deep/
 *       deeper/
 *         file1.txt
 *   sub2/
 *     FILE1.TXT
 * </pre>
 */
class RecursiveFileSearchTest {

    @TempDir
    Path tempDir;

    private File root;

    @BeforeEach
    void buildTree() throws IOException {
        root = tempDir.toFile();
        Files.createFile(tempDir.resolve("file1.txt"));
        Files.createFile(tempDir.resolve("data.log"));
        Files.createDirectory(tempDir.resolve("empty"));

        Path sub1 = Files.createDirectory(tempDir.resolve("sub1"));
        Files.createFile(sub1.resolve("file1.txt"));
        Files.createFile(sub1.resolve("notes.txt"));
        Path deeper = Files.createDirectories(sub1.resolve("deep").resolve("deeper"));
        Files.createFile(deeper.resolve("file1.txt"));

        Path sub2 = Files.createDirectory(tempDir.resolve("sub2"));
        Files.createFile(sub2.resolve("FILE1.TXT"));
    }

    private String abs(String relative) {
        return tempDir.resolve(relative).toAbsolutePath().toString();
    }

    private static List<String> sorted(String... paths) {
        List<String> list = new ArrayList<>(Arrays.asList(paths));
        list.sort(null);
        return list;
    }

    // --- Base cases ---------------------------------------------------

    @Test
    void baseCase_fileDirectlyInRootIsFound() {
        Map<String, List<String>> result = RecursiveFileSearch.searchFiles(root, List.of("data.log"), true);
        assertEquals(List.of(abs("data.log")), result.get("data.log"));
    }

    @Test
    void baseCase_emptyDirectoryFindsNothing() {
        File empty = tempDir.resolve("empty").toFile();
        Map<String, List<String>> result = RecursiveFileSearch.searchFiles(empty, List.of("file1.txt"), true);
        assertTrue(result.get("file1.txt").isEmpty());
    }

    @Test
    void baseCase_missingFileMapsToEmptyList() {
        Map<String, List<String>> result = RecursiveFileSearch.searchFiles(root, List.of("missing.txt"), true);
        assertTrue(result.containsKey("missing.txt"));
        assertTrue(result.get("missing.txt").isEmpty());
    }

    // --- Recursive steps ----------------------------------------------

    @Test
    void recursiveStep_fileOneLevelDownIsFound() {
        Map<String, List<String>> result = RecursiveFileSearch.searchFiles(root, List.of("notes.txt"), true);
        assertEquals(List.of(abs("sub1/notes.txt")), result.get("notes.txt"));
    }

    @Test
    void recursiveStep_findsEveryCopyAtEveryDepth() {
        Map<String, List<String>> result = RecursiveFileSearch.searchFiles(root, List.of("file1.txt"), true);
        assertEquals(sorted(abs("file1.txt"), abs("sub1/file1.txt"), abs("sub1/deep/deeper/file1.txt")),
                result.get("file1.txt"));
    }

    // --- Enhancements -------------------------------------------------

    @Test
    void enhancement_multipleFilesInOneRun() {
        Map<String, List<String>> result =
                RecursiveFileSearch.searchFiles(root, List.of("notes.txt", "data.log", "missing.txt"), true);
        assertEquals(List.of("notes.txt", "data.log", "missing.txt"), new ArrayList<>(result.keySet()));
        assertEquals(1, result.get("notes.txt").size());
        assertEquals(1, result.get("data.log").size());
        assertEquals(0, result.get("missing.txt").size());
    }

    @Test
    void enhancement_countOccurrences() {
        assertEquals(3, RecursiveFileSearch.countOccurrences(root, "file1.txt", true));
        assertEquals(1, RecursiveFileSearch.countOccurrences(root, "notes.txt", true));
        assertEquals(0, RecursiveFileSearch.countOccurrences(root, "missing.txt", true));
    }

    @Test
    void enhancement_caseSensitiveIgnoresDifferentCase() {
        assertEquals(0, RecursiveFileSearch.countOccurrences(root, "File1.Txt", true));
        assertEquals(1, RecursiveFileSearch.countOccurrences(root, "FILE1.TXT", true));
    }

    @Test
    void enhancement_caseInsensitiveMatchesAnyCase() {
        Map<String, List<String>> result = RecursiveFileSearch.searchFiles(root, List.of("File1.Txt"), false);
        assertEquals(4, result.get("File1.Txt").size());
        assertTrue(result.get("File1.Txt").contains(abs("sub2/FILE1.TXT")));
    }

    @Test
    void duplicateRequestedNamesAreCountedOnce() {
        Map<String, List<String>> result =
                RecursiveFileSearch.searchFiles(root, List.of("notes.txt", "notes.txt"), true);
        assertEquals(1, result.size());
        assertEquals(1, result.get("notes.txt").size());
    }

    @Test
    void symbolicLinkCycleDoesNotCauseInfiniteRecursion() throws IOException {
        try {
            Files.createSymbolicLink(tempDir.resolve("sub1/loop"), tempDir);
        } catch (UnsupportedOperationException | IOException e) {
            return; // file system does not allow symlinks (e.g. Windows without admin rights)
        }
        assertEquals(3, RecursiveFileSearch.countOccurrences(root, "file1.txt", true));
    }

    // --- Invalid input ------------------------------------------------

    @Test
    void invalid_nonExistentDirectoryThrows() {
        File missing = tempDir.resolve("does-not-exist").toFile();
        assertThrows(IllegalArgumentException.class,
                () -> RecursiveFileSearch.searchFiles(missing, List.of("file1.txt"), true));
    }

    @Test
    void invalid_rootIsAFileThrows() {
        File file = tempDir.resolve("data.log").toFile();
        assertThrows(IllegalArgumentException.class,
                () -> RecursiveFileSearch.searchFiles(file, List.of("file1.txt"), true));
    }

    @Test
    void invalid_nullDirectoryThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> RecursiveFileSearch.searchFiles(null, List.of("file1.txt"), true));
    }

    @Test
    void invalid_emptyOrBlankFileNamesThrow() {
        assertThrows(IllegalArgumentException.class,
                () -> RecursiveFileSearch.searchFiles(root, List.of(), true));
        assertThrows(IllegalArgumentException.class,
                () -> RecursiveFileSearch.searchFiles(root, List.of("  "), true));
        assertThrows(IllegalArgumentException.class,
                () -> RecursiveFileSearch.countOccurrences(root, null, true));
    }
}
