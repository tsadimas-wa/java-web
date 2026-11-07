package gr.uniwa.ice.webapp1;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/hello")   // το servlet απαντά στο URL  /webapp1/hello
public class HelloServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String name = req.getParameter("name");   // ?name=...
        if (name == null || name.isBlank()) {
            name = "κόσμε";
        }
        resp.setContentType("text/html; charset=UTF-8");
        try (PrintWriter out = resp.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html><body>");
            out.println("<h1>Γεια σου, " + escape(name) + "!</h1>");
            out.println("</body></html>");
        }
    }

    /** Απλό HTML escaping ώστε να μην μπορεί ο χρήστης να «περάσει» HTML/JavaScript (XSS). */
    static String escape(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
