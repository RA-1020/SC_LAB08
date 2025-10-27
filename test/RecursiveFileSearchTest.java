package com.example.recursion;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit Test Case for RecursiveFileSearch (Task 3).
 * Tests cover base cases, recursive steps, and edge conditions.
 */
class RecursiveFileSearchTest {

    // @TempDir creates a temporary directory for this test class.
    // It's automatically created and deleted by JUnit.
    @TempDir
    static Path tempDir;

    static File rootDir;

    /**
     * Sets up a mock directory structure before any tests run.
     * Structure:
     * tempDir/
     * -> file1.txt
     * -> data.log
     * -> subdir1/
     * -> file1.txt
     * -> notes.txt
     * -> subdir2/
     * -> FILE1.TXT (for case-sensitivity test)
     * -> emptySubDir/
     */
    @BeforeAll
    static void setupFileSystem() throws IOException {
        rootDir = tempDir.toFile();

        // Create files in root
        Files.createFile(tempDir.resolve("file1.txt"));
        Files.createFile(tempDir.resolve("data.log"));

        // Create subdir1 and files
        Path subdir1 = Files.createDirectory(tempDir.resolve("subdir1"));
        Files.createFile(subdir1.resolve("file1.txt"));
        Files.createFile(subdir1.resolve("notes.txt"));

        // Create subdir2 and files
        Path subdir2 = Files.createDirectory(tempDir.resolve("subdir2"));
        Files.createFile(subdir2.resolve("FILE1.TXT"));

        // Create an empty directory 
        Files.createDirectory(tempDir.resolve("emptySubDir"));
    }

    @Test
    void testFileFound_CaseSensitive() {
        // Search for "file1.txt" case-sensitively
        List<String> filesToSearch = List.of("file1.txt");
        Map<String, List<String>> results = RecursiveFileSearch.searchFiles(rootDir, filesToSearch, true);
        
        List<String> paths = results.get("file1.txt");
        assertNotNull(paths);
        // Should find 2: one in root, one in subdir1
        assertEquals(2, paths.size());
        assertTrue(paths.contains(tempDir.resolve("file1.txt").toAbsolutePath().toString()));
        assertTrue(paths.contains(tempDir.resolve("subdir1/file1.txt").toAbsolutePath().toString()));
    }

    @Test
    void testFileFound_CaseInsensitive() {
        // Search for "file1.txt" case-insensitively
        List<String> filesToSearch = List.of("file1.txt");
        Map<String, List<String>> results = RecursiveFileSearch.searchFiles(rootDir, filesToSearch, false);
        
        List<String> paths = results.get("file1.txt");
        assertNotNull(paths);
        // Should find 3: file1.txt, file1.txt, and FILE1.TXT
        assertEquals(3, paths.size());
        assertTrue(paths.contains(tempDir.resolve("subdir2/FILE1.TXT").toAbsolutePath().toString()));
    }

    @Test
    void testFileNotFound() {
        // Base case: File does not exist
        List<String> filesToSearch = List.of("nonexistent.txt");
        Map<String, List<String>> results = RecursiveFileSearch.searchFiles(rootDir, filesToSearch, true);
        
        List<String> paths = results.get("nonexistent.txt");
        assertNotNull(paths);
        assertEquals(0, paths.size());
    }

    @Test
    void testSearchInEmptyDirectory() {
        // Edge case: Searching in an empty directory 
        File emptyDir = tempDir.resolve("emptySubDir").toFile();
        List<String> filesToSearch = List.of("file1.txt");
        Map<String, List<String>> results = RecursiveFileSearch.searchFiles(emptyDir, filesToSearch, true);
        
        List<String> paths = results.get("file1.txt");
        assertNotNull(paths);
        assertEquals(0, paths.size());
    }

    @Test
    void testMultipleFilesSearch() {
        // Enhancement test: Search for multiple files at once 
        List<String> filesToSearch = List.of("notes.txt", "data.log");
        Map<String, List<String>> results = RecursiveFileSearch.searchFiles(rootDir, filesToSearch, true);
        
        List<String> notesPaths = results.get("notes.txt");
        List<String> dataPaths = results.get("data.log");
        
        assertNotNull(notesPaths);
        assertNotNull(dataPaths);
        assertEquals(1, notesPaths.size());
        assertEquals(1, dataPaths.size());
        assertTrue(notesPaths.contains(tempDir.resolve("subdir1/notes.txt").toAbsolutePath().toString()));
        assertTrue(dataPaths.contains(tempDir.resolve("data.log").toAbsolutePath().toString()));
    }
}