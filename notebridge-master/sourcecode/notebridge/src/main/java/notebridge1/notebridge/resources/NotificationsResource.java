package notebridge1.notebridge.resources;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import notebridge1.notebridge.Security;
import notebridge1.notebridge.dao.NotificationDAO;
import notebridge1.notebridge.model.Notification;
import org.json.JSONObject;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Path("/notification")
public class NotificationsResource {

    /**
     * Retrieves a notification by its ID.
     *
     * @param id the ID of the notification
     * @return a Response object containing the notification or an error code if the notification is not found
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNotificationById(@PathParam("id") int id) {
        Notification notification = NotificationDAO.INSTANCE.getNotificationById(id);
        return Response.ok().entity(notification).build();
    }

    /**
     * Retrieves notifications for a specific user.
     *
     * @param id the ID of the user
     * @return a Response object containing a list of notifications for the user
     */
    @GET
    @Path("/user/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNotificationsForUser(@PathParam("id") int id) {
        List<Notification> notifications = NotificationDAO.INSTANCE.getNotificationsForUser(id);
        return Response.ok().entity(notifications).build();
    }

    /**
     * Retrieves the count of notifications.
     *
     * @return a Response object containing the count of notifications
     */
    @GET
    @Path("/count")
    public Response getNotificationCount()  {
        int count = NotificationDAO.INSTANCE.getNotificationCount();
        return Response.ok().entity(count).build();
    }

    /**
     * Deletes a notification by its ID.
     *
     * @param id the ID of the notification
     * @return a Response object indicating the success of the deletion or an error code if the notification is not found
     */
    @DELETE
    @Path("/{id}")
    public Response deleteNotification(@PathParam("id") int id) {
        if (NotificationDAO.INSTANCE.deleteNotification(id)) {
            return Response.ok().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    /**
     * Creates a new notification.
     *
     * @param jsonPayload the JSON payload containing the notification information
     * @param request     the HttpServletRequest object
     * @return a Response object containing the ID of the created notification or an error code if the CSRF token is invalid or an exception occurs
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createNotification(String jsonPayload,
                                       @Context HttpServletRequest request) {
        System.out.println("CREATING NOTIFICATION FROM JSON: " + jsonPayload);

        if (!Security.isValidCsrfToken(request)) {
            System.out.println("Invalid csrf in createNotification");
            return Response.ok().build();
        }

        try {
            JSONObject json = new JSONObject(jsonPayload);

            int senderId = json.optInt("senderId");
            int receiverId = json.optInt("receiverId");
            String text = json.optString("text");
            boolean isConfirmed = json.optBoolean("isConfirmed");
            int bookingId = json.optInt("bookingId");

            LocalDate currentDate = LocalDate.now();
            Date sqlDate = Date.valueOf(currentDate);

            Notification notification = new Notification(receiverId, text, sqlDate,
                    isConfirmed, senderId, bookingId);

            int res = NotificationDAO.INSTANCE.addNotification(notification);
            return Response.ok(res).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.ok().build();
        }
    }

    /**
     * Confirms a notification.
     *
     * @param notificationId the ID of the notification to confirm
     * @param request        the HttpServletRequest object
     * @return a Response object indicating the success of the confirmation or an error code if the CSRF token is invalid
     */
    @GET
    @Path("/confirm/{notificationId}")
    public Response confirmNotification(@PathParam("notificationId") int notificationId,
                                        @Context HttpServletRequest request) {
        if (!Security.isValidCsrfToken(request)) {
            System.out.println("Invalid csrf in confirmNotification");
            return Response.ok().build();
        }

        boolean res = NotificationDAO.INSTANCE.confirmNotification(notificationId);
        return Response.ok(res).build();
    }
}
