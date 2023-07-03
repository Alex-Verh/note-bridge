package notebridge1.notebridge.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import notebridge1.notebridge.dao.TeacherInstrumentsDAO;
import notebridge1.notebridge.model.Instrument;
import notebridge1.notebridge.model.Teacher;
import notebridge1.notebridge.model.TeacherInstruments;

import java.util.List;

@Path("/teacher-instruments")
public class TeacherInstrumentsResource {

    /**
     * Retrieves all teacher instruments.
     *
     * @return a Response object containing the list of teacher instruments
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTeacherInstruments() {
        List<TeacherInstruments> teacherInstrumentsList = TeacherInstrumentsDAO.INSTANCE.getTeacherInstruments();
        return Response.ok().entity(teacherInstrumentsList).build();
    }

    /**
     * Retrieves the teacher instruments for a specific teacher ID.
     *
     * @param id the ID of the teacher
     * @return a Response object containing the list of teacher instruments
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTeacherInstrumentsByTeacherId(@PathParam("id") int id) {
        List<Instrument> teacherInstrumentsList = TeacherInstrumentsDAO.INSTANCE.getInstrumentsByTeacherId(id);
        return Response.ok().entity(teacherInstrumentsList).build();
    }

    /**
     * Retrieves the teachers who teach a specific instrument ID.
     *
     * @param id the ID of the instrument
     * @return a Response object containing the list of teachers
     */
    @GET
    @Path("/instrument/{id}")
    public Response getTeacherInstrumentsByInstrumentId(@PathParam("id") int id) {
        List<Teacher> teacherList = TeacherInstrumentsDAO.INSTANCE.getTeachersByInstrumentId(id);
        return Response.ok().entity(teacherList).build();
    }

}
