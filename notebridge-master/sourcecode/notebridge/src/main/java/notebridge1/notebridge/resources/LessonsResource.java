package notebridge1.notebridge.resources;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import notebridge1.notebridge.Security;
import notebridge1.notebridge.dao.LessonDAO;
import notebridge1.notebridge.model.Lesson;
import notebridge1.notebridge.model.User;

import java.io.IOException;
import java.util.*;

@Path("/lessons")
public class LessonsResource {

    /**
     * Retrieves a list of all lessons.
     *
     * @return the Response object containing the list of lessons in JSON format
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLessons() {
        List<Lesson> lessonList = LessonDAO.INSTANCE.getLessons();
        return Response.ok().entity(lessonList).build();
    }

    /**
     * Retrieves the details of a lesson by its ID.
     *
     * @param id the ID of the lesson
     * @return the Response object containing the lesson details in JSON format
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLessonById(@PathParam("id") int id) {
        Lesson lesson = LessonDAO.INSTANCE.getLessonById(id);
        return Response.ok().entity(lesson).build();
    }

    /**
     * Retrieves a list of lessons associated with a specific student.
     *
     * @param studentId the ID of the student
     * @return the Response object containing the list of lessons in JSON format
     */
    @GET
    @Path("/student/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLessonsOfStudent(@PathParam("id") int studentId) {
        System.out.println("request received");
        List<Lesson> lessonList = LessonDAO.INSTANCE.getLessonsByStudentId(studentId);
        return Response.ok().entity(lessonList).build();
    }

    /**
     * Retrieves the count of lessons associated with a specific student.
     *
     * @param studentId the ID of the student
     * @return the Response object containing the count of lessons as JSON format
     */
    @GET
    @Path("/student/count/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLessonsCountOfStudent(@PathParam("id") int studentId) {
        System.out.println("request received");
        List<Lesson> lessonList = LessonDAO.INSTANCE.getLessonsByStudentId(studentId);
        return Response.ok().entity(lessonList.size()).build();
    }

    /**
     * Retrieves a list of lessons associated with a specific teacher.
     *
     * @param teacherId the ID of the teacher
     * @return the Response object containing the list of lessons in JSON format
     */
    @GET
    @Path("/teacher/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLessonsOfTeacher(@PathParam("id") int teacherId) {
        List<Lesson> lessonList = LessonDAO.INSTANCE.getLessonsByTeacherId(teacherId);
        return Response.ok().entity(lessonList).build();
    }

    /**
     * Retrieves the total count of lessons.
     *
     * @return the Response object containing the total count of lessons
     */
    @GET
    @Path("/count")
    public Response getCountLessons() {
        int count = LessonDAO.INSTANCE.getNumberOfLessons();
        return Response.ok().entity(count).build();
    }

    /**
     * Updates the details of a lesson.
     *
     * @param lesson the Lesson object representing the updated lesson details
     * @return the Response object indicating the success or failure of the update operation
     */
    @PUT
    public Response updateLesson(Lesson lesson) {
        if (LessonDAO.INSTANCE.updateLesson(lesson) < 0) {
            return Response.ok().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    /**
     * Creates a new lesson.
     *
     * @param price       the price of the lesson
     * @param instrumentId the ID of the instrument associated with the lesson
     * @param skillId     the ID of the skill level associated with the lesson
     * @param description the description of the lesson
     * @param title       the title of the lesson
     * @param csrfToken   the CSRF token for security validation
     * @param request     the HttpServletRequest object
     * @param response    the HttpServletResponse object
     * @return the Response object indicating the success or failure of the lesson creation
     * @throws IOException if an I/O error occurs
     */
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response createLesson(@FormParam("lessonPrice") String price,
                                 @FormParam("lessonInstrument") String instrumentId,
                                 @FormParam("lessonLevel") String skillId,
                                 @FormParam("lessonDescription") String description,
                                 @FormParam("lessonTitle") String title,
                                 @FormParam(Security.CSRF_COOKIE_NAME) String csrfToken,
                                 @Context HttpServletRequest request,
                                 @Context HttpServletResponse response) throws IOException {

        if (!Security.isValidCsrfToken(request, csrfToken)) {
            System.out.println("Invalid csrf token in createLesson");
            return Response.ok().build();
        }

        User user = (User) request.getSession(false).getAttribute("user");
        int teacherId = user.getId();
        if(LessonDAO.INSTANCE.getLessonsByTeacherId(teacherId).size() == 3) {
            response.sendRedirect("/profile/"+teacherId+"?error=lesson");
            return null;
        }
        double priceDouble;
        int instrumentIdInt;
        int skillIdInt;

        try {
            priceDouble = Double.parseDouble(price);
            instrumentIdInt = Integer.parseInt(instrumentId);
            skillIdInt = Integer.parseInt(skillId);
        } catch (NumberFormatException e) {
            System.out.println("Wrong data type received in createLesson: " + e.getMessage());
            return Response.ok(-1).build();
        }

        Lesson lesson = new Lesson(teacherId, priceDouble, instrumentIdInt, skillIdInt, description, title);
        int id = LessonDAO.INSTANCE.insertLesson(lesson);

        response.sendRedirect("/profile");
        return Response.ok().entity(id).build();
    }

    /**
     * Deletes a lesson by its ID.
     *
     * @param lessonId the ID of the lesson to be deleted
     * @param request  the HttpServletRequest object
     * @return the Response object indicating the success or failure of the delete operation
     */
    @DELETE
    @Path("/{lessonId}")
    public Response deleteLesson(@PathParam("lessonId") int lessonId,
                                 @Context HttpServletRequest request) {

        if (!Security.isValidCsrfToken(request)) {
            System.out.println("Invalid csrf token in deleteLesson");
            return Response.ok().build();
        }
        boolean res = LessonDAO.INSTANCE.deleteLesson(lessonId);
        return Response.ok(res).build();
    }


    /**
     * Retrieves a list of lessons based on search criteria.
     *
     * @param offset       the offset value for pagination
     * @param instrumentId the ID of the instrument used for filtering lessons
     * @param skillId      the ID of the skill level used for filtering lessons
     * @param rating       the rating value used for filtering lessons (0-5 stars)
     * @param location     the location used for filtering lessons
     * @param availability the availability used for filtering lessons
     * @param type         the type used for filtering lessons
     * @return the Response object containing the filtered list of lessons in JSON format
     */
    @GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLessonsBySearch(
            @DefaultValue("0") @QueryParam("lessonOffset") int offset,
            @DefaultValue("%%") @QueryParam("lessonInstrumentId") String instrumentId,
            @DefaultValue("%%") @QueryParam("lessonSkillId") String skillId,
            @DefaultValue("-1") @QueryParam("lessonRating") int rating,
            @DefaultValue("%%") @QueryParam("lessonLocation") String location,
            @DefaultValue("%%") @QueryParam("lessonAvailability") String availability,
            @DefaultValue("%%") @QueryParam("lessonType") String type) {
        int ratingTen;
        Map<Integer, Integer> ratingMap = Map.of(0, 0,
                1, 1,
                2, 2,
                3, 4,
                4, 6,
                5, 8);
        if (rating != -1) {
            ratingTen = ratingMap.get(rating);
        } else {
            ratingTen = rating;
        }

        if (!type.equalsIgnoreCase("false") && !type.equalsIgnoreCase("true")) {
            type = "%%";
        }

        System.out.printf("Lesson search: %s, %s, %s, %s, %s, %s, %s %n",
                offset, instrumentId, skillId, ratingTen, location, availability, type);

        List<Lesson> lessonList = LessonDAO.INSTANCE.getLessonsBySearch(
                offset,
                instrumentId,
                skillId,
                ratingTen,
                location,
                availability,
                type
        );
        Collection<List<Lesson>> response = getLessonsByTeacher(lessonList);
        return Response.ok().entity(response).build();
    }

    /**
     * Groups the lessons by teacher.
     *
     * @param lessonList the list of lessons
     * @return a collection of lesson lists grouped by teacher
     */
    private Collection<List<Lesson>> getLessonsByTeacher(List<Lesson> lessonList) {
        Map<Integer, List<Lesson>> lessonsByTeacher = new HashMap<>();

        for (Lesson lesson : lessonList) {
            int teacherId = lesson.getTeacherId();
            if (lessonsByTeacher.containsKey(teacherId)) {
                lessonsByTeacher.get(teacherId).add(lesson);
            } else {
                lessonsByTeacher.put(teacherId, new ArrayList<>(List.of(lesson)));
            }
        }
        return lessonsByTeacher.values();
    }
}
