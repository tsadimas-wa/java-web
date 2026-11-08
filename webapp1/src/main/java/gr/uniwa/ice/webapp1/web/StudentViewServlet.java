package gr.uniwa.ice.webapp1.web;

import gr.uniwa.ice.webapp1.model.Student;
import gr.uniwa.ice.webapp1.repository.StudentRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

/** GET /students/view?id=3 : εμφανίζει έναν φοιτητή. */
@WebServlet("/students/view")
public class StudentViewServlet extends HttpServlet {

    private final StudentRepository repository = StudentRepository.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Optional<Student> student;
        try {
            long id = Long.parseLong(req.getParameter("id"));
            student = repository.findById(id);
        } catch (NumberFormatException e) {
            student = Optional.empty();
        }

        if (student.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Ο φοιτητής δεν βρέθηκε");
            return;
        }

        req.setAttribute("student", student.get());
        req.getRequestDispatcher("/WEB-INF/jsp/student-view.jsp").forward(req, resp);
    }
}
