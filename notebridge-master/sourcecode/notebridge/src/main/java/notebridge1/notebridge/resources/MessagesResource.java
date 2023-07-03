package notebridge1.notebridge.resources;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import notebridge1.notebridge.Security;
import notebridge1.notebridge.dao.MessageDAO;
import notebridge1.notebridge.model.Message;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Path("/message")
public class MessagesResource {

    /**
     * Retrieves all messages with either the sending or receiving ID equal to the given ID.
     *
     * @param id the ID of the user
     * @return a Response object containing a list of messages or an error code if the database fails
     */
    @GET
    @Path("/participant/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getChatHistoryOfUser(@PathParam("id") int id) {
        List<Message> messageList  = MessageDAO.INSTANCE.getChatHistoryOfUser(id);
        return Response.ok().entity(messageList).build();
    }

    /**
     * Adds a new message to the system.
     *
     * @param jsonPayload the JSON payload containing the message information
     * @param request     the HttpServletRequest object
     * @return a Response object containing the ID of the added message or an error code if the CSRF token is invalid or an exception occurs
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addNewMessage(String jsonPayload,
                                  @Context HttpServletRequest request) {

        if (!Security.isValidCsrfToken(request)) {
            System.out.println("Invalid csrf addNewMessage");
            return Response.ok().build();
        }

        System.out.println("SENDING NEW MESSAGE: " + jsonPayload);

        int messageId = -1;

        try {
            JSONObject json = new JSONObject(jsonPayload);
            int senderId = json.getInt("senderId");
            int receiverId = json.getInt("receiverId");
            String messageText = json.getString("messageText");
            Instant currentTimestamp = Instant.now();
            Timestamp sqlTimestamp = Timestamp.from(currentTimestamp);

            Message message = new Message(senderId, receiverId, messageText, sqlTimestamp);
            messageId = MessageDAO.INSTANCE.addNewMessage(message);
        } catch (Exception e) {
            System.out.println("Exception in addNewMessage: " + e.getMessage());
        }
        return Response.ok().entity(messageId).build();
    }
}
