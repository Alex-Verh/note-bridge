package notebridge1.notebridge.resources;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import notebridge1.notebridge.Security;
import notebridge1.notebridge.dao.TeacherDAO;
import notebridge1.notebridge.dao.UserDAO;
import notebridge1.notebridge.model.User;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.JSONObject;

import java.io.IOException;

@Path("/users")
public class UsersResource {

    /**
     * Retrieves the details of a specific user.
     *
     * @param userId the ID of the user
     * @return a Response object containing the user details
     */
    @GET
    @Path("/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserDetails(@PathParam("userId") int userId) {
        User user = UserDAO.INSTANCE.getUserById(userId);
        return Response.ok().entity(user).build();
    }


    /**
     * Retrieves the total count of users.
     *
     * @return a Response object containing the total count of users
     */
    @GET
    @Path("/count")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getCount() {
        int count = UserDAO.INSTANCE.countUsers();
        return Response.ok().entity(count).build();
    }

    /**
     * Retrieves the total count of unique cities in the user records.
     *
     * @return a Response object containing the total count of unique cities
     */
    @GET
    @Path("/count/city")
    public Response getCountCities() {
        int count = UserDAO.INSTANCE.countCities();
        return Response.ok().entity(count).build();
    }

    /**
     * Retrieves the ID of the currently logged-in user.
     *
     * @param request  the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @return a Response object containing the ID of the current user
     */
    @GET
    @Path("/current")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCurrentUser(@Context HttpServletRequest request,
                                   @Context HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return Response.ok().build();
        }
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Response.ok().build();
        }
        return Response.ok().entity(user.getId()).build();
    }

    /**
     * Retrieves the profile details of a specific user.
     *
     * @param userId   the ID of the user
     * @param request  the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @return a Response object containing the profile details of the user
     * @throws IOException if there is an error in the redirect process
     */
    @GET
    @Path("/profile/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProfileDetails(@PathParam("userId") int userId,
                                  @Context HttpServletRequest request,
                                  @Context HttpServletResponse response) throws IOException {

        System.out.println("Received GET request for /profile/{userId}");
        User reqUser = UserDAO.INSTANCE.getUserById(userId);

        if (reqUser == null) {
            response.sendRedirect("/main");
            return Response.ok().build();
        }
        boolean isTeacher = TeacherDAO.INSTANCE.checkTeacherExistsByID(userId);

        HttpSession session = request.getSession(false);
        boolean isSessionNull = session == null;

//        System.out.println("session: " + session);
//        System.out.println("is session null: " + isSessionNull);

        boolean isCurrentProfile = false;
        if (!isSessionNull) {
            User sessionUser = (User) session.getAttribute("user");
            if (sessionUser != null) {
                isCurrentProfile = reqUser.getId() == sessionUser.getId();
            }
        }

        JSONObject responseJson = new JSONObject();
        responseJson.put("id", reqUser.getId());
        responseJson.put("full_name", reqUser.getFullName());
        responseJson.put("email", reqUser.getEmail());
        responseJson.put("country", reqUser.getCountry());
        responseJson.put("city", reqUser.getCity());
        responseJson.put("pfp_path", reqUser.getPfpPath());
        responseJson.put("description", reqUser.getDescription());
        responseJson.put("is_teacher", isTeacher);
        responseJson.put("online", reqUser.isOnline());
        responseJson.put("is_current_profile", isCurrentProfile);

        return Response.ok(responseJson.toString()).build();
    }

    /**
     * Updates the details of a user.
     *
     * @param fullName the full name of the user
     * @param isOnline the online status of the user
     * @param country  the country of the user
     * @param city     the city of the user
     * @param request  the HttpServletRequest object
     * @return a Response object indicating the success of the operation
     */
    @PUT
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response updateUserDetails(@FormDataParam("editFullName") String fullName,
                                      @FormDataParam("is_online") String isOnline,
                                      @FormDataParam("editCountry") String country,
                                      @FormDataParam("editCity") String city,
                                      @Context HttpServletRequest request) {

        System.out.printf("USERS FORM UPDATE: %s %s %s %s %n", fullName, isOnline, country, city);

        if (!Security.isValidCsrfToken(request)) {
            System.out.println("Invalid csrf in updateUserDetails");
            return Response.ok().build();
        }

        if (checkSessionUser(request)) {
            return Response.ok().build();
        }
        User user = (User) request.getSession(false).getAttribute("user");
        int id = user.getId();

        boolean resp = UserDAO.INSTANCE.updateUser(id, fullName, isOnline, country, city);
        return Response.ok(resp).build();
    }

    /**
     * Updates the description of a user.
     *
     * @param jsonPayload the JSON payload containing the user description
     * @param request     the HttpServletRequest object
     * @param response    the HttpServletResponse object
     * @return a Response object indicating the success of the operation
     * @throws IOException if there is an error in the redirect process
     */
    @PUT
    @Path("/description")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUserDescription(String jsonPayload,
                                          @Context HttpServletRequest request,
                                          @Context HttpServletResponse response) throws IOException {

        System.out.println("USERS DESCRIPTION UPDATE: " + jsonPayload);

        if (!Security.isValidCsrfToken(request)) {
            System.out.println("Invalid csrf in updateDescription");
            response.sendRedirect("/main");
            return Response.ok().build();
        }

        if (checkSessionUser(request)) {
            return Response.ok().build();
        }
        User user = (User) request.getSession(false).getAttribute("user");
        int id = user.getId();

        try {
            JSONObject jsonObject = new JSONObject(jsonPayload);
            String profileDescription = jsonObject.getString("profileDescriptionInputData");

            boolean resp = UserDAO.INSTANCE.updateUserDescription(id, profileDescription);
            return Response.ok(resp).build();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return Response.ok().build();
    }

    /**
     * Checks if the current session user is valid.
     *
     * @param request the HttpServletRequest object
     * @return true if the session user is invalid, false otherwise
     */
    private boolean checkSessionUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return true;
        }
        User user = (User) session.getAttribute("user");
        return user == null;
    }

    /**
     * Creates a new user.
     *
     * @param newUser the User object representing the new user
     * @return a Response object indicating the success of the operation
     */
    // This function is used for directly posting user objects (mostly testing)
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createUser(User newUser) {
        int id = UserDAO.INSTANCE.insertUser(newUser);
        if (id != -1) {
            return Response.ok().entity(id).build();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * Updates the details of a user.
     *
     * @param newUser the User object representing the updated user details
     * @return a Response object indicating the success of the operation
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUserDetails(User newUser) {
        if (UserDAO.INSTANCE.getUserById(newUser.getId()) == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        if (UserDAO.INSTANCE.updateUser(newUser)) {
            return Response.ok().build();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * Deletes a user.
     *
     * @param userId the ID of the user to be deleted
     * @return a Response object indicating the success of the operation
     */
    @DELETE
    @Path("/{userId}")
    public Response deleteUser(@PathParam("userId") int userId) {
        if (UserDAO.INSTANCE.deleteUser(userId)) {
            return Response.ok().build();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * Checks if a user with the specified email exists.
     *
     * @param email    the email to check
     * @return a Response object containing a JSON indicating if the email exists
     */
    @GET
    @Path("/exists/{email}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getExistsMail(@PathParam("email") String email) {

        JSONObject responseJson = new JSONObject();
        responseJson.put("exists", UserDAO.INSTANCE.checkUserExistsByEmail(email));
        return Response.ok(responseJson.toString()).build();
    }
}
