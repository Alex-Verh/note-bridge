package notebridge1.notebridge.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import notebridge1.notebridge.Security;
import notebridge1.notebridge.dao.TeacherDAO;
import notebridge1.notebridge.model.Teacher;
import notebridge1.notebridge.model.User;

import java.io.IOException;

/**
 * Servlet for handling the "Become Teacher" functionality.
 */
@WebServlet("/become-teacher")
public class BecomeTeacherServlet extends HttpServlet {

    /**
     * Handles the GET request for becoming a teacher.
     *
     * @param request  the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @throws ServletException if a servlet error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        if (!Security.isValidCsrfToken(request)) {
            System.out.println("Invalid csrf in become-teacher");
            return;
        }

        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return;
        }

        int id = user.getId();
        Teacher teacher = new Teacher(id);
        TeacherDAO.INSTANCE.insertTeacher(teacher);

        response.sendRedirect("/profile");
    }
}
