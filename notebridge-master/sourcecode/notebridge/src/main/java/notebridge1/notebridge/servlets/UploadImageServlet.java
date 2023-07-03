package notebridge1.notebridge.servlets;

import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import notebridge1.notebridge.Security;
import notebridge1.notebridge.model.User;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * The UploadImageServlet class is a servlet that handles image upload requests.
 * It allows users to upload and store images on the server.
 * The servlet expects to receive HTTP PUT requests.
 */
@WebServlet("/upload/image")
@MultipartConfig(
        maxFileSize = 20971520, // Specify the maximum file size allowed
        maxRequestSize = 41943040, // Specify the maximum request size allowed
        fileSizeThreshold = 1048576 // Specify the size threshold for storing files on disk
)
public class UploadImageServlet extends HttpServlet {

    /**
     * Processes HTTP PUT requests for uploading and storing user images.
     * It validates the CSRF token, retrieves the user from the session,
     * and saves the uploaded image to the server.
     *
     * @param request  the HTTP request
     * @param response the HTTP response
     * @throws IOException if there is an I/O error while processing the request
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("Upload image");

        if (!Security.isValidCsrfToken(request)) {
            System.out.println("Invalid csrf in uploadImage");
            response.sendRedirect("/main");
            return;
        }

        try {
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
            int id = user.getId();
            Part filePart = request.getPart("editProfileImage");
            String contentType = filePart.getContentType();
            boolean isImage = contentType.startsWith("image/");

            if (!isImage) {
                System.out.println("Not image");
                return;
            }

            String media = System.getenv("MEDIA_UPLOAD_DIR");
            String filename = id + ".jpg";

            InputStream fileContent = filePart.getInputStream();
            File file = new File(media, filename);

            if (!file.exists()) {
                Files.createFile(file.toPath());
            }

            Files.copy(fileContent, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}