package notebridge1.notebridge.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import notebridge1.notebridge.Database;
import notebridge1.notebridge.Security;
import notebridge1.notebridge.UnverifiedAccountsManager;
import notebridge1.notebridge.dao.TeacherDAO;
import notebridge1.notebridge.dao.UserDAO;
import notebridge1.notebridge.model.Teacher;
import notebridge1.notebridge.model.User;


import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Servlet for handling login requests.
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    /**
     * Handles the GET request for the login page.
     *
     * @param req  the HttpServletRequest object
     * @param resp the HttpServletResponse object
     * @throws ServletException if a servlet error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            User user = (User) session.getAttribute("user");
            if (user != null) {
                resp.sendRedirect("/profile/"+user.getId());
                return;
            }
        }
        if (req.getQueryString() != null) {
            String token = req.getQueryString().split("=")[1];
            if(UnverifiedAccountsManager.INSTANCE.validateUserToken(token)) {
                User user = UnverifiedAccountsManager.INSTANCE.getUserForToken(token);
                UnverifiedAccountsManager.INSTANCE.removeTokenUser(token);

                UserDAO.INSTANCE.insertUser(user);
                if (UnverifiedAccountsManager.INSTANCE.isTeacher(user)) {
                    int newId = UserDAO.INSTANCE.getIdByEmail(user.getEmail());
                    System.out.printf("Creating teacher with ID: %s %n", newId);

                    Teacher teacher = new Teacher(newId);
                    if (TeacherDAO.INSTANCE.insertTeacher(teacher) <= 0) {
                        System.out.println("Teacher was not created upon registration.");
                    } else {
                        System.out.println("Teacher successfully created upon registration.");
                    }
                    UnverifiedAccountsManager.INSTANCE.removeTeacher(user);
                }
            }
        }

        String targetPath = "/dist/login.html";
        req.getRequestDispatcher(targetPath).forward(req, resp);
    }

    /**
     * Handles the POST request for login authentication.
     *
     * @param req  the HttpServletRequest object
     * @param resp the HttpServletResponse object
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String loginEmail = req.getParameter("loginEmail").toLowerCase();
        String loginPassword = req.getParameter("loginPassword");

        System.out.printf("received data %s %s %n", loginEmail, loginPassword);

        boolean isValid = Security.validateEmail(loginEmail)
                && Security.validatePassword(loginPassword)
                && performAuthentication(loginEmail, loginPassword);

        System.out.println(Security.validateEmail(loginEmail));
        System.out.println(Security.validatePassword(loginPassword));
        System.out.println(performAuthentication(loginEmail, loginPassword));
        System.out.println("isValid = " + isValid);

        if (!isValid) {
            String errorMessage = "Invalid credentials. Please try again.";
            resp.sendRedirect("/login?error=" + errorMessage);
            return;
        }
        User user = UserDAO.INSTANCE.getUserByEmail(loginEmail);
        if (user == null) {
            String errorMessage = "Invalid user. Please try again.";
            resp.sendRedirect("/login?error=" + errorMessage);
            return;
        }

        generateSession(req, user);
        Security.storeCsrfToken(req, resp, Security.generateCsrfToken());
        resp.sendRedirect("/profile");
    }

    /**
     * Performs the authentication by verifying the email and password against the stored values.
     *
     * @param email    the login email
     * @param password the login password
     * @return true if the authentication is successful, false otherwise
     */
    private boolean performAuthentication(String email, String password) {
        String query = "SELECT password, password_salt FROM users WHERE lower(email) = lower(?)";
        Object[] args = { email };

        byte[] salt = new byte[16];
        byte[] originalHash = new byte[32];

        try {
            ResultSet res = Database.INSTANCE.getPreparedStatementQuery(query, args);
            if (res.next()) {
                salt = res.getBytes("password_salt");
                originalHash = res.getBytes("password");
            } else {
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e);
        }

        return UserDAO.INSTANCE.verifyPasswordArgon2(password, salt, originalHash);
    }

    private void generateSession(HttpServletRequest req, User user) {
        HttpSession session = req.getSession();
        user.setPassword("");

        session.setAttribute("user", user);
        session.setMaxInactiveInterval(30*60);
    }
}
