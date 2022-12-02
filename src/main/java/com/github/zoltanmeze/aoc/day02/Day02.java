package com.github.zoltanmeze.aoc.day02;

import com.github.zoltanmeze.aoc.utilities.ResourceUtils;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Slf4j
public class Day02 implements Runnable {

    public static void main(String[] args) {
        new Day02().run();
    }

    @Override
    public void run() {
        log.info("Part one: {}", partOne());
        log.info("Part two: {}", partTwo());
    }

    public Object partOne() {
        var input = parseInput();
        int result = 0;
        for (var round : input) {
            RockPaperScissors pick = RockPaperScissors.values()[round.getRight() - 'X'];
            result += pick.getValue() + pick.outcome(round.getLeft());
        }
        return result;
    }

    public Object partTwo() {
        var input = parseInput();
        int result = 0;

        for (var round : input) {
            var pick = switch (round.getRight()) {
                case 'X' -> round.getLeft().loose();
                case 'Y' -> round.getLeft().draw();
                case 'Z' -> round.getLeft().win();
                default -> throw new RuntimeException();
            };
            result += pick.getValue() + pick.outcome(round.getLeft());
        }
        return result;
    }

    @RequiredArgsConstructor
    @Getter
    enum RockPaperScissors {
        ROCK(1),
        PAPER(2),
        SCISSORS(3);

        final int value;

        public RockPaperScissors draw() {
            return this;
        }

        public RockPaperScissors loose() {
            return RockPaperScissors.values()[(this.getValue() + 1) % 3];
        }

        public RockPaperScissors win() {
            return RockPaperScissors.values()[this.getValue() % 3];
        }

        public int outcome(RockPaperScissors other) {
            if (other == this) {
                return 3;
            } else if (this.getValue() - 1 == other.getValue() % 3) {
                return 6;
            }
            return 0;
        }
    }

    @Data
    @RequiredArgsConstructor(staticName = "of")
    static class Pair<L, R> {
        final L left;
        final R right;
    }

    @SneakyThrows
    public List<Pair<RockPaperScissors, Character>> parseInput() {
        File file = ResourceUtils.getResourceFile("day02.txt");
        try (
            FileReader fileReader = new FileReader(file);
            Scanner scanner = new Scanner(fileReader)
        ) {
            List<Pair<RockPaperScissors, Character>> groups = new ArrayList<>();
            while (scanner.hasNext()) {
                String next = scanner.nextLine();
                groups.add(Pair.of(RockPaperScissors.values()[next.charAt(0) - 'A'], next.charAt(2)));
            }
            return groups;
        }
    }
}
