package gr.uniwa.ice.webapp1.web;

import gr.uniwa.ice.webapp1.repository.StudentRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/** GET /students : λίστα με όλους τους φοιτητές. */
@WebServlet("/students")
public class StudentListServlet extends HttpServlet {

    private final StudentRepository repository = StudentRepository.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("students", repository.findAll());
        req.getRequestDispatcher("/WEB-INF/jsp/student-list.jsp").forward(req, resp);
    }
}
