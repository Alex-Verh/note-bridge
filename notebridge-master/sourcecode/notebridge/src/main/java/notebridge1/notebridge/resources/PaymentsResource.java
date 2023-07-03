package notebridge1.notebridge.resources;

import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import notebridge1.notebridge.dao.PaymentDAO;

@Path("/payment")
public class PaymentsResource {

    /**
     * Updates a payment to "paid" status.
     *
     * @param id the ID of the payment
     * @return a Response object indicating the success of the update or an error code if the payment is not found
     */
    @PUT
    @Path("/{id}")
    public Response updatePaymentToPayed(@PathParam("id") int id) {
        if (PaymentDAO.INSTANCE.updatePaymentToPayed(id) < 0) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok().build();
    }
}
