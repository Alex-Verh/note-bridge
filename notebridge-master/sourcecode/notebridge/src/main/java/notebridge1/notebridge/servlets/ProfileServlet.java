package notebridge1.notebridge.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import notebridge1.notebridge.dao.UserDAO;
import notebridge1.notebridge.model.User;

import java.io.IOException;

/**
 * The ProfileServlet class is a servlet that handles requests for the user profile page.
 * It retrieves information about the user and determines the appropriate target path for forwarding the request.
 * The servlet expects to receive HTTP GET requests.
 */
@WebServlet("/profile/*")
public class ProfileServlet extends HttpServlet {

    /**
     * Processes HTTP GET requests for the user profile page.
     * It retrieves the user's information and determines the target path for forwarding the request.
     *
     * @param request  the HTTP request
     * @param response the HTTP response
     * @throws ServletException if there is an error in the servlet
     * @throws IOException      if there is an I/O error while processing the request
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("Hello from profile servlet!");

        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.replace("/", "").equals("")) {
            HttpSession session = request.getSession(false);

            if (session == null) {
                response.sendRedirect("/main");
                return;
            }

            User user = (User) session.getAttribute("user");
            if (user == null) {
                response.sendRedirect("/main");
                return;
            }

            response.sendRedirect("/profile/" + user.getId());
            return;
        }

        String[] pathParts = pathInfo.split("/");
        if (pathParts.length > 1) {
            try {
                int userId = Integer.parseInt(pathParts[1]);
                System.out.println("userId: " + userId);

                if (UserDAO.INSTANCE.getUserById(userId) == null) {
                    response.sendRedirect("/main");
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("Specified id is not an integer.");
                response.sendRedirect("/main");
                return;
            }
        }

        String targetPath = "/dist/profile.html";
        request.getRequestDispatcher(targetPath).forward(request, response);
    }
}
