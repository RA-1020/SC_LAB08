package com.example.recursion;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RecursiveFileSearch {

    /**
     * Main method to run the file search from the command line.
     * * Usage: java com.example.recursion.RecursiveFileSearch <directoryPath> <caseSensitive:true|false> <fileName1> [fileName2] ...
     * Example: java com.example.recursion.RecursiveFileSearch "C:\MyFolder" false "report.txt" "notes.log"
     */
    public static void main(String[] args) {
        // 1. Check for minimum required arguments [cite: 21]
        if (args.length < 3) {
            System.out.println("Usage: java RecursiveFileSearch <directoryPath> <caseSensitive:true|false> <fileName1> [fileName2] ...");
            return;
        }

        String dirPath = args[0];
        boolean caseSensitive = Boolean.parseBoolean(args[1]);

        // Collect all file names to search for
        List<String> filesToSearch = new ArrayList<>();
        for (int i = 2; i < args.length; i++) {
            filesToSearch.add(args[i]);
        }

        // 2. Error Handling: Check if directory exists [cite: 25]
        File directory = new File(dirPath);
        if (!directory.exists() || !directory.isDirectory()) {
            System.err.println("Error: Directory does not exist or is not a directory: " + dirPath);
            return;
        }

        // 3. Perform the search
        System.out.println("Searching in: " + dirPath);
        System.out.println("Case Sensitive: " + caseSensitive);
        System.out.println("Files to find: " + filesToSearch);
        System.out.println("---------------------------------");

        try {
            Map<String, List<String>> results = searchFiles(directory, filesToSearch, caseSensitive);

            // 4. Display results [cite: 23]
            boolean filesFound = false;
            for (String fileName : filesToSearch) {
                List<String> foundPaths = results.getOrDefault(fileName, new ArrayList<>());
                
                if (!foundPaths.isEmpty()) {
                    filesFound = true;
                    // Enhancement: Count the number of times found 
                    System.out.println("\nFound '" + fileName + "' " + foundPaths.size() + " time(s):");
                    for (String path : foundPaths) {
                        System.out.println("  -> " + path);
                    }
                }
            }

            if (!filesFound) {
                System.out.println("\nNo matching files were found.");
            }

        } catch (Exception e) {
            // General exception handling [cite: 25]
            System.err.println("An error occurred during the search: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Public wrapper method that initializes the search.
     *
     * @param directory     The directory to start the search from.
     * @param fileNames     A list of file names to search for.
     * @param caseSensitive True for case-sensitive search, false otherwise.
     * @return A Map where keys are file names and values are lists of full paths where the file was found.
     */
    public static Map<String, List<String>> searchFiles(File directory, List<String> fileNames, boolean caseSensitive) {
        Map<String, List<String>> foundFilesMap = new HashMap<>();
        // Initialize map for all files we are searching for
        for (String name : fileNames) {
            foundFilesMap.put(name, new ArrayList<>());
        }
        
        recursiveSearch(directory, fileNames, caseSensitive, foundFilesMap);
        return foundFilesMap;
    }

    /**
     * @param directory     The current directory being searched.
     * @param fileNames     The list of file names to find.
     * @param caseSensitive True for case-sensitive matching.
     * @param foundFilesMap The map to populate with results.
     */
    private static void recursiveSearch(File directory, List<String> fileNames, boolean caseSensitive, Map<String, List<String>> foundFilesMap) {
        
        // List files in the current directory [cite: 26]
        File[] filesList = directory.listFiles();

        // Handle cases where directory is empty or not accessible [cite: 25]
        if (filesList == null) {
            return;
        }

        for (File file : filesList) {
            if (file.isDirectory()) {
                // Recursive Step: If it's a directory, search inside it [cite: 13]
                recursiveSearch(file, fileNames, caseSensitive, foundFilesMap);
            } else if (file.isFile()) {
                // Base Case (Implicit): If it's a file, check for a match [cite: 13]
                String currentFileName = file.getName();

                for (String fileNameToFind : fileNames) {
                    boolean match;
                    if (caseSensitive) {
                        match = currentFileName.equals(fileNameToFind);
                    } else {
                        // Enhancement: Case-insensitive search 
                        match = currentFileName.equalsIgnoreCase(fileNameToFind);
                    }

                    if (match) {
                        // File found, add its full path to the map [cite: 23]
                        foundFilesMap.get(fileNameToFind).add(file.getAbsolutePath());
                    }
                }
            }
        }
    }
}