package notebridge1.notebridge.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import notebridge1.notebridge.SMTP;

import java.io.IOException;

/**
 * Servlet for handling the contact form submission and displaying the contact page.
 */
@WebServlet("/contact")
public class ContactServlet extends HttpServlet {

    /**
     * Handles the GET request for displaying the contact page.
     *
     * @param request  the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @throws ServletException if a servlet error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String targetPath = "/dist/contact.html";
        request.getRequestDispatcher(targetPath).forward(request, response);
    }

    /**
     * Handles the POST request for submitting the contact form.
     *
     * @param req  the HttpServletRequest object
     * @param resp the HttpServletResponse object
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String contactTopic = req.getParameter("contactTopic");
        String contactEmail = req.getParameter("contactEmail");
        String contactMessage = req.getParameter("contactMessage");
        String contactFullName = req.getParameter("contactFullName");
        String contact_type;
        if (req.getParameter("contact_type").equals("1")) {
            contact_type = "Technical";
        } else {
            contact_type = "Feedback";
        }

        String subject = contact_type + ": " + contactTopic;
        String body = "Fullname: " + contactFullName
                +"\nEmail: " + contactEmail
                +"\nMessage: " + contactMessage;

        SMTP.INSTANCE.sendEmail(subject, body);
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
}
