package com.github.zoltanmeze.aoc.day04;

import com.github.zoltanmeze.aoc.utilities.ResourceUtils;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

@Slf4j
public class Day04 implements Runnable {

    public static void main(String[] args) {
        new Day04().run();
    }

    @Override
    public void run() {
        log.info("Part one: {}", partOne());
        log.info("Part two: {}", partTwo());
    }

    public Object partOne() {
        var input = parseInput();
        int result = 0;
        for (var pair : input) {
            if (pair.left.contains(pair.right) || pair.right.contains(pair.left)) {
                result++;
            }
        }
        return result;
    }

    public Object partTwo() {
        var input = parseInput();
        int result = 0;
        for (var pair : input) {
            if (pair.left.overlap(pair.right)) {
                result++;
            }
        }
        return result;
    }

    @Data
    @RequiredArgsConstructor(staticName = "of")
    static class Range {
        final int start;
        final int end;

        public boolean contains(Range other) {
            return other.start >= this.start && other.end <= this.end;
        }

        public boolean overlap(Range other) {
            return this.inRange(other.start)
                || this.inRange((other.end))
                || other.inRange(this.start)
                || other.inRange((this.end));
        }

        public boolean inRange(int x) {
            return this.start <= x && x <= this.end;
        }
    }

    @Data
    @RequiredArgsConstructor(staticName = "of")
    static class Pair<L, R> {
        final L left;
        final R right;
    }

    @SneakyThrows
    public List<Pair<Range, Range>> parseInput() {
        File file = ResourceUtils.getResourceFile("day04.txt");
        try (
            FileReader fileReader = new FileReader(file);
            Scanner scanner = new Scanner(fileReader)
        ) {
            List<Pair<Range, Range>> groups = new ArrayList<>();
            while (scanner.hasNext()) {
                int[] ranges = Arrays.stream(scanner.nextLine().split(",|-"))
                    .mapToInt(Integer::parseInt)
                    .toArray();
                groups.add(Pair.of(Range.of(ranges[0], ranges[1]), Range.of(ranges[2], ranges[3])));
            }
            return groups;
        }
    }
}
