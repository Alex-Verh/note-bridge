package notebridge1.notebridge.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import notebridge1.notebridge.Security;
import notebridge1.notebridge.model.User;

import java.io.IOException;

/**
 * Servlet for handling user logout.
 */
@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    /**
     * Handles the GET request for user logout.
     *
     * @param request  the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @throws ServletException if a servlet error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        if (!Security.isValidCsrfToken(request)) {
            System.out.println("Invalid csrf in logout");
            return;
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("JSESSIONID")) {
                    System.out.println("JSESSIONID=" + cookie.getValue());
                }
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
        }

        HttpSession session = request.getSession(false);

        if (session != null) {
            System.out.println("Invalidating session...");

            User user = (User) session.getAttribute("user");
            if (user != null) {
                System.out.println("For user: " + user.getId());
            }
            session.invalidate();
        }

        response.sendRedirect("/main");
    }
}
