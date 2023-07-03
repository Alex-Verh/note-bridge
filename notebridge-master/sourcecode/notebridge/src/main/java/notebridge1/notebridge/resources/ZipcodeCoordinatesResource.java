package notebridge1.notebridge.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import notebridge1.notebridge.dao.ZipcodeCoordinateDAO;
import notebridge1.notebridge.model.ZipcodeCoordinate;

import java.util.List;

@Path("/zipcode-coordinates")
public class ZipcodeCoordinatesResource {

    /**
     * Retrieves a list of all zipcode coordinates.
     *
     * @return a Response object containing a list of zipcode coordinates in JSON format
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getZipcodeCoordinates() {
        List<ZipcodeCoordinate> zipcodeCoordinates = ZipcodeCoordinateDAO.INSTANCE.getZipcodeCoordinates();
        return Response.ok().entity(zipcodeCoordinates).build();
    }


    /**
     * Retrieves the zipcode coordinate for the specified zipcode.
     *
     * @param zipcode the zipcode for which to retrieve the coordinate
     * @return a Response object containing the zipcode coordinate in JSON format,
     *         or an empty response if the coordinate is not found
     */
    @Path("/{zipcode}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getZipcodeCoordinateByName(@PathParam("zipcode") String zipcode) {
        ZipcodeCoordinate zipcodeByName = ZipcodeCoordinateDAO.INSTANCE.getZipcodeByName(zipcode);
        if (zipcodeByName != null) {
            return Response.ok().entity(zipcodeByName).build();
        }
        return Response.ok().build();
    }
}
