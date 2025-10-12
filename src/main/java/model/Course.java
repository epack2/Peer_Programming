package model;

import util.ValidationUtil;

import java.util.*;

/**
 * Domain model: Course (no UI / persistence here).
 */
public class Course {
    private final String code;
    private final String title;
    private final int capacity;
    private final List<String> roster = new ArrayList<>();   // student IDs
    private final List<String> waitlist = new ArrayList<>(); // student IDs FIFO

    public Course(String code, String title, int capacity) {
        ValidationUtil.requireNonEmpty(code, "Course code");
        ValidationUtil.requireNonEmpty(title, "Course title");
        ValidationUtil.requireRange(capacity, 1, 500, "Capacity");
        this.code = code;
        this.title = title;
        this.capacity = capacity;
    }

    public String getCode() { return code; }
    public String getTitle() { return title; }
    public int getCapacity() { return capacity; }

    public List<String> getRoster() { return Collections.unmodifiableList(roster); }
    public List<String> getWaitlist() { return Collections.unmodifiableList(waitlist); }

    // internal helpers used by service/repo implementations
    public boolean isFull() { return roster.size() >= capacity; }
    public boolean rosterContains(String sid) { return roster.contains(sid); }
    public boolean waitlistContains(String sid) { return waitlist.contains(sid); }

    public boolean addToRoster(String sid) { if (!roster.contains(sid)) return roster.add(sid); return false; }
    public boolean addToWaitlist(String sid) { if (!waitlist.contains(sid)) return waitlist.add(sid); return false; }
    public boolean removeFromRoster(String sid) { return roster.remove(sid); }
    public boolean removeFromWaitlist(String sid) { return waitlist.remove(sid); }

    /**
     * Promote the first waitlisted student to roster (FIFO).
     * @return promoted student id or null if none
     */
    public String promoteOneFromWaitlist() {
        if (waitlist.isEmpty()) return null;
        String promoted = waitlist.remove(0);
        roster.add(promoted);
        return promoted;
    }
}
