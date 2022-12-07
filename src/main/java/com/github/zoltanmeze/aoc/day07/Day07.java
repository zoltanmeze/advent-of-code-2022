package com.github.zoltanmeze.aoc.day07;

import static com.github.zoltanmeze.aoc.day07.Day07.Command.CD;
import static com.github.zoltanmeze.aoc.day07.Day07.Command.LS;

import com.github.zoltanmeze.aoc.utilities.ResourceUtils;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;

@Slf4j
public class Day07 implements Runnable {

    public static void main(String[] args) {
        new Day07().run();
    }

    @Override
    public void run() {
        log.info("Part one: {}", partOne());
        log.info("Part two: {}", partTwo());
    }

    public Object partOne() {
        var input = parseInput();
        Directory root = buildFileStructure(input);
        return root.getUsageReport().values()
            .stream()
            .filter(size -> size <= 100_000)
            .mapToLong(Long::longValue)
            .sum();
    }

    public Object partTwo() {
        var input = parseInput();
        Directory root = buildFileStructure(input);

        Map<String, Long> sizes = root.getUsageReport();

        long target = 30_000_000 - 70_000_000 + sizes.get(root.getAbsolutePath());

        log.info("Target size: {}", target);

        return sizes.values()
            .stream()
            .filter(size -> size >= target)
            .mapToLong(Long::longValue)
            .min()
            .orElseThrow();
    }

    private Directory buildFileStructure(List<CommandExecution> input) {
        Directory root = new Directory(null, "");
        Directory current = root;
        for (CommandExecution execution : input) {
            switch (execution.getCommand()) {
                case CD -> {
                    switch (execution.getArguments()[0]) {
                        case ".." -> current = current.getParent();
                        case "/" -> current = root;
                        default -> current = current.addSubdirectory(execution.getArguments()[0]);
                    };
                }
                case LS -> {
                    for (String output : execution.getOutput()) {
                        String[] parts = output.split("\\s");
                        if ("dir".equals(parts[0])) {
                            current.addSubdirectory(parts[1]);
                        } else {
                            current.addFile(parts[1], Integer.parseInt(parts[0]));
                        }
                    }
                }
            }
        }
        return root;
    }

    @Data
    @EqualsAndHashCode(exclude = "parent")
    @ToString(exclude = "parent")
    static class Directory {

        final Directory parent;
        final String absolutePath;

        @Getter(AccessLevel.NONE)
        final Map<String, Directory> subdirectories = new HashMap<>();
        final Map<String, Long> files = new HashMap<>();

        Directory(Directory parent, String name) {
            this.parent = parent;
            this.absolutePath = parent != null ? String.join("/", parent.absolutePath, name) : "";
        }

        public Directory addSubdirectory(String name) {
            return subdirectories.computeIfAbsent(name, k -> new Directory(this, k));
        }

        public Collection<Directory> getSubdirectories() {
            return subdirectories.values();
        }

        public void addFile(String name, long size) {
            files.put(name, size);
        }

        public Map<String, Long> getUsageReport() {
            Stack<Directory> directories = new Stack<>();
            directories.add(this);

            Map<String, Long> sizes = new LinkedHashMap<>();
            while (!directories.isEmpty()) {
                Directory directory = directories.peek();
                if (!sizes.containsKey(directory.absolutePath) && !directory.subdirectories.isEmpty()) {
                    sizes.put(directory.absolutePath, 0L); // Placeholder
                    directories.addAll(directory.subdirectories.values());
                } else {
                    directories.pop();
                    long size = directory.files.values()
                        .stream()
                        .mapToLong(Long::longValue)
                        .sum();
                    long total = sizes.compute(directory.absolutePath, (k, v) -> (v != null) ? v + size : size);
                    if (directory.parent != null) {
                        sizes.computeIfPresent(directory.parent.absolutePath, (k, v) -> v + total); // Always present (placeholder)
                    }
                }
            }
            return sizes;
        }
    }

    enum Command {
        CD,
        LS
    }

    @Data
    @RequiredArgsConstructor(staticName = "of")
    static class CommandExecution {
        final Command command;
        final String[] arguments;
        final List<String> output = new ArrayList<>();
    }

    @SneakyThrows
    public List<CommandExecution> parseInput() {
        File file = ResourceUtils.getResourceFile("day07.txt");
        try (
            FileReader fileReader = new FileReader(file);
            Scanner scanner = new Scanner(fileReader)
        ) {
            List<CommandExecution> input = new ArrayList<>();
            CommandExecution lastExecution = null;
            while (scanner.hasNextLine()) {
                String next = scanner.nextLine();
                String[] parts = next.split("\\s");
                switch (parts[1]) {
                    case "cd" -> {
                        input.add(CommandExecution.of(CD, Arrays.copyOfRange(parts, 2, parts.length)));
                        lastExecution = null;
                    }
                    case "ls" -> {
                        lastExecution = CommandExecution.of(LS, null);
                        input.add(lastExecution);
                    }
                    default -> {
                        if (lastExecution == null) {
                            throw new IllegalStateException();
                        }
                        lastExecution.getOutput().add(next);
                    }
                }
            }
            return input;
        }
    }
}
