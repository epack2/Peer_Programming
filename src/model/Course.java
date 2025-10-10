package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class Course {
    private final String code;
    private final String title;
    private final int capacity;
    private final List<String> roster = new ArrayList<>();   // enrolled student IDs (FIFO insertion)
    private final List<String> waitlist = new ArrayList<>(); // FIFO

    public Course(String code, String title, int capacity) {
        this.code = code;
        this.title = title;
        this.capacity = capacity;
    }

    public String getCode() { return code; }
    public String getTitle() { return title; }
    public int getCapacity() { return capacity; }

    public List<String> getRoster() { return Collections.unmodifiableList(roster); }
    public List<String> getWaitlist() { return Collections.unmodifiableList(waitlist); }

    // Made all these public
    public void addToRoster(String studentId) { roster.add(studentId); }
    public boolean removeFromRoster(String studentId) { return roster.remove(studentId); }
    public void addToWaitlist(String studentId) { waitlist.add(studentId); }
    public boolean removeFromWaitlist(String studentId) { return waitlist.remove(studentId); }
    public boolean rosterContains(String studentId) { return roster.contains(studentId); }
    public boolean waitlistContains(String studentId) { return waitlist.contains(studentId); }
    public int rosterSize() { return roster.size(); }
    public String pollWaitlist() { return waitlist.isEmpty() ? null : waitlist.remove(0); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Course)) return false;
        Course c = (Course) o;
        return Objects.equals(code, c.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public String toString() {
        return code + " " + title + " cap=" + capacity + " enrolled=" + roster.size() + " wait=" + waitlist.size();
    }
}
