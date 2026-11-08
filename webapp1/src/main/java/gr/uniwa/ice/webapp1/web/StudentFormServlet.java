package gr.uniwa.ice.webapp1.web;

import gr.uniwa.ice.webapp1.model.Student;
import gr.uniwa.ice.webapp1.repository.StudentRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@WebServlet("/students/new")
public class StudentFormServlet extends HttpServlet {

    private static final String FORM_JSP = "/WEB-INF/jsp/student-form.jsp";

    private final StudentRepository repository = StudentRepository.getInstance();

    /** GET /students/new : εμφάνιση της (κενής) φόρμας. */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher(FORM_JSP).forward(req, resp);
    }

    /** POST /students/new : υποβολή της φόρμας. */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // 1. Διαβάζουμε τα πεδία της φόρμας. Το όνομα είναι το name="..." του <input>.
        String firstName = trim(req.getParameter("firstName"));
        String lastName  = trim(req.getParameter("lastName"));
        String email     = trim(req.getParameter("email"));
        String semester  = trim(req.getParameter("semester"));
        // Ένα checkbox στέλνεται ΜΟΝΟ όταν είναι τσεκαρισμένο. Αλλιώς getParameter() == null.
        boolean active   = req.getParameter("active") != null;

        // 2. Φτιάχνουμε το αντικείμενο
        Student student = new Student();
        student.setFirstName(firstName);
        student.setLastName(lastName);
        student.setEmail(email);
        student.setActive(active);

        // 3. Έλεγχος εγκυρότητας (validation) στον server
        Map<String, String> errors = new LinkedHashMap<>();
        if (firstName.isEmpty()) {
            errors.put("firstName", "Το όνομα είναι υποχρεωτικό.");
        }
        if (lastName.isEmpty()) {
            errors.put("lastName", "Το επώνυμο είναι υποχρεωτικό.");
        }
        if (!email.matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+")) {
            errors.put("email", "Δώστε ένα έγκυρο email.");
        }
        try {
            int s = Integer.parseInt(semester);
            if (s < 1 || s > 10) {
                errors.put("semester", "Το εξάμηνο πρέπει να είναι από 1 έως 10.");
            }
            student.setSemester(s);
        } catch (NumberFormatException e) {
            errors.put("semester", "Επιλέξτε εξάμηνο.");
        }

        // 4α. Υπάρχουν λάθη: ξαναδείχνουμε τη φόρμα με τα μηνύματα και ό,τι είχε γράψει ο χρήστης
        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            req.setAttribute("student", student);
            req.getRequestDispatcher(FORM_JSP).forward(req, resp);
            return;
        }

        // 4β. Όλα σωστά: αποθήκευση και redirect στη σελίδα του φοιτητή (Post/Redirect/Get)
        repository.save(student);
        resp.sendRedirect(req.getContextPath() + "/students/view?id=" + student.getId() + "&saved=1");
    }

    private static String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
