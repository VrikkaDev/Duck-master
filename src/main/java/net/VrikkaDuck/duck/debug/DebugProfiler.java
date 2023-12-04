package net.VrikkaDuck.duck.debug;

import net.VrikkaDuck.duck.Variables;

import java.util.*;

public class DebugProfiler{
    public final int MAX_ENTRIES;
    private final ThreadLocal<Queue<ProfileEntry>> threadLocalQueue = ThreadLocal.withInitial(LinkedList::new);
    private final ArrayDeque<ProfileEntry> profilingResults = new ArrayDeque<>(1);

    public DebugProfiler(int maxEntries) {
        this.MAX_ENTRIES = maxEntries;
    }

    public void start(String methodName) {
        if (!Variables.DEBUG) {
            return;
        }

        Queue<ProfileEntry> queue = threadLocalQueue.get();
        queue.offer(new ProfileEntry(methodName, System.nanoTime(), 0));
        if (queue.size() > MAX_ENTRIES) {
            queue.poll(); // Remove the oldest entry to keep the size bounded
        }
    }

    public void stop(String methodName) {
        if (!Variables.DEBUG) {
            return;
        }

        Queue<ProfileEntry> queue = threadLocalQueue.get();

        ProfileEntry entry = queue.poll();
        if (entry != null && entry.methodName.equals(methodName)) {
            long endTime = System.nanoTime();
            entry.elapsedTime = endTime - entry.startTime;

            // Store the profiling result in the list
            storeProfilingResult(entry);
        } else {
            Variables.LOGGER.warn("Method " + methodName + " was not properly started or already stopped." + " Should be " + entry.methodName);
        }
    }

    private void storeProfilingResult(ProfileEntry entry) {
        profilingResults.push(entry);

        if (profilingResults.size() > MAX_ENTRIES) {
            profilingResults.removeLast();
        }
    }

    public List<ProfileEntry> getProfilingResults() {
        return new ArrayList<>(profilingResults);
    }

    public List<ProfileEntry> getProfilingResultAverage() {
        Map<String, ProfileEntry> resultMap = new HashMap<>();

        for (ProfileEntry pe : getProfilingResults()) {
            if (pe.elapsedTime == 0) {
                continue;
            }

            resultMap.compute(pe.methodName, (key, existingEntry) -> {
                if (existingEntry == null) {
                    return new ProfileEntry(pe.methodName, pe.startTime, pe.elapsedTime);
                } else {
                    existingEntry.addAnother(pe);
                    return existingEntry;
                }
            });
        }

        return resultMap.values().stream().sorted().toList();
    }

    public static class ProfileEntry implements Comparable<ProfileEntry> {
        public String methodName;
        public long startTime;
        public long elapsedTime;
        public int entries;

        ProfileEntry(String methodName, long startTime, long elapsedTime) {
            this.methodName = methodName;
            this.startTime = startTime;
            this.elapsedTime = elapsedTime;
            entries = 1;
        }

        public long getAverageElapsed() {
            return (elapsedTime / entries);
        }

        public void addAnother(ProfileEntry other) {
            this.startTime = Math.min(this.startTime, other.startTime);
            this.elapsedTime += other.elapsedTime;
            this.entries += other.entries;
        }

        @Override
        public int compareTo(ProfileEntry other) {
            return (int) (other.elapsedTime - this.elapsedTime);
        }
    }

}
