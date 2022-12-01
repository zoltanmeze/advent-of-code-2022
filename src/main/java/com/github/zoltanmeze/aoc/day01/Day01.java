package com.github.zoltanmeze.aoc.day01;

import com.github.zoltanmeze.aoc.utilities.ResourceUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

@Slf4j
public class Day01 implements Runnable {

    public static void main(String[] args) {
        new Day01().run();
    }

    @Override
    public void run() {
        log.info("Part one: {}", partOne());
        log.info("Part two: {}", partTwo());
    }

    public Object partOne() {
        List<List<Integer>> input = parseInput();
        return input.stream()
            .map(x -> x.stream()
                .mapToInt(Integer::intValue)
                .sum())
            .max(Comparator.comparingInt(x -> x))
            .get();
    }


    public Object partTwo() {
        List<List<Integer>> input = parseInput();
        return input.stream()
            .map(x -> x.stream()
                .mapToInt(Integer::intValue)
                .sum())
            .sorted(Comparator.comparingInt(x -> (int) x).reversed())
            .mapToInt(Integer::intValue)
            .limit(3)
            .sum();
    }

    @SneakyThrows
    public List<List<Integer>> parseInput() {
        File file = ResourceUtils.getResourceFile("day01.txt");
        try (
            FileReader fileReader = new FileReader(file);
            Scanner scanner = new Scanner(fileReader)
        ) {
            List<List<Integer>> groups = new ArrayList<>();
            List<Integer> group = new ArrayList<>();
            while (scanner.hasNext()) {
                String next = scanner.nextLine();
                if (next.isBlank()) {
                    groups.add(group);
                    group = new ArrayList<>();
                    continue;
                }
                group.add(Integer.valueOf(next));
            }
            if (!group.isEmpty()) {
                groups.add(group);
            }
            return groups;
        }
    }
}
