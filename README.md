# aoc-2019
Advent of Code 2019 puzzles

Disclaimer: all code written in this repository was done so with the intent of solving the puzzles at adventofcode.com and getting an answer as quickly as possible. Some of it has been cleaned up and made more efficient/readable. Some of it has not.

You will need [Apache Maven](https://maven.apache.org/) to compile and run this code.

To compile: run `mvn compile` from the root directory. You will need [ardnaxelarak-utils](https://github.com/ardnaxelarak/ardnaxelarak-utils) as a dependency.

To run on windows: `run.bat <javaClassName>`. For example, for puzzle 5b, you would run `run.bat Puzzle5b`.
To redirect the input file, append `< inputs\<whatever>.txt` to the command. So again for puzzle 5b,
the full command would be:
```
run.bat Puzzle5b < inputs\p5.txt
```

To run on things with bash: hopefully `run.sh` will work as well as `run.bat`, but I've been developing under Windows and not really paying attention to the bash version.
