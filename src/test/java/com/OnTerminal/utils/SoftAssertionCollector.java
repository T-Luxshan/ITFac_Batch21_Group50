package com.OnTerminal.utils;

import java.util.ArrayList;
import java.util.List;

public final class SoftAssertionCollector {

    private static final ThreadLocal<List<String>> FAILURES =
            ThreadLocal.withInitial(ArrayList::new);

    private SoftAssertionCollector() {
    }

    public static void checkTrue(String message, boolean condition) {
        if (!condition) {
            FAILURES.get().add(message);
        }
    }

    public static boolean hasFailures() {
        return !FAILURES.get().isEmpty();
    }

    public static String summary() {
        List<String> failures = FAILURES.get();
        StringBuilder sb = new StringBuilder("Soft assertion failures (" + failures.size() + "):");
        for (String failure : failures) {
            sb.append("\n - ").append(failure);
        }
        return sb.toString();
    }

    public static void clear() {
        FAILURES.get().clear();
    }
}
