package notebridge1.notebridge.resources;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import notebridge1.notebridge.dao.InstrumentDAO;
import notebridge1.notebridge.model.Instrument;

import java.util.List;

@Path("/instruments")
public class InstrumentsResource {

    /**
     * Retrieves a list of all instruments.
     *
     * @return the Response object containing the list of instruments in JSON format
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInstruments() {
        List<Instrument> instrumentList = InstrumentDAO.INSTANCE.getInstruments();
        return Response.ok().entity(instrumentList).build();
    }

    /**
     * Retrieves the details of an instrument by its ID.
     *
     * @param instrumentId the ID of the instrument
     * @return the Response object containing the instrument details in JSON format
     */
    @GET
    @Path("/{instrumentId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInstrumentDetails(@PathParam("instrumentId") int instrumentId) {
        Instrument instrument = InstrumentDAO.INSTANCE.getInstrumentById(instrumentId);
        return Response.ok().entity(instrument).build();
    }

    /**
     * Retrieves the total count of instruments.
     *
     * @return the Response object containing the total count of instruments as plain text
     */
    @GET
    @Path("/count")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getCount() {
        int count = InstrumentDAO.INSTANCE.getInstrumentCount();
        return Response.ok().entity(count).build();
    }

    /**
     * Retrieves a list of instruments learned by a specific student.
     *
     * @param studentId the ID of the student
     * @return the Response object containing the list of instruments in JSON format
     */
    @GET
    @Path("/student/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInstrumentsLearnedByStudent(@PathParam("id") int studentId) {
        List<Instrument> instrumentList = InstrumentDAO.INSTANCE.getInstrumentsLearnedByStudent(studentId);
        return Response.ok().entity(instrumentList).build();
    }

    /**
     * Creates a new instrument.
     *
     * @param newInstrument the Instrument object representing the new instrument
     * @return the Response object indicating the success or failure of the operation
     */
    // This function is used for directly posting instrument objects (mostly testing)
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createInstrument(Instrument newInstrument) {
        int id = InstrumentDAO.INSTANCE.insertInstrument(newInstrument);
        if (id == -1) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.ok().entity(id).build();
    }

    /**
     * Updates the details of an instrument.
     *
     * @param newInstrument the Instrument object representing the updated instrument details
     * @return the Response object indicating the success or failure of the operation
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateInstrumentDetails(Instrument newInstrument) {
        if (InstrumentDAO.INSTANCE.updateInstrument(newInstrument) < 0) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.ok().build();
    }

    /**
     * Deletes an instrument by its ID.
     *
     * @param instrumentId the ID of the instrument
     * @return the Response object indicating the success or failure of the operation
     */
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{instrumentId}")
    public Response deleteInstrument(@PathParam("instrumentId") int instrumentId) {
        if (InstrumentDAO.INSTANCE.deleteInstrument(instrumentId) < 0) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.ok().build();
    }
}
