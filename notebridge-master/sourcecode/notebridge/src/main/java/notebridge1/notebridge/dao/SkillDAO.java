package notebridge1.notebridge.dao;

import notebridge1.notebridge.Database;
import notebridge1.notebridge.model.Skill;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This enum class provides data access methods for managing skills in the database.
 */
public enum SkillDAO {
    INSTANCE;

    /**
     * Retrieves all the skills from the database.
     *
     * @return A list of skills.
     */
    public List<Skill> getSkills() {
        List<Skill> skillList = new ArrayList<>();

        String query = "SELECT * FROM skill";

        try {
            ResultSet res = Database.INSTANCE.getPreparedStatementQuery(query);
            while (res.next()) {
                int skillId = res.getInt(1);
                String name = res.getString(2);

                Skill skill = new Skill(skillId, name);
                skillList.add(skill);

                System.out.printf("%d %s %n", skillId, name);
            }
            return skillList;

        } catch (SQLException e) {
            System.err.println("Error from query: " + query + "\n With the SQL error: " + e);
            return null;
        }
    }

    /**
     * Retrieves a skill by its ID.
     *
     * @param id The ID of the skill.
     * @return The skill matching the given ID.
     */
    public Skill getSkillById(int id) {
        String query = """
                SELECT *
                FROM skill
                WHERE id = ?
                """;
        ResultSet res = Database.INSTANCE.getPreparedStatementQuery(query, new Object[]{id});
        try {
            res.next();
            int skillId = res.getInt(1);
            String name = res.getString(2);
            return new Skill(skillId, name);
        } catch (SQLException e) {
            System.err.println("Error from query: " + query + "\n With the SQL error: " + e);
            return null;
        }
    }
}
