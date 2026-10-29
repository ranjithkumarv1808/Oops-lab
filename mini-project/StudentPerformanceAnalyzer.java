import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
 * Mini Project 87 - Student Performance Analyzer
 * Concepts: Class & Object, Collections (ArrayList), Methods
 */

// ---------- Main class ----------
public class StudentPerformanceAnalyzer {

    // When input is piped from a file, echo it so the saved output reads like a real session
    static void echo(String text) {
        if (System.console() == null) {
            System.out.println(text);
        }
    }

    // Reads an integer within [min, max]; keeps asking until valid (exception handling)
    static int readInt(Scanner sc, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = sc.nextInt();
                if (value < min || value > max) {
                    System.out.println("  Invalid! Enter a value between " + min + " and " + max + ".");
                    continue;
                }
                echo(String.valueOf(value));
                return value;
            } catch (InputMismatchException e) {
                System.out.println("  Invalid! Please enter a number.");
                echo(sc.next()); // discard bad token
            }
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        PerformanceAnalyzer analyzer = new PerformanceAnalyzer();

        System.out.println("====== STUDENT PERFORMANCE ANALYZER ======");

        int subjectCount = readInt(sc, "Enter number of subjects : ", 1, 10);
        for (int i = 1; i <= subjectCount; i++) {
            System.out.print("Enter name of subject " + i + "   : ");
            String subject = sc.next();
            echo(subject);
            analyzer.addSubject(subject);
        }

        int studentCount = readInt(sc, "Enter number of students : ", 1, 100);
        for (int i = 1; i <= studentCount; i++) {
            System.out.print("\nEnter name of student " + i + " : ");
            String name = sc.next();
            echo(name);
            Student s = new Student(name);
            for (int j = 0; j < analyzer.getSubjectCount(); j++) {
                int mark = readInt(sc, "  Marks in " + analyzer.getSubject(j) + " (0-100) : ", 0, 100);
                s.addMark(mark);
            }
            analyzer.addStudent(s);
        }

        analyzer.displayMarkTable();
        analyzer.displaySubjectAnalysis();
        analyzer.displayOverallAnalysis();

        sc.close();
    }
}

// ---------- Class representing one student ----------
class Student {
    private final String name;
    private final List<Integer> marks = new ArrayList<>();

    Student(String name) {
        this.name = name;
    }

    String getName() {
        return name;
    }

    void addMark(int mark) {
        marks.add(mark);
    }

    int getMark(int index) {
        return marks.get(index);
    }

    int getTotal() {
        int total = 0;
        for (int m : marks) {
            total += m;
        }
        return total;
    }

    double getAverage() {
        return marks.isEmpty() ? 0 : (double) getTotal() / marks.size();
    }

    // Performance category based on the student's average
    String getCategory() {
        double avg = getAverage();
        if (avg >= 85) return "Excellent";
        else if (avg >= 70) return "Good";
        else if (avg >= 50) return "Average";
        else return "Needs Improvement";
    }
}

// ---------- Class that holds all students and performs the analysis ----------
class PerformanceAnalyzer {
    private final List<String> subjects = new ArrayList<>();
    private final List<Student> students = new ArrayList<>();

    void addSubject(String subject) {
        subjects.add(subject);
    }

    void addStudent(Student s) {
        students.add(s);
    }

    int getSubjectCount() {
        return subjects.size();
    }

    String getSubject(int index) {
        return subjects.get(index);
    }

    // ----- Display the full mark table -----
    void displayMarkTable() {
        System.out.println("\n================ MARK TABLE ================");
        System.out.printf("%-14s", "Student");
        for (String sub : subjects) {
            System.out.printf("%-12s", sub);
        }
        System.out.printf("%-8s%-9s%-18s%n", "Total", "Average", "Category");
        System.out.println("-".repeat(14 + 12 * subjects.size() + 35));

        for (Student s : students) {
            System.out.printf("%-14s", s.getName());
            for (int i = 0; i < subjects.size(); i++) {
                System.out.printf("%-12d", s.getMark(i));
            }
            System.out.printf("%-8d%-9.2f%-18s%n", s.getTotal(), s.getAverage(), s.getCategory());
        }
    }

    // ----- Subject-wise analysis: highest, lowest, average -----
    void displaySubjectAnalysis() {
        System.out.println("\n============ SUBJECT-WISE ANALYSIS ============");
        for (int i = 0; i < subjects.size(); i++) {
            int highest = Integer.MIN_VALUE;
            int lowest = Integer.MAX_VALUE;
            int sum = 0;
            String highestName = "", lowestName = "";

            for (Student s : students) {
                int mark = s.getMark(i);
                sum += mark;
                if (mark > highest) {
                    highest = mark;
                    highestName = s.getName();
                }
                if (mark < lowest) {
                    lowest = mark;
                    lowestName = s.getName();
                }
            }
            double avg = (double) sum / students.size();

            System.out.println("Subject  : " + subjects.get(i));
            System.out.println("  Highest: " + highest + " (" + highestName + ")");
            System.out.println("  Lowest : " + lowest + " (" + lowestName + ")");
            System.out.printf("  Average: %.2f  [%s]%n", avg, categoryOf(avg));
            System.out.println();
        }
    }

    // ----- Overall class analysis -----
    void displayOverallAnalysis() {
        Student topper = students.get(0);
        Student last = students.get(0);
        double classSum = 0;

        for (Student s : students) {
            classSum += s.getAverage();
            if (s.getTotal() > topper.getTotal()) topper = s;
            if (s.getTotal() < last.getTotal()) last = s;
        }

        System.out.println("============== OVERALL ANALYSIS ==============");
        System.out.println("Number of students : " + students.size());
        System.out.println("Class topper       : " + topper.getName() + " (Total " + topper.getTotal() + ")");
        System.out.println("Lowest performer   : " + last.getName() + " (Total " + last.getTotal() + ")");
        System.out.printf("Class average      : %.2f%n", classSum / students.size());
        System.out.println("Class category     : " + categoryOf(classSum / students.size()));
    }

    // Same category rule applied to any average value
    private String categoryOf(double avg) {
        if (avg >= 85) return "Excellent";
        else if (avg >= 70) return "Good";
        else if (avg >= 50) return "Average";
        else return "Needs Improvement";
    }
}

