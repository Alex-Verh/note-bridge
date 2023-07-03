package notebridge1.notebridge.filters;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter(urlPatterns = {"/api/disabled/*"})
public class GlobalFilter implements Filter {

    /**
     * Performs the filtering operation for requests matching the specified URL pattern.
     * Checks if the request requires validation and if the session exists.
     * If validation is required and the session is not present, access is denied.
     * Otherwise, the request is passed to the next filter in the chain.
     *
     * @param servletRequest  the ServletRequest object
     * @param servletResponse the ServletResponse object
     * @param filterChain     the FilterChain object
     * @throws IOException      if an I/O error occurs during the filtering operation
     * @throws ServletException if any servlet-specific error occurs during the filtering operation
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        HttpSession session = request.getSession(false);

        if (isValidationRequired(request) && session == null) {
            System.out.println("Access denied");
            return;
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Checks if request validation is required based on the HTTP method.
     * Validation is not required for GET requests.
     *
     * @param request the HttpServletRequest object
     * @return true if request validation is required, false otherwise
     */
    private boolean isValidationRequired(HttpServletRequest request) {
        String method = request.getMethod();
        return !method.equalsIgnoreCase("GET");
    }
}
