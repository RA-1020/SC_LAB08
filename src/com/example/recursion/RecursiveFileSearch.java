package com.example.recursion;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Task 1 - Recursive File Search.
 *
 * <p>Searches a directory tree recursively for one or more file names and reports
 * the full path of every match, together with how many times each name occurs.</p>
 *
 * <pre>
 * Usage:   java com.example.recursion.RecursiveFileSearch &lt;directory&gt; &lt;fileName&gt; [fileName ...] [-i]
 * Example: java com.example.recursion.RecursiveFileSearch C:\Projects report.txt notes.log -i
 * </pre>
 *
 * <p>The search is case-sensitive by default; pass {@code -i} or {@code --ignore-case}
 * (anywhere in the argument list) to make it case-insensitive.</p>
 */
public final class RecursiveFileSearch {

    /** Command-line flags that switch the search to case-insensitive mode. */
    private static final Set<String> IGNORE_CASE_FLAGS = Set.of("-i", "--ignore-case");

    private RecursiveFileSearch() {
        // utility class, not meant to be instantiated
    }

    // ------------------------------------------------------------------
    // Public API
    // ------------------------------------------------------------------

    /**
     * Recursively searches {@code root} and all of its subdirectories for files whose
     * names match any of {@code fileNames}.
     *
     * <p><b>Precondition:</b> {@code root} is non-null and denotes an existing directory;
     * {@code fileNames} is non-null, non-empty and contains no null or blank names.</p>
     *
     * <p><b>Postcondition:</b> returns a map with exactly one key per distinct requested
     * name, in the order the names were given. Each value is the sorted list of absolute
     * paths of the regular files whose name equals that key (ignoring case when
     * {@code caseSensitive} is false). A name that was not found maps to an empty list.
     * The size of each list is the number of occurrences of that name in the tree.
     * Symbolic links to directories are not followed, so the search always terminates.</p>
     *
     * @param root          directory to start searching from
     * @param fileNames     names of the files to look for (multiple-file enhancement)
     * @param caseSensitive true for exact matching, false to ignore case
     * @return map from each requested name to the paths where it was found
     * @throws IllegalArgumentException if any precondition is violated
     */
    public static Map<String, List<String>> searchFiles(File root, Collection<String> fileNames,
                                                        boolean caseSensitive) {
        validateDirectory(root);
        Set<String> targets = validateFileNames(fileNames);

        Map<String, List<String>> results = new LinkedHashMap<>();
        for (String name : targets) {
            results.put(name, new ArrayList<>());
        }

        searchDirectory(root, targets, caseSensitive, results);

        for (List<String> paths : results.values()) {
            Collections.sort(paths);
        }
        return results;
    }

    /**
     * Counts how many times a single file name appears in {@code root} and its subdirectories.
     *
     * <p><b>Precondition:</b> as for {@link #searchFiles}, with one non-blank name.</p>
     * <p><b>Postcondition:</b> returns the number of regular files in the tree whose name
     * matches {@code fileName}; 0 if there are none.</p>
     *
     * @param root          directory to start searching from
     * @param fileName      name of the file to count
     * @param caseSensitive true for exact matching, false to ignore case
     * @return number of occurrences (count enhancement)
     * @throws IllegalArgumentException if any precondition is violated
     */
    public static int countOccurrences(File root, String fileName, boolean caseSensitive) {
        if (fileName == null) {
            throw new IllegalArgumentException("File name must not be null.");
        }
        return searchFiles(root, List.of(fileName), caseSensitive).get(fileName).size();
    }

    // ------------------------------------------------------------------
    // Recursive helper
    // ------------------------------------------------------------------

    /**
     * Recursive helper that visits one directory.
     *
     * <ul>
     *   <li><b>Base case:</b> a regular file is compared against the targets and never
     *       recursed into; a directory that is empty or unreadable contributes nothing.</li>
     *   <li><b>Recursive step:</b> every (non-symlink) subdirectory is searched by a
     *       recursive call on that subdirectory, which is strictly deeper in the tree,
     *       so the recursion always moves toward the leaves and terminates.</li>
     * </ul>
     */
    private static void searchDirectory(File directory, Set<String> targets, boolean caseSensitive,
                                        Map<String, List<String>> results) {
        File[] entries = directory.listFiles();
        if (entries == null) {
            return; // base case: unreadable directory (e.g. permission denied)
        }

        for (File entry : entries) {
            if (entry.isDirectory()) {
                if (!Files.isSymbolicLink(entry.toPath())) {
                    searchDirectory(entry, targets, caseSensitive, results); // recursive step
                }
            } else if (entry.isFile()) {
                recordIfMatch(entry, targets, caseSensitive, results);   // base case
            }
        }
    }

    /** Adds the absolute path of {@code file} to every target name it matches. */
    private static void recordIfMatch(File file, Set<String> targets, boolean caseSensitive,
                                      Map<String, List<String>> results) {
        String actualName = file.getName();
        for (String target : targets) {
            if (namesMatch(actualName, target, caseSensitive)) {
                results.get(target).add(file.getAbsolutePath());
            }
        }
    }

    /** Case-sensitivity enhancement: compares two names using the selected mode. */
    static boolean namesMatch(String actual, String wanted, boolean caseSensitive) {
        return caseSensitive ? actual.equals(wanted) : actual.equalsIgnoreCase(wanted);
    }

    // ------------------------------------------------------------------
    // Input validation
    // ------------------------------------------------------------------

    private static void validateDirectory(File root) {
        if (root == null) {
            throw new IllegalArgumentException("Directory must not be null.");
        }
        if (!root.exists()) {
            throw new IllegalArgumentException("Directory does not exist: " + root.getPath());
        }
        if (!root.isDirectory()) {
            throw new IllegalArgumentException("Path is not a directory: " + root.getPath());
        }
    }

    /** Rejects bad name lists and removes duplicate names while keeping their order. */
    private static Set<String> validateFileNames(Collection<String> fileNames) {
        if (fileNames == null || fileNames.isEmpty()) {
            throw new IllegalArgumentException("At least one file name is required.");
        }
        Set<String> unique = new LinkedHashSet<>();
        for (String name : fileNames) {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("File names must not be null or blank.");
            }
            unique.add(name);
        }
        return unique;
    }

    // ------------------------------------------------------------------
    // Command-line entry point
    // ------------------------------------------------------------------

    /**
     * Parses the command line, runs the search and prints a readable report.
     *
     * @param args {@code <directory> <fileName> [fileName ...] [-i | --ignore-case]}
     */
    public static void main(String[] args) {
        boolean caseSensitive = true;
        List<String> positional = new ArrayList<>();
        for (String arg : args) {
            if (IGNORE_CASE_FLAGS.contains(arg.toLowerCase())) {
                caseSensitive = false;
            } else {
                positional.add(arg);
            }
        }

        if (positional.size() < 2) {
            System.out.println("Usage: java com.example.recursion.RecursiveFileSearch "
                    + "<directory> <fileName> [fileName ...] [-i | --ignore-case]");
            return;
        }

        File root = new File(positional.get(0));
        List<String> names = positional.subList(1, positional.size());

        try {
            Map<String, List<String>> results = searchFiles(root, names, caseSensitive);
            printReport(root, results, caseSensitive);
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
        } catch (SecurityException e) {
            System.err.println("Error: access to the directory was denied (" + e.getMessage() + ").");
        }
    }

    private static void printReport(File root, Map<String, List<String>> results, boolean caseSensitive) {
        System.out.println("Searching in    : " + root.getAbsolutePath());
        System.out.println("Case-sensitive  : " + (caseSensitive ? "yes" : "no"));
        System.out.println("Files requested : " + results.keySet());
        System.out.println("--------------------------------------------------");

        for (Map.Entry<String, List<String>> entry : results.entrySet()) {
            List<String> paths = entry.getValue();
            if (paths.isEmpty()) {
                System.out.println("'" + entry.getKey() + "' was not found.");
            } else {
                System.out.println("'" + entry.getKey() + "' found " + paths.size() + " time(s):");
                for (String path : paths) {
                    System.out.println("    -> " + path);
                }
            }
        }
    }
}
