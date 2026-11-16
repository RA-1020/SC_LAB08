# SE-314 Software Construction - Lab 08: Recursion

**Rohaan Ahmad** · CMS 429399 · NUST SEECS

Eclipse Java project (JUnit 5) for the three Lab 08 tasks.

| Task | Class | What it does |
|------|-------|--------------|
| 1 | `RecursiveFileSearch` | Recursively searches a directory tree for one or more file names, prints every full path and a per-name count, with an optional case-insensitive mode. |
| 2 | `StringPermutations` | Generates all permutations of a string recursively (`generatePermutations`, swap and backtrack) and iteratively (Heap's algorithm), with an option to exclude duplicates. |
| 2 | `PermutationBenchmark` | Times the recursive and iterative generators for n = 6..10 (median of 5 runs after warm-up). |
| 3 | `RecursiveFileSearchTest`, `StringPermutationsTest` | 28 JUnit 5 tests covering base cases, recursive steps, enhancements and invalid input. |

## Layout

```
src/com/example/recursion/    main code
test/com/example/recursion/   JUnit 5 tests
```

## Running in Eclipse

1. *File > Import > Existing Projects into Workspace* and select this folder.
2. Right-click the `test` folder and choose *Run As > JUnit Test*.
3. For the command-line programs, use *Run As > Run Configurations > Arguments*:

```
RecursiveFileSearch   <directory> <fileName> [fileName ...] [-i | --ignore-case]
StringPermutations    <string> [--unique]
PermutationBenchmark  [maxN]          (add -Xmx2g under VM arguments for n = 10)
```

Examples:

```
RecursiveFileSearch  C:\LabDemo report.txt todo.md -i
StringPermutations   AAB --unique
```
