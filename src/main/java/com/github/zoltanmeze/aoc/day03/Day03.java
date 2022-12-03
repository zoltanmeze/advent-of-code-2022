package com.github.zoltanmeze.aoc.day03;

import com.github.zoltanmeze.aoc.utilities.ResourceUtils;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class Day03 implements Runnable {

    public static void main(String[] args) {
        new Day03().run();
    }

    @Override
    public void run() {
        log.info("Part one: {}", partOne());
        log.info("Part two: {}", partTwo());
    }

    public Object partOne() {
        var input = parseInput();
        int sum = 0;
        for (var rucksack : input) {
            Set<Character> commonItems = new HashSet<>(rucksack.getLeft());
            commonItems.retainAll(rucksack.getRight());
            sum += calculateSum(commonItems);
        }
        return sum;
    }

    public Object partTwo() {
        var input = parseInput();
        int sum = 0;
        Set<Character> commonItems = null;
        for (int i = 0, g = 0; i < input.size(); i++, g = i % 3) {
            var rucksack = input.get(i);
            if (g == 0) {
                commonItems = rucksack.getDistinctItems();
                continue;
            }
            commonItems.retainAll(rucksack.getDistinctItems());
            if (g == 2) {
                if (commonItems.size() > 1) {
                    throw new IllegalStateException(); // Shouldn't be a case
                }
                sum += calculateSum(commonItems);
            }
        }
        return sum;
    }

    private int calculateSum(Collection<Character> characters) {
        int sum = 0;
        for (char ch : characters) {
            if (ch >= 'a' && ch <= 'z') {
                sum += ch - 'a' + 1;
            } else {
                sum += ch - 'A' + 27;
            }
        }
        return sum;
    }

    @Data
    @RequiredArgsConstructor(staticName = "of")
    static class Rucksack {
        final List<Character> left;
        final List<Character> right;

        public Set<Character> getDistinctItems() {
            Set<Character> all = new HashSet<>(left);
            all.addAll(right);
            return all;
        }
    }

    @SneakyThrows
    public List<Rucksack> parseInput() {
        File file = ResourceUtils.getResourceFile("day03.txt");
        try (
            FileReader fileReader = new FileReader(file);
            Scanner scanner = new Scanner(fileReader)
        ) {
            List<Rucksack> groups = new ArrayList<>();
            while (scanner.hasNext()) {
                List<Character> next = scanner.nextLine().chars()
                    .mapToObj(x -> (char) x)
                    .collect(Collectors.toList());
                groups.add(Rucksack.of(
                    next.subList(0, next.size() / 2),
                    next.subList(next.size() / 2, next.size())
                ));
            }
            return groups;
        }
    }
}
