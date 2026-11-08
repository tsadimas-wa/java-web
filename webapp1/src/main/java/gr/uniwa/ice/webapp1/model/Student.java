package gr.uniwa.ice.webapp1.model;

/**
 * Ένας φοιτητής. Είναι JavaBean: έχει κενό constructor και getters/setters,
 * ώστε οι JSP σελίδες να διαβάζουν τις ιδιότητες με EL, π.χ. ${student.firstName}.
 */
public class Student {

    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private int semester;
    private boolean active;

    public Student() {
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getSemester() { return semester; }
    public void setSemester(int semester) { this.semester = semester; }

    // Για boolean ιδιότητες ο getter λέγεται isXxx() αντί για getXxx()
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
