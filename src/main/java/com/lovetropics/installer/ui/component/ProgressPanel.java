package com.lovetropics.installer.ui.component;

import java.awt.GridLayout;
import java.util.ArrayDeque;
import java.util.Deque;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.lovetropics.installer.ProgressCallback;

public class ProgressPanel extends JPanel implements ProgressCallback {

    private class Step {

        private final String name;
        private final int maxProgress;
        private int progress;

        Step(String name, int maxProgress) {
            this.name = name;
            this.maxProgress = maxProgress;
        }
    }

    private final Deque<Step> stack = new ArrayDeque<>();

    public ProgressPanel() {
        setLayout(new GridLayout(0, 1, 0, 0));
        JLabel def = new JLabel("Ready!", SwingConstants.CENTER);
        def.setVerticalAlignment(SwingConstants.TOP);
        add(def);
    }

    private void updateText() {
        removeAll();
        for (Step step : (Iterable<Step>) stack::descendingIterator) {
            StringBuilder text = new StringBuilder().append(step.name);
            if (step.maxProgress > 0) {
                text.append(": ").append(step.progress);
                if (step.maxProgress == 100) {
                    text.append("%");
                } else {
                    text.append(" / ").append(step.maxProgress);
                }
            }
            System.out.println(text);
            JLabel lab = new JLabel(text.toString(), SwingConstants.CENTER);
            lab.setVerticalAlignment(SwingConstants.TOP);
            add(lab);
        }
        System.out.println();
        revalidate();
        repaint();
    }

    @Override
    public synchronized void push(String step, int maxProgress) {
        stack.push(new Step(step, maxProgress));
        updateText();
    }

    @Override
    public synchronized void pop() {
        stack.pop();
        updateText();
    }

    @Override
    public synchronized void addProgress(int amount) {
        stack.peek().progress += amount;
        updateText();
    }

    @Override
    public void setProgress(int amount) {
        stack.peek().progress = amount;
        updateText();
    }
}
