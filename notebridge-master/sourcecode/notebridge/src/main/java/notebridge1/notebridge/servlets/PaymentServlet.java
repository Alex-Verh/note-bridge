package notebridge1.notebridge.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * The PaymentServlet class is a servlet that handles requests for the payment page.
 * It redirects the client to the payment page by forwarding the request to the target path "/dist/payment.html".
 * The servlet expects to receive HTTP GET requests.
 */
@WebServlet("/payment")
public class PaymentServlet extends HttpServlet {

    /**
     * Processes HTTP GET requests and redirects the client to the payment page.
     *
     * @param request  the HTTP request
     * @param response the HTTP response
     * @throws ServletException if there is an error in the servlet
     * @throws IOException      if there is an I/O error while processing the request
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String targetPath = "/dist/payment.html";
        request.getRequestDispatcher(targetPath).forward(request, response);
    }
}
