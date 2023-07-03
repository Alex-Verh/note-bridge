package notebridge1.notebridge.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import notebridge1.notebridge.PasswordResetManager;
import notebridge1.notebridge.Security;
import notebridge1.notebridge.dao.UserDAO;
import notebridge1.notebridge.model.User;

import java.io.IOException;

/**
 * The ResetPasswordServlet class is a servlet that handles requests for resetting the user's password.
 * It provides functionality for displaying the password reset form, validating the password reset token,
 * and updating the user's password. The servlet expects to receive HTTP GET and POST requests.
 */
@WebServlet("/reset-password")
public class ResetPasswordServlet extends HttpServlet {
    private String token;

    /**
     * Processes HTTP GET requests for displaying the password reset form.
     * It checks the CSRF token, determines the appropriate target path for forwarding the request,
     * and redirects the user to the password reset form or the main page based on token validation.
     *
     * @param request  the HTTP request
     * @param response the HTTP response
     * @throws ServletException if there is an error in the servlet
     * @throws IOException      if there is an I/O error while processing the request
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session != null) {
            User user = (User) session.getAttribute("user");
            if (user != null) {
                request.getRequestDispatcher("/dist/reset-password.html").forward(request, response);
                return;
            }
        }

        token = request.getQueryString().split("=")[1];
        if(PasswordResetManager.INSTANCE.validatePasswordResetToken(token)) {
            request.getRequestDispatcher("/dist/reset-password.html").forward(request, response);
        } else {
            response.sendRedirect("/main");
        }
    }

    /**
     * Processes HTTP POST requests for updating the user's password.
     * It retrieves the email and new password from the request, updates the user's password,
     * and redirects the user to the appropriate page based on their login status.
     *
     * @param request  the HTTP request
     * @param response the HTTP response
     * @throws IOException if there is an I/O error while processing the request
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            User user = (User) session.getAttribute("user");
            if (user != null) {
                String email = user.getEmail();
                String password = request.getParameter("resetPassword");
                UserDAO.INSTANCE.updatePassword(email, password);
                response.sendRedirect("/profile/"+user.getId());
                return;
            }
        }

        String email = PasswordResetManager.INSTANCE.getEmailForToken(token);
        String password = request.getParameter("resetPassword");
        UserDAO.INSTANCE.updatePassword(email, password);
        PasswordResetManager.INSTANCE.removeTokenEmail(token);
        response.sendRedirect("/login");
    }
}
