package com.yurwar.solution;

public abstract class Solution {
    private final String taskName;

    public Solution(String taskName) {
        this.taskName = taskName;
    }

    public abstract void executeTask();

    protected void printSolution(String text) {
        System.out.printf("Solution for %s: %s%n", getTaskName(), text);
    }

    public String getTaskName() {
        return taskName;
    }
}
