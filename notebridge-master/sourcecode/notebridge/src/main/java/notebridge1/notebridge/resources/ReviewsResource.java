package notebridge1.notebridge.resources;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import notebridge1.notebridge.Security;
import notebridge1.notebridge.dao.ReviewDAO;
import notebridge1.notebridge.model.Review;

import java.io.IOException;
import java.util.List;

@Path("/reviews")
public class ReviewsResource {

    /**
     * Retrieves all reviews.
     *
     * @return a Response object containing the list of reviews
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReviews() {
        List<Review> reviewList = ReviewDAO.INSTANCE.getReviews();
        return Response.ok().entity(reviewList).build();
    }

    /**
     * Retrieves reviews of a specific teacher.
     *
     * @param id the ID of the teacher
     * @return a Response object containing the list of reviews for the teacher
     */
    @GET
    @Path("/teacher/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReviewsOfTeacher(@PathParam("id") int id) {
        List<Review> reviewList = ReviewDAO.INSTANCE.getReviewsOfTeacher(id);
        return Response.ok().entity(reviewList).build();
    }

    /**
     * Retrieves the count of reviews for a specific teacher.
     *
     * @param id the ID of the teacher
     * @return a Response object containing the count of reviews for the teacher
     */
    @GET
    @Path("/count/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReviewCountOfTeacher(@PathParam("id") int id) {
        int count = ReviewDAO.INSTANCE.getCountOfTeacher(id);
        return Response.ok().entity(count).build();
    }

    /**
     * Retrieves the count of reviews for a specific lesson from a student.
     *
     * @param lessonId  the ID of the lesson
     * @param studentId the ID of the student
     * @return a Response object containing the count of reviews for the lesson from the student
     */
    @GET
    @Path("/has-review-from/{lessonId}/{studentId}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getCountReviewsForLessonFromStudent(@PathParam("lessonId") int lessonId,
                                                        @PathParam("studentId") int studentId) {
        int res = ReviewDAO.INSTANCE.getCountReviewsForLessonFromStudent(lessonId, studentId);
        return Response.ok(res).build();
    }

    /**
     * Creates a new review.
     *
     * @param rating      the rating of the review
     * @param comment     the comment of the review
     * @param teacherId   the ID of the teacher
     * @param studentId   the ID of the student
     * @param lessonId    the ID of the lesson
     * @param csrfToken   the CSRF token for security
     * @param request     the HttpServletRequest object
     * @param response    the HttpServletResponse object
     * @return a Response object indicating the success of the review creation
     * @throws IOException if there is an error redirecting the response
     */
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response createReview(@FormParam("addMark") double rating,
                                 @FormParam("reviewComment") String comment,
                                 @FormParam("teacherId") int teacherId,
                                 @FormParam("studentId") int studentId,
                                 @FormParam("lessonId") int lessonId,
                                 @FormParam(Security.CSRF_COOKIE_NAME) String csrfToken,
                                 @Context HttpServletRequest request,
                                 @Context HttpServletResponse response) throws IOException {

        System.out.printf("CREATE REVIEW: %s %s %s %s %S %n", rating, comment, teacherId, studentId, lessonId);

        if (!Security.isValidCsrfToken(request, csrfToken)) {
            System.out.println("Invalid csrf in confirmNotification");
            return Response.ok().build();
        }

        Review review = new Review(teacherId, studentId, rating, comment, lessonId);
        int res = ReviewDAO.INSTANCE.addReviewToTeacher(review);

        response.sendRedirect("/profile");
        return Response.ok(res).build();
    }

    /**
     * Deletes a review.
     *
     * @param lessonId the ID of the lesson
     * @param studentId the ID of the student
     * @param request the HttpServletRequest object
     * @return a Response object indicating the success of the review deletion
     */
    @DELETE
    @Path("/{lessonId}/{studentId}")
    public Response deleteReview(@PathParam("lessonId") int lessonId,
                                 @PathParam("studentId") int studentId,
                                 @Context HttpServletRequest request) {

        if (!Security.isValidCsrfToken(request)) {
            System.out.println("Invalid csrf in deleteReview");
            return Response.ok().build();
        }

        int res = ReviewDAO.INSTANCE.deleteReview(lessonId, studentId);
        return Response.ok(res > 0).build();
    }
}
