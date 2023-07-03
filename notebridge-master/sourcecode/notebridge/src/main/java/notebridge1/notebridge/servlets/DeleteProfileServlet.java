package notebridge1.notebridge.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import notebridge1.notebridge.Security;
import notebridge1.notebridge.dao.UserDAO;
import notebridge1.notebridge.model.User;

import java.io.IOException;

/**
 * Servlet for handling profile deletion.
 */
@WebServlet("/delete-profile")
public class DeleteProfileServlet extends HttpServlet {

    /**
     * Handles the GET request for deleting the user's profile.
     *
     * @param request  the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @throws ServletException if a servlet error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        System.out.println("DELETE PROFILE SERVLET");

        if (!Security.isValidCsrfToken(request)) {
            System.out.println("Invalid csrf in delete-profile");
            return;
        }

        HttpSession session = request.getSession(false);
        if (session != null) {
            User user = (User) session.getAttribute("user");
            if (user != null) {
                boolean res = UserDAO.INSTANCE.deleteUser(user.getId());
                System.out.println("ACCOUNT DELETION ID = " + user.getId() + " RESULT = " + res);
            }
        }
        response.sendRedirect("/logout");
    }
}
