package notebridge1.notebridge.resources;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import notebridge1.notebridge.Security;
import notebridge1.notebridge.dao.TeacherScheduleDAO;
import notebridge1.notebridge.model.TeacherSchedule;
import org.json.JSONObject;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;

@Path("/schedule")
public class TeacherSchedulesResource {

    /**
     * Retrieves all teacher schedules.
     *
     * @return a Response object containing the list of teacher schedules
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTeacherSchedules() {
        List<TeacherSchedule> teacherScheduleList = TeacherScheduleDAO.INSTANCE.getTeacherSchedules();
        return Response.ok().entity(teacherScheduleList).build();
    }

    /**
     * Retrieves a specific teacher schedule by its ID.
     *
     * @param id the ID of the teacher schedule
     * @return a Response object containing the teacher schedule
     */
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTeacherScheduleById(@PathParam("id") int id) {
        TeacherSchedule teacherSchedule = TeacherScheduleDAO.INSTANCE.getTeacherScheduleById(id);
        return Response.ok().entity(teacherSchedule).build();
    }

    /**
     * Retrieves the teacher schedules for a specific teacher ID.
     * The schedules are sorted by date and start time.
     *
     * @param id the ID of the teacher
     * @return a Response object containing the list of teacher schedules
     */
    @GET
    @Path("/teacher/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTeacherScheduleByTeacher(@PathParam("id") int id) {
        List<TeacherSchedule> scheduleList = TeacherScheduleDAO.INSTANCE.getTeacherSchedulesByTeacherId(id);

        scheduleList.sort(Comparator.comparing(TeacherSchedule::getDate).thenComparing(TeacherSchedule::getStartTime));

        return Response.ok().entity(scheduleList).build();
    }

    /**
     * Retrieves the available teacher schedules for a specific teacher ID.
     *
     * @param id the ID of the teacher
     * @return a Response object containing the list of available teacher schedules
     */
    @GET
    @Path("/teacher/free/{teacherId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFreeTeacherSchedulesByTeacherId(@PathParam("teacherId") int id) {
        List<TeacherSchedule> scheduleList = TeacherScheduleDAO.INSTANCE.getFreeTeacherSchedulesByTeacherId(id);
        return Response.ok().entity(scheduleList).build();
    }

    /**
     * Retrieves the total count of teacher schedules.
     *
     * @return a Response object containing the total count of teacher schedules
     */
    @GET
    @Path("/count")
    public Response getTotalScheduleCount() {
        int count = TeacherScheduleDAO.INSTANCE.getTeacherScheduleCount();
        return Response.ok().entity(count).build();
    }

    /**
     * Creates a new teacher schedule.
     *
     * @param jsonPayload the JSON payload containing the schedule details
     * @param request     the HttpServletRequest object
     * @return a Response object indicating the success of the operation
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createTeacherSchedule(String jsonPayload,
                                          @Context HttpServletRequest request) {

        if (!Security.isValidCsrfToken(request)) {
            System.out.println("Invalid csrf in createSchedule");
            return Response.ok().build();
        }

        boolean res = false;

        try {
            JSONObject json = new JSONObject(jsonPayload);
            int teacherId = json.getInt("teacherId");

            String dateString = json.getString("date");
            Date sqlDate = Date.valueOf(dateString);

            String startTimeString = json.getString("startTime");
            LocalTime startTimeLocal = LocalTime.parse(startTimeString);
            Time sqlStartTime = Time.valueOf(startTimeLocal);

            String endTimeString = json.getString("endTime");
            LocalTime endTimeLocal = LocalTime.parse(endTimeString);
            Time sqlEndTime = Time.valueOf(endTimeLocal);


            TeacherSchedule schedule = new TeacherSchedule(teacherId, sqlDate, sqlStartTime, sqlEndTime);
            res = TeacherScheduleDAO.INSTANCE.insertTeacherSchedule(schedule) > 0;
        } catch (Exception e) {
            System.out.println("Error creating teacher schedule: " + jsonPayload);
        }

        return Response.ok(res).build();
    }

    /**
     * Deletes a teacher schedule by its ID.
     *
     * @param id      the ID of the teacher schedule
     * @param request the HttpServletRequest object
     * @return a Response object indicating the success of the operation
     */
    @DELETE
    @Path("/{id}")
    public Response deleteTeacherSchedule(@PathParam("id") int id,
                                          @Context HttpServletRequest request) {

        if (!Security.isValidCsrfToken(request)) {
            System.out.println("Invalid csrf in deleteSchedule");
            return Response.ok().build();
        }

        boolean res = TeacherScheduleDAO.INSTANCE.deleteTeacherSchedule(id) > 0;
        return Response.ok(res).build();
    }
}
