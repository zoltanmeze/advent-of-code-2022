package com.github.zoltanmeze.aoc.day05;

import com.github.zoltanmeze.aoc.utilities.ResourceUtils;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class Day05 implements Runnable {

    public static void main(String[] args) {
        new Day05().run();
    }

    @Override
    public void run() {
        log.info("Part one: {}", partOne());
        log.info("Part two: {}", partTwo());
    }

    public Object partOne() {
        var input = parseInput();
        List<Stack<Character>> crateStacks = input.crateStacks;
        for (Operation operation : input.operations) {
            for (int i = 0; i < operation.quantity; i++) {
                crateStacks.get(operation.destinationStack - 1).push(crateStacks.get(operation.sourceStack - 1).pop());
            }
        }
        return compute(crateStacks);
    }

    public Object partTwo() {
        var input = parseInput();
        List<Stack<Character>> crateStacks = input.crateStacks;
        for (Operation operation : input.operations) {
            LinkedList<Character> move = new LinkedList<>();
            for (int i = 0; i < operation.quantity; i++) {
                move.addFirst(crateStacks.get(operation.sourceStack - 1).pop());
            }
            crateStacks.get(operation.destinationStack - 1).addAll(move);
        }
        return compute(crateStacks);
    }

    public String compute(List<Stack<Character>> crateStacks) {
        StringBuilder result = new StringBuilder();
        for (Stack<Character> crateStack : crateStacks) {
            if (!crateStack.empty()) {
                result.append(crateStack.peek());
            }
        }
        return result.toString();
    }

    @Data
    @RequiredArgsConstructor(staticName = "of")
    static class Operation {
        final int quantity;
        final int sourceStack;
        final int destinationStack;
    }

    @Data
    @RequiredArgsConstructor(staticName = "of")
    static class Input {
        final List<Stack<Character>> crateStacks;
        final List<Operation> operations;
    }

    @SneakyThrows
    public Input parseInput() {
        File file = ResourceUtils.getResourceFile("day05.txt");
        try (
            FileReader fileReader = new FileReader(file);
            Scanner scanner = new Scanner(fileReader)
        ) {
            scanner.useDelimiter("\\n{2}");

            Pattern cratePattern = Pattern.compile("(?<=\\[)[A-Z](?=]\\s?)|\\h{4}");
            Pattern operationPattern = Pattern.compile("^move (?<quantity>\\d+) from (?<source>\\d+) to (?<target>\\d+)$");

            Map<Integer, Stack<Character>> crates = new HashMap<>();

            String[] crateRows = scanner.next().split("\\n");
            for (int row = crateRows.length - 1; row >= 0; row--) {
                Matcher matcher = cratePattern.matcher(crateRows[row]);
                for (int column = 0; matcher.find(); column++) {
                    String crate = matcher.group();
                    if (!crate.isBlank()) {
                        crates.computeIfAbsent(column, k -> new Stack<>()).push(crate.charAt(0));
                    }
                }
            }
            List<Operation> operations = new ArrayList<>();
            for (String operation : scanner.next().split("\\n")) {
                Matcher matcher = operationPattern.matcher(operation);
                while (matcher.find()) {
                    operations.add(Operation.of(
                        Integer.parseInt(matcher.group("quantity")),
                        Integer.parseInt(matcher.group("source")),
                        Integer.parseInt(matcher.group("target"))
                    ));
                }
            }
            return Input.of(new ArrayList<>(crates.values()), operations);
        }
    }
}
