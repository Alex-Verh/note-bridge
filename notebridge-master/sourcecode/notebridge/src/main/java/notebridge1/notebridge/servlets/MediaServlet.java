package notebridge1.notebridge.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * The MediaServlet class is a servlet that handles requests for serving media files.
 * It retrieves the requested media file from the server's file system and streams it back to the client.
 * The servlet expects the environment variable "MEDIA_UPLOAD_DIR" to be set, specifying the directory where media files are stored.
 * If the requested file does not exist and has a ".jpg" extension, a default profile image is served instead.
 */
@WebServlet("/media/*")
public class MediaServlet extends HttpServlet {

    /**
     * Processes HTTP GET requests for serving media files.
     *
     * @param request  the HTTP request
     * @param response the HTTP response
     * @throws ServletException if there is an error in the servlet
     * @throws IOException      if there is an I/O error while processing the request
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String media = System.getenv("MEDIA_UPLOAD_DIR");

        String filename = URLDecoder.decode(request.getPathInfo().substring(1), StandardCharsets.UTF_8);
        File file = new File(media, filename);

        if (!file.exists() && filename.endsWith(".jpg")) {
            String webRootPath = getServletContext().getRealPath("/");
            file = new File(webRootPath + "static/images/default-profile-image.png");
        }

        response.setHeader("Content-Type", getServletContext().getMimeType(filename));
        response.setHeader("Content-Length", String.valueOf(file.length()));
        response.setHeader("Content-Disposition", "inline; filename=\"" + file.getName() + "\"");
        Files.copy(file.toPath(), response.getOutputStream());
    }
}
