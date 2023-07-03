package notebridge1.notebridge.servlets;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import notebridge1.notebridge.PasswordResetManager;
import notebridge1.notebridge.SMTP;
import notebridge1.notebridge.dao.UserDAO;
import notebridge1.notebridge.model.User;

import java.io.IOException;

/**
 * Servlet for handling password forgot requests.
 */
@WebServlet("/forgot-password")
public class ForgotPasswordServlet extends HttpServlet {

    /**
     * Handles the GET request for password reset.
     *
     * @param request  the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @throws ServletException if a servlet error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            User user = (User) session.getAttribute("user");
            if (user != null) {
                response.sendRedirect("/profile/"+user.getId());
                return;
            }
        }

        String targetPath = "/dist/forgot-password.html";
        request.getRequestDispatcher(targetPath).forward(request, response);
    }

    /**
     * Handles the POST request for password reset.
     *
     * @param req  the HttpServletRequest object
     * @param resp the HttpServletResponse object
     * @throws ServletException if a servlet error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        String email = req.getParameter("forgotEmail").toLowerCase();
        // Check if the email is in the DB
        if (!UserDAO.INSTANCE.checkUserExistsByEmail(email)) {
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);

        } else {
            String baseURL = req.getRequestURL().toString();
            String baseURI = baseURL.substring(0, baseURL.length() - req.getRequestURI().length()) + req.getContextPath();

            String token = PasswordResetManager.INSTANCE.generatePasswordResetToken(email);
            String passwordResetLink = baseURI + "/reset-password?token=" + token;
            String subject = "Reset Password";
            String body = "Click the link to reset your password: " + passwordResetLink;
            SMTP.INSTANCE.sendEmail(subject, body, email);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        }
    }
}
