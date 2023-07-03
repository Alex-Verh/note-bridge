package notebridge1.notebridge.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import notebridge1.notebridge.SMTP;
import notebridge1.notebridge.UnverifiedAccountsManager;
import notebridge1.notebridge.model.User;

import java.io.IOException;

/**
 * The RegisterServlet class is a servlet that handles requests for the user registration process.
 * It provides functionality for displaying the registration form, submitting the registration form, and sending email verification to the user.
 * The servlet expects to receive HTTP GET and POST requests.
 */
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    /**
     * Processes HTTP GET requests for displaying the registration form.
     * If the user is already logged in, they are redirected to the main page.
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
                String url = response.encodeRedirectURL("/main");
                response.sendRedirect(url);
                return;
            }
        }

        String targetPath = "/dist/register.html";
        request.getRequestDispatcher(targetPath).forward(request, response);
    }

    /**
     * Processes HTTP POST requests for submitting the registration form and performing user registration.
     *
     * @param req  the HTTP request
     * @param resp the HTTP response
     * @throws ServletException if there is an error in the servlet
     * @throws IOException      if there is an I/O error while processing the request
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            performRegister(req, resp);
        } catch (IOException e) {
            System.out.println("Register servlet error");
        }
    }

    /**
     * Performs the user registration process.
     * It retrieves the registration form data, creates a new user, sends email verification, and sets the response status to indicate success.
     *
     * @param request  the HTTP request
     * @param response the HTTP response
     * @throws IOException if there is an I/O error while processing the request
     */
    private void performRegister(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String email = request.getParameter("registerEmail");
        String password = request.getParameter("registerPassword");
        String fullName = request.getParameter("registerFullName");
        String isTeacher = request.getParameter("is_teacher");

        System.out.printf("Form POST request received: %s, %s, %s, isTeacher=(%s) %n", email, password, fullName, isTeacher);

        System.out.printf("Creating user: %s, %s, %s %n", email.toLowerCase(), password, fullName);
        User user = new User(email.toLowerCase(), password, fullName);

        if(isTeacher.equalsIgnoreCase("1")){
            UnverifiedAccountsManager.INSTANCE.addTeacher(user);
        }

        String baseURL = request.getRequestURL().toString();
        String baseURI = baseURL.substring(0, baseURL.length() - request.getRequestURI().length()) + request.getContextPath();

        String token = UnverifiedAccountsManager.INSTANCE.generateUserToken(user);
        String passwordResetLink = baseURI + "/login?token=" + token;
        String subject = "Verify Email Address";
        String body = "Click the link to verify your email address: " + passwordResetLink;

        SMTP.INSTANCE.sendEmail(subject, body, email.toLowerCase());
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
}
