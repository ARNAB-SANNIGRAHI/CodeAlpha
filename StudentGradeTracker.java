import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class StudentGradeTracker extends JFrame {
    private ArrayList<Student> students;
    private JTextField nameField, gradeField;
    private DefaultTableModel tableModel;
    private JLabel avgLabel, highLabel, lowLabel;

    public StudentGradeTracker() {
        students = new ArrayList<>();
        setTitle("Student Grade Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Top panel for input
        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Name:"));
        nameField = new JTextField(10);
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Grade:"));
        gradeField = new JTextField(5);
        inputPanel.add(gradeField);
        JButton addButton = new JButton("Add Student");
        inputPanel.add(addButton);
        add(inputPanel, BorderLayout.NORTH);

        // Table for students
        tableModel = new DefaultTableModel(new Object[]{"Name", "Grade"}, 0);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Bottom panel for summary
        JPanel summaryPanel = new JPanel();
        avgLabel = new JLabel("Average: N/A");
        highLabel = new JLabel("Highest: N/A");
        lowLabel = new JLabel("Lowest: N/A");
        summaryPanel.add(avgLabel);
        summaryPanel.add(highLabel);
        summaryPanel.add(lowLabel);
        add(summaryPanel, BorderLayout.SOUTH);

        // Add button action
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addStudent();
            }
        });

        // Enter key submits grade
        gradeField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addStudent();
            }
        });
    }

    private void addStudent() {
        String name = nameField.getText().trim();
        String gradeText = gradeField.getText().trim();
        if (name.isEmpty() || gradeText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both name and grade.");
            return;
        }
        double grade;
        try {
            grade = Double.parseDouble(gradeText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid grade. Please enter a number.");
            return;
        }
        students.add(new Student(name, grade));
        tableModel.addRow(new Object[]{name, grade});
        nameField.setText("");
        gradeField.setText("");
        updateSummary();
    }

    private void updateSummary() {
        if (students.isEmpty()) {
            avgLabel.setText("Average: N/A");
            highLabel.setText("Highest: N/A");
            lowLabel.setText("Lowest: N/A");
            return;
        }
        double sum = 0, max = Double.NEGATIVE_INFINITY, min = Double.POSITIVE_INFINITY;
        for (Student s : students) {
            double g = s.getGrade();
            sum += g;
            if (g > max) max = g;
            if (g < min) min = g;
        }
        double avg = sum / students.size();
        avgLabel.setText(String.format("Average: %.2f", avg));
        highLabel.setText(String.format("Highest: %.2f", max));
        lowLabel.setText(String.format("Lowest: %.2f", min));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new StudentGradeTracker().setVisible(true);
        });
    }
} 