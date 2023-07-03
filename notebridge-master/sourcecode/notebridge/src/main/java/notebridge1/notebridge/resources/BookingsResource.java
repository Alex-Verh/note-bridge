package notebridge1.notebridge.resources;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import notebridge1.notebridge.Security;
import notebridge1.notebridge.dao.BookingDAO;
import notebridge1.notebridge.dao.LessonDAO;
import notebridge1.notebridge.dao.NotificationDAO;
import notebridge1.notebridge.model.Booking;
import notebridge1.notebridge.model.Lesson;
import notebridge1.notebridge.model.Notification;
import org.json.JSONObject;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Path("/booking")
public class BookingsResource {

    /**
     * Retrieves all bookings from the database.
     *
     * @return the Response object containing the list of bookings in JSON format
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBookings() {
        List<Booking> bookingList = BookingDAO.INSTANCE.getBookings();
        return Response.ok().entity(bookingList).build();
    }

    /**
     * Creates a booking, performs payment, and creates notifications based on the provided JSON payload.
     *
     * @param jsonPayload the JSON payload containing booking information
     * @param request     the HttpServletRequest object
     * @return the Response object indicating the success or failure of the operation
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createBookingAndPaymentAndNotification(String jsonPayload,
                                                           @Context HttpServletRequest request) {
        System.out.println("CREATING BookingAndPaymentAndNotification FROM JSON: " + jsonPayload);

        if (!Security.isValidCsrfToken(request)) {
            System.out.println("Invalid csrf in createBooking");
            return Response.ok().build();
        }

        try {
            JSONObject json = new JSONObject(jsonPayload);

            // BOOKING + PAYMENT
            int studentId = json.optInt("senderId"); // AKA SENDER ID
            int lessonId = json.optInt("lessonId");
            int scheduleId = json.optInt("scheduleId");
            boolean isCanceled = false;
            boolean isFinished = false;

            Booking booking = new Booking(studentId, lessonId, scheduleId, isCanceled, isFinished);
            int bookingId = BookingDAO.INSTANCE.insertBookingAlongWithPayment(booking);

            if (bookingId > -1) {
                System.out.println("Booking created successfully");
            }


            // 3 NOTIFICATIONS (1 student + 2 teacher)
            int receiverId = json.optInt("receiverId");

            System.out.printf("ARGUMENTS FOR NOTIFICATIONS: %s %s %s %s %n ", studentId, lessonId, scheduleId, bookingId);

            LocalDate currentDate = LocalDate.now();
            Date sqlDate = Date.valueOf(currentDate);

            Lesson lesson = LessonDAO.INSTANCE.getLessonById(lessonId);
            String lessonTitle = lesson.getTitle();

            String studentText = "Successfully booked lesson: " + lessonTitle;
            String teacherTextBookingCreated = String.format("Student %s booked your lesson with the title '%s'",
                    studentId, lessonTitle);
            String teacherTextConfirmBookingFinished = "Please confirm the lesson took place!";

            boolean isConfirmedNoAction = true;
            boolean isConfirmedNeedAction = false;

            // student notification
            Notification studentNotification = new Notification(studentId, studentText, sqlDate,
                    isConfirmedNoAction, receiverId, bookingId);
            int studentNotificationId = NotificationDAO.INSTANCE.addNotification(studentNotification);
            if (studentNotificationId > -1) {
                System.out.println("Successfully created student notification");
            }

            // teacher booking created notification
            Notification teacherBookingCreatedNotification = new Notification(receiverId, teacherTextBookingCreated,
                    sqlDate, isConfirmedNoAction, studentId, bookingId);
            int teacherBookingCreatedNotificationId = NotificationDAO.INSTANCE.addNotification(teacherBookingCreatedNotification);
            if (teacherBookingCreatedNotificationId > -1) {
                System.out.println("Successfully created teacher booking created notification");
            }

            Notification teacherConfirmBookingFinishedNotification = new Notification(receiverId,
                    teacherTextConfirmBookingFinished, sqlDate, isConfirmedNeedAction, studentId, bookingId);
            int teacherConfirmBookingFinishedId = NotificationDAO.INSTANCE.addNotification(teacherConfirmBookingFinishedNotification);
            if (teacherConfirmBookingFinishedId > -1) {
                System.out.println("Successfully created teacher confirm booking finished notification");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return Response.ok().build();
    }

    /**
     * Retrieves the number of bookings for a specific student.
     *
     * @param studentId the ID of the student
     * @return the Response object containing the number of bookings
     */
    @GET
    @Path("/count/{id}")
    public Response getLessonCount(@PathParam("id") int studentId) {
        int count = BookingDAO.INSTANCE.getBookingCount(studentId);
        return Response.ok().entity(count).build();
    }

    /**
     * Retrieves a booking by its ID.
     *
     * @param id the ID of the booking
     * @return the Response object containing the booking information in JSON format
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBookingById(@PathParam("id") int id) {
        Booking booking = BookingDAO.INSTANCE.getBookingById(id);
        return Response.ok().entity(booking).build();

    }

    /**
     * Retrieves the total number of bookings.
     *
     * @return the Response object containing the total number of bookings
     */
    @GET
    @Path("/count")
    public Response getBookingCount() {
        int count = BookingDAO.INSTANCE.getAllBookingCount();
        return Response.ok().entity(count).build();
    }

    /**
     * Retrieves bookings for a specific student.
     *
     * @param studentId the ID of the student
     * @return the Response object containing the list of bookings in JSON format
     */
    @GET
    @Path("/student/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBookingByStudentId(@PathParam("id") int studentId) {
        List<Booking> bookingList = BookingDAO.INSTANCE.getBookingsByStudentId(studentId);
        return Response.ok().entity(bookingList).build();
    }

    /**
     * Retrieves bookings for a specific schedule.
     *
     * @param scheduleId the ID of the schedule
     * @return the Response object containing the list of bookings in JSON format
     */
    @GET
    @Path("/schedule/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBookingByScheduleId(@PathParam("id") int scheduleId) {
        List<Booking> bookingList = BookingDAO.INSTANCE.getBookingsByScheduleId(scheduleId);
        return Response.ok().entity(bookingList).build();
    }

    /**
     * Sets a booking as canceled.
     *
     * @param bookingId the ID of the booking
     * @param request   the HttpServletRequest object
     * @return the Response object indicating the success or failure of the operation
     */
    @GET
    @Path("/canceled/{id}")
    public Response setBookingCancelled(@PathParam("id") int bookingId,
                                        @Context HttpServletRequest request) {
        if (!Security.isValidCsrfToken(request)) {
            System.out.println("Invalid csrf in setBookingCanceled");
            return Response.ok().build();
        }

        boolean res = BookingDAO.INSTANCE.setBookingCanceled(bookingId) < 0;
        return Response.ok(res).build();
    }

    /**
     * Sets a booking as finished.
     *
     * @param bookingId the ID of the booking
     * @param request   the HttpServletRequest object
     * @return the Response object indicating the success or failure of the operation
     */
    @GET
    @Path("/finished/{id}")
    public Response setBookingFinished(@PathParam("id") int bookingId,
                                       @Context HttpServletRequest request) {
        if (!Security.isValidCsrfToken(request)) {
            System.out.println("Invalid csrf in setBookingFinished");
            return Response.ok().build();
        }

        boolean res = BookingDAO.INSTANCE.setBookingFinished(bookingId) < 0;
        return Response.ok(res).build();
    }

    /**
     * Deletes a booking by its ID.
     *
     * @param bookingId the ID of the booking
     * @return the Response object indicating the success or failure of the operation
     */
    @DELETE
    @Path("/{id}")
    public Response deleteBookingById(@PathParam("id") int bookingId) {
        if (BookingDAO.INSTANCE.deleteBookingById(bookingId) < 0) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.ok().build();
    }
}
