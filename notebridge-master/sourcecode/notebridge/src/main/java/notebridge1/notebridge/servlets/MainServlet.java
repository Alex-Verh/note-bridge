package notebridge1.notebridge.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Servlet for displaying the main page.
 */
@WebServlet("/main")
public class MainServlet extends HttpServlet {

    /**
     * Handles the GET request for user logout.
     *
     * @param request  the HttpServletRequest object representing the HTTP request
     * @param response the HttpServletResponse object representing the HTTP response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String targetPath = "/dist/main.html";
        request.getRequestDispatcher(targetPath).forward(request, response);
    }
}
