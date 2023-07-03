package notebridge1.notebridge.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import notebridge1.notebridge.dao.SkillDAO;
import notebridge1.notebridge.model.Skill;

import java.util.List;

@Path("/skill")
public class SkillsResource {

    /**
     * Retrieves all skills.
     *
     * @return a Response object containing the list of skills
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSkills() {
        List<Skill> skillList = SkillDAO.INSTANCE.getSkills();
        return Response.ok().entity(skillList).build();
    }

    /**
     * Retrieves the details of a specific skill.
     *
     * @param skillId the ID of the skill
     * @return a Response object containing the details of the skill
     */
    @GET
    @Path("/{skill-id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInstrumentDetails(@PathParam("skill-id") int skillId) {
        Skill skill = SkillDAO.INSTANCE.getSkillById(skillId);
        return Response.ok().entity(skill).build();
    }
}
