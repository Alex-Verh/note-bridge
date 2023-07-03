package notebridge1.notebridge.resources;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import notebridge1.notebridge.Security;
import notebridge1.notebridge.dao.TeacherDAO;
import notebridge1.notebridge.dao.UserDAO;
import notebridge1.notebridge.model.Teacher;
import notebridge1.notebridge.model.User;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;

import java.util.ArrayList;
import java.util.List;

@Path("/teachers")
public class TeachersResource {

    /**
     * Retrieves the details of a specific teacher.
     *
     * @param teacherId the ID of the teacher
     * @return a Response object containing the teacher details
     */
    @GET
    @Path("/{teacherId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTeacherDetails(@PathParam("teacherId") int teacherId) {
        Teacher teacher = TeacherDAO.INSTANCE.getTeacherById(teacherId);
        return Response.ok().entity(teacher).build();
    }

    /**
     * Retrieves the total count of teachers.
     *
     * @return a Response object containing the total count of teachers
     */
    @GET
    @Path("/count")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getCountTeachers() {
        int count = TeacherDAO.INSTANCE.getTeacherCount();
        return Response.ok().entity(count).build();
    }

    /**
     * Searches for teachers based on the specified criteria.
     *
     * @param instrumentId the ID of the instrument (optional)
     * @param skillId      the ID of the skill (optional)
     * @param rating       the rating of the teacher (optional)
     * @param type         the type of teaching (online or not) (optional)
     * @param date         the date of availability (optional)
     * @return a Response object containing the list of matching teachers
     */
    @GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchTeachers(
            @DefaultValue("%%") @QueryParam("instrumentId") String instrumentId,
            @DefaultValue("%%") @QueryParam("skillId") String skillId,
            @DefaultValue("-1") @QueryParam("rating") int rating,
            @DefaultValue("%%") @QueryParam("online") String type,
            @DefaultValue("%%") @QueryParam("date") String date) {

        if (!type.equalsIgnoreCase("false") && !type.equalsIgnoreCase("true")) {
            type = "%%";
        }

        List<Teacher> teacherList = TeacherDAO.INSTANCE.searchTeachers(instrumentId, skillId, rating, type, date);
        return Response.ok().entity(teacherList).build();
    }

    /**
     * Updates the details of a teacher.
     *
     * @param fullName    the full name of the teacher
     * @param isOnline    the online status of the teacher
     * @param country     the country of the teacher
     * @param city        the city of the teacher
     * @param experience  the experience of the teacher
     * @param zipcode     the zipcode of the teacher
     * @param instruments the list of instruments taught by the teacher
     * @param request     the HttpServletRequest object
     * @return a Response object indicating the success of the operation
     */
    @PUT
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response updateTeacherDetails(@FormDataParam("editFullName") String fullName,
                                         @FormDataParam("is_online") String isOnline,
                                         @FormDataParam("editCountry") String country,
                                         @FormDataParam("editCity") String city,
                                         @FormDataParam("editExperience") String experience,
                                         @FormDataParam("editZip") String zipcode,
                                         @FormDataParam("instrument") List<FormDataBodyPart> instruments,
                                         @Context HttpServletRequest request){

        System.out.printf("received form info: %s %s %s %s %s %s %n", fullName, isOnline, country, city, experience, zipcode);

        if (!Security.isValidCsrfToken(request)) {
            System.out.println("Invalid csrf in updateTeacherDetails");
            return Response.ok().build();
        }

        zipcode = zipcode.toUpperCase().replaceAll("\\s", "");

        if (!checkSessionUser(request)) {
            return Response.ok().build();
        }
        User user = (User) request.getSession(false).getAttribute("user");
        int id = user.getId();


        List<String> selectedInstruments = new ArrayList<>();
        if (instruments != null) {
            for (FormDataBodyPart instrumentPart : instruments) {
                String instrument = instrumentPart.getValueAs(String.class);
                selectedInstruments.add(instrument);
                System.out.println("INSTRUMENT: " + instrument);
            }
        }

        System.out.println("SELECTED INSTRUMENTS: " + selectedInstruments.size());

        UserDAO.INSTANCE.updateUser(id, fullName, isOnline, country, city);
        TeacherDAO.INSTANCE.updateTeacherDetails(id, experience, zipcode);
        TeacherDAO.INSTANCE.updateTeacherInstruments(id, selectedInstruments);

        return Response.ok().build();
    }

    /**
     * Creates a new teacher.
     *
     * @param newTeacher the Teacher object representing the new teacher
     * @return a Response object indicating the success of the operation
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createTeacher(Teacher newTeacher) {
        int id = TeacherDAO.INSTANCE.insertTeacher(newTeacher);
        if (id == -1) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.ok().entity(id).build();
    }

    /**
     * Deletes a teacher by its ID.
     *
     * @param id the ID of the teacher
     * @return a Response object indicating the success of the operation
     */
    @DELETE
    @Path("/{id}")
    public Response deleteTeacher(@PathParam("id") int id) {
        if (TeacherDAO.INSTANCE.deleteTeacher(id) > 0) {
            return Response.ok().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    private boolean checkSessionUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        User user = (User) session.getAttribute("user");
        return user != null;
    }
}
