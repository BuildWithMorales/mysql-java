package projects.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.math.BigDecimal;

import projects.entity.Project;
import projects.exception.DbException;
import provided.util.DaoBase;

public class ProjectDao extends DaoBase {
	
	// Added table name constants
	private static final String Category_Table = "category";
	private static final String Material_Table = "material";
	private static final String Project_Table = "project";
	private static final String Project_Category_Table = "project_category";
	private static final String Step_Table = "step";

    public Project insertProject(Project project) {
    	String sql = "INSERT INTO " + Project_Table + " (project_name, estimated_hours, actual_hours, difficulty, notes) "
                + "VALUES (?, ?, ?, ?, ?)";


        try (Connection conn = DbConnection.getConnection()) {
            startTransaction(conn);

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                // Use DaoBase helper to set values safely
                setParameter(stmt, 1, project.getProjectName(), String.class);
                setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
                setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
                setParameter(stmt, 4, project.getDifficulty(), Integer.class);
                setParameter(stmt, 5, project.getNotes(), String.class);

                stmt.executeUpdate(); // Execute the insert
                
                Integer projectId = getLastInsertId(conn, Project_Table);

                commitTransaction(conn);
                
                project.setProjectId(projectId);
                return project; // Return project object (with no project_id yet)
            } catch (Exception e) {
                rollbackTransaction(conn);
                throw new DbException("Error inserting project.", e);
            }
        } catch (SQLException e) {
            throw new DbException("Database error.", e);
        }
    }
}