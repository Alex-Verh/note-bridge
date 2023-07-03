package notebridge1.notebridge.resources;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import notebridge1.notebridge.dao.RandomPlusCountDAO;
import notebridge1.notebridge.dao.TeacherInstrumentsDAO;
import notebridge1.notebridge.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.net.URI;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

// run server before running the tests
public class GeneralResourceTest {
    Client client;
    WebTarget target;

    @BeforeEach
    void setUp() {
        client = ClientBuilder.newClient();
        target = client.target(getBaseUri());
    }

    private URI getBaseUri() {
        return UriBuilder.fromUri("http://localhost:8080/api/").build();
    }

    @AfterEach
    void tearDown() {
        client.close();
    }

    /**
     * The resource requires the following:
     * - A "/count" api
     * - A POST request to the default path which returns the new id
     * - A delete request to "/id" after the default path
     * - A get request to "/id" after the default path
     *
     * @param path             path after "/api"
     * @param model            the model object to insert
     * @param uniqueIdentifier such as an email to check if the model was inserted successfully
     */
    @ParameterizedTest
    @MethodSource("provideParameters")
    // this test should fail because the POST method requires a CSRF token,
    // which means users cannot change details about someone else's account
    public void testCreatePutDeleteCount(String path, Object model, String uniqueIdentifier) {
        // remember count of users
        int count = target.path(path + "/count").request().get(Integer.class);
        // insert a new user
        Response response = target.path(path).request(MediaType.APPLICATION_JSON).post(Entity.entity(model, MediaType.APPLICATION_JSON));
        String responseTextId = response.readEntity(String.class);
        System.out.println(responseTextId);
        // check if insert gave successful http code
        assertEquals(200, response.getStatus());
        // check if insert is in the database
        String getNewObject = target.path(path + "/" + responseTextId).request(MediaType.APPLICATION_JSON).get(String.class);
        assertTrue(getNewObject.contains(uniqueIdentifier));
        // check if amount of users has increased
        assertEquals(count + 1, target.path(path + "/count").request().get(Integer.class));
        // deleting the user
        Response delete = target.path(path + "/" + responseTextId).request().delete();
        // checking if delete code was successful
        assertEquals(200, delete.getStatus());
        // checking if user is actually deleted
        Response getDeleted = target.path(path + "/" + responseTextId).request(MediaType.APPLICATION_JSON).get();
        assertEquals(getDeleted.getStatus(), 200);
        // checking user count again
        assertEquals(count, target.path(path + "/count").request().get(Integer.class));
    }

    /**
     * Returns the arguments need for the test.
     *
     * @return stream of arguments
     */
    private static Stream<Arguments> provideParameters() {
        Random random = new Random();
        String email = "test12" + random.nextInt(1000) + "44@gmail.com";
        User user = new User(email, "HOijsafd#%223", "Testing Test", "Netherlands", "Deventer");

        int randomTeacherId = -1;
        int randomInstrumentIdFromTeacher = -1;
        while (randomInstrumentIdFromTeacher == -1) {
            randomTeacherId = RandomPlusCountDAO.INSTANCE.getRandomId("teacher");
            randomInstrumentIdFromTeacher = RandomPlusCountDAO.INSTANCE.getRandomInstrumentId(randomTeacherId);
        }
        Lesson lesson = new Lesson(randomTeacherId, 20.40, randomInstrumentIdFromTeacher, 2, "I liek teaching", "Title attempt #011");

        Instrument instrument = new Instrument("Harp");

        int randomStudentId = RandomPlusCountDAO.INSTANCE.getRandomId("users");
        int randomLessonId = RandomPlusCountDAO.INSTANCE.getLessonId();
        int scheduleId = RandomPlusCountDAO.INSTANCE.getScheduleId(randomLessonId);
        while (!RandomPlusCountDAO.INSTANCE.checkBookingExists(randomLessonId, scheduleId)) {
            randomStudentId = RandomPlusCountDAO.INSTANCE.getRandomId("users");
            randomLessonId = RandomPlusCountDAO.INSTANCE.getLessonId();
            scheduleId = RandomPlusCountDAO.INSTANCE.getScheduleId(randomLessonId);
        }
//        Booking booking = new Booking(randomStudentId, randomLessonId, scheduleId);
        Booking booking = RandomPlusCountDAO.INSTANCE.getNewValidBooking();

        int randomUserId = RandomPlusCountDAO.INSTANCE.getRandomId("users");
        int randomBookingId = RandomPlusCountDAO.INSTANCE.getRandomId("booking");
        Notification notification = new Notification(randomUserId, "50% off for team notedashbridge1 only", new Date(System.currentTimeMillis()), true, RandomPlusCountDAO.INSTANCE.getRandomId("users"), randomBookingId);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("CET"));
        String formattedDate = simpleDateFormat.format(notification.getDate());

        TeacherSchedule teacherSchedule = new TeacherSchedule(randomTeacherId);

        int randomUserNotTeacher = RandomPlusCountDAO.INSTANCE.getUserIdNotTeacher();
        Teacher teacher = new Teacher(randomUserNotTeacher, "I am very experienced", 0, "7521ZT", "/my_secret_videos/teaching");

        return Stream.of(
                Arguments.of("/users", user, user.getEmail()),
                Arguments.of("/lessons", lesson, String.valueOf(lesson.getTeacherId())),
                Arguments.of("/instruments", instrument, instrument.getName()),
                Arguments.of("/booking", booking, String.valueOf(booking.getScheduleId())),
                Arguments.of("/notification", notification, formattedDate),
                Arguments.of("/schedule", teacherSchedule, teacherSchedule.getStartTime().toString()),
                Arguments.of("/teachers", teacher, teacher.getExperience())
        );
    }

    // belongs to deleted database table
    /*
    @Test
    public void testGetCountryCount() {
        String path = "country";
        Response response = target.path(path + "/count").request().get();
        assertEquals(response.readEntity(String.class), "250");
    }


    @Test
    public void testGetCountry() {
        Response response = target.path("country/528").request(MediaType.APPLICATION_JSON).get();
        assertEquals("{\"id\":528,\"name\":\"Netherlands, Kingdom of the\"}", response.readEntity(String.class));
        // trying to get a non-existing country
        Response response1 = target.path("country/0").request(MediaType.APPLICATION_JSON).get();
        String text = response1.readEntity(String.class);
        assertEquals(200, response1.getStatus());
        assertTrue(text.contains("Not specified"));
    }
    */

    // should fail because the POST message requires an CSRF token
    // so that people cannot send a message as somebody else
    @Test
    public void testAddAndGetMessage() {
        // first we add a message
        int senderId = RandomPlusCountDAO.INSTANCE.getRandomId("users");
        Response response = target.path("message").request().post(
                Entity.entity(
                        new Message(
                                senderId,
                                RandomPlusCountDAO.INSTANCE.getRandomId("users"),
                                "Hello, how are you",
                                new Timestamp(System.currentTimeMillis())
                        ),
                        MediaType.APPLICATION_JSON
                )
        );
        String responseId = response.readEntity(String.class);
        System.out.println(responseId);
        assertEquals(200, response.getStatus());
        // then we check if the message is in the db
        Response response2 = target.path("message/" + senderId).request(MediaType.APPLICATION_JSON).get();
        String result = response2.readEntity(String.class);
        // checking if the message we get has the send id we put i
        System.out.println(result);
        assertTrue(result.contains(String.valueOf(senderId)));
    }

    @RepeatedTest(5)
    // should fail because users without CSRF token cannot add reviews for another user
    public void testAddAndGetReview() {
        // first we add a review
        List<Integer> list = RandomPlusCountDAO.INSTANCE.getTeacherWithStudent();
        int teacherId = list.get(0);
        int lessonId = list.get(1);
        int studentId = RandomPlusCountDAO.INSTANCE.getStudentOfTeacherId(teacherId);
        // check review count of teacher before adding
        Response response1 = target.path("reviews/count/" + teacherId).request(MediaType.APPLICATION_JSON).get();
        String firstCount = response1.readEntity(String.class);
        Form form = new Form();
        form.param("addMark", "8");
        form.param("reviewComment", "I did not like it");
        form.param("teacherId", Integer.toString(teacherId));
        form.param("studentId", Integer.toString(studentId));
        form.param("lessonId", Integer.toString(lessonId));
//        form.param(Security.CSRF_COOKIE_NAME, )
        Response response = target.path("reviews").request().post(
                Entity.entity(
                        new Review(
                                teacherId,
                                studentId,
                                8,
                                "I did not like it",
                                lessonId
                        ),
                        MediaType.APPLICATION_FORM_URLENCODED_TYPE
                )
        );
        String responseId = response.readEntity(String.class);
        System.out.println(responseId);
        String secondCount = target.path("reviews/count/" + teacherId).request(MediaType.APPLICATION_JSON).get().readEntity(String.class);
        assertEquals(Integer.parseInt(firstCount) + 1, Integer.parseInt(secondCount));
        assertEquals(200, response.getStatus());
        // then we check if the message is in the db
        Response response2 = target.path("reviews/teacher/" + teacherId).request(MediaType.APPLICATION_JSON).get();
        String result = response2.readEntity(String.class);
        // checking if the message we get has the send id we put i
        assertTrue(result.contains(String.valueOf(teacherId)) && result.contains(String.valueOf(studentId)));
    }


    @Test
    public void getSkill() {
        Response response = target.path("skill/2").request(MediaType.APPLICATION_JSON).get();
        assertTrue(response.readEntity(String.class).contains("Intermediate"));
        Response response1 = target.path("skill/4").request(MediaType.APPLICATION_JSON).get();
        assertTrue(response1.readEntity(String.class).isBlank());
    }

    @RepeatedTest(10)
    public void getTeacherInstruments() {
        int teacherId = RandomPlusCountDAO.INSTANCE.getRandomId("teacher");
        Response response = target.path("teacher-instruments/" + teacherId).request(MediaType.APPLICATION_JSON).get();
        assertEquals(200, response.getStatus());
        String entity = response.readEntity(String.class);
        List<Instrument> instrumentList = TeacherInstrumentsDAO.INSTANCE.getInstrumentsByTeacherId(teacherId);
        if (instrumentList.isEmpty()) {
            assertTrue(entity.contains("[]"));
        } else {
            assertTrue(entity.contains(Integer.toString(instrumentList.get(0).getId())));
        }

        int instrumentId = RandomPlusCountDAO.INSTANCE.getRandomId("instrument");
        List<Teacher> teacherList = TeacherInstrumentsDAO.INSTANCE.getTeachersByInstrumentId(instrumentId);
        Response response1 = target.path("teacher-instruments/instrument/" + instrumentId).request(MediaType.APPLICATION_JSON).get();
        assertEquals(200, response1.getStatus());
        String entity1 = response1.readEntity(String.class);
        if (teacherList.isEmpty()) {
            assertTrue(entity1.contains("[]"));
        } else {
            assertTrue(entity1.contains(Integer.toString(teacherList.get(0).getId())));
        }
    }
}
