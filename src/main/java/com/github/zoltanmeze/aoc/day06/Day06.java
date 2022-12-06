package com.github.zoltanmeze.aoc.day06;

import com.github.zoltanmeze.aoc.utilities.ResourceUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Scanner;

@Slf4j
public class Day06 implements Runnable {

    public static void main(String[] args) {
        new Day06().run();
    }

    @Override
    public void run() {
        log.info("Part one: {}", partOne());
        log.info("Part two: {}", partTwo());
    }

    public Object partOne() {
        var input = parseInput();
        return compute(input, 4);
    }

    public Object partTwo() {
        var input = parseInput();
        return compute(input, 14);
    }

    private int compute(char[] input, int targetSize) {
        int[] chars = new int[26];
        Queue<Character> queue = new ArrayDeque<>(targetSize);
        for (int i = 0; i < input.length; i++) {
            if (queue.size() == targetSize) {
                return i;
            }
            char next = input[i];
            queue.offer(next);
            if (++chars[next - 'a'] > 1) {
                // Duplicate found, just remove everything until first occurrence is found
                // This eliminates the need to check if all elements are unique in the queue
                char current;
                do {
                    current = queue.remove();
                    chars[current - 'a'] -= 1;
                } while (current != next);
            }
        }
        throw new IllegalStateException();
    }

    @SneakyThrows
    public char[] parseInput() {
        File file = ResourceUtils.getResourceFile("day06.txt");
        try (
            FileReader fileReader = new FileReader(file);
            Scanner scanner = new Scanner(fileReader)
        ) {
            return scanner.nextLine().toCharArray();
        }
    }
}
