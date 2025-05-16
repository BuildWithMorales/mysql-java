package projects.dao;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.math.BigDecimal;
import java.util.ArrayList;

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
    public List<Project> fetchAllProjects() {
    	//SQL to select all project rows, ordered alphabetically
    	String sql = "SELECT * FROM " + Project_Table + " ORDER BY project_id";
    	
    	try (Connection conn =DbConnection.getConnection()) {
    		startTransaction(conn); // Start transaction
    		
    		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
    			try (ResultSet rs = stmt.executeQuery()) {
    				List<Project> projects = new ArrayList<>(); // List to hold project rows
    				
    				while (rs.next()) {
    					Project project = new Project(); // Create a new Project object 
    					
    					// Populate Project fields from database columns 
    					project.setProjectId(rs.getInt("project_id"));
    					project.setProjectName(rs.getString("project_name"));
    					project.setEstimatedHours(rs.getBigDecimal("estimated_hours"));
    					project.setActualHours(rs.getBigDecimal("actual_hours"));
    					project.setDifficulty(rs.getObject("difficulty", Integer.class));
    					project.setNotes(rs.getString("notes"));
    					
    					projects.add(project); // Add to list
    				}
    				
    				commitTransaction(conn); // Commit after success
    				return projects;
    			}
    		} catch (Exception e) {
    			rollbackTransaction(conn); // Roll back on failure 
    			throw new DbException("Error fetching project list.", e);
    		}
    	} catch (SQLException e) {
    		throw new DbException("Database connection error.", e); }
    	}
    	public Project fetchProjectById(Integer projectId) {
    		String sql = "SELECT * FROM " + Project_Table + " WHERE project_id = ?";
    		
    		try (Connection conn = DbConnection.getConnection()) {
    			startTransaction(conn);
    			
    			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
    				setParameter(stmt, 1, projectId, Integer.class);
    				
    				try (ResultSet rs = stmt.executeQuery()) {
    					if (rs.next()) {
    						Project project = new Project();
    						
    						project.setProjectId(rs.getInt("project_id"));
    						project.setProjectName(rs.getString("project_name"));
    						project.setEstimatedHours(rs.getBigDecimal("estimated_hours"));
    						project.setActualHours(rs.getBigDecimal("actual_hours"));
    						project.setDifficulty(rs.getObject("difficulty", Integer.class));
    						project.setNotes(rs.getString("notes"));
    						
    						commitTransaction(conn);
    						return project;
    						
    					  }
    					}	
    				commitTransaction(conn);
    			} catch (Exception e) {
    				rollbackTransaction(conn);
    				throw new DbException("Error fetching project with ID=" + projectId, e);
    		    	} 
       			} catch (SQLException e) {
       				throw new DbException("Database error.", e);
    		}
    		return null; // Return null if project not found
    
    }
    	public List<String> fetchMaterialsByProjectId(Integer projectId) {
    	    String sql = "SELECT material_name FROM material WHERE project_id = ?";

    	    try (Connection conn = DbConnection.getConnection();
    	         PreparedStatement stmt = conn.prepareStatement(sql)) {

    	        setParameter(stmt, 1, projectId, Integer.class);

    	        try (ResultSet rs = stmt.executeQuery()) {
    	            List<String> materials = new ArrayList<>();

    	            while (rs.next()) {
    	                materials.add(rs.getString("material_name"));
    	            }

    	            return materials;
    	        }

    	    } catch (SQLException e) {
    	        throw new DbException("Error fetching materials for project ID " + projectId, e);
    	    }
    	}

    	public List<String> fetchStepsByProjectId(Integer projectId) {
    	    String sql = "SELECT step_text FROM step WHERE project_id = ? ORDER BY step_order";

    	    try (Connection conn = DbConnection.getConnection();
    	         PreparedStatement stmt = conn.prepareStatement(sql)) {

    	        setParameter(stmt, 1, projectId, Integer.class);

    	        try (ResultSet rs = stmt.executeQuery()) {
    	            List<String> steps = new ArrayList<>();

    	            while (rs.next()) {
    	                steps.add(rs.getString("step_text"));
    	            }

    	            return steps;
    	        }

    	    } catch (SQLException e) {
    	        throw new DbException("Error fetching steps for project ID: " + projectId, e);
    	    }
    	}
    	    public List<String> fetchCategoriesByProjectId(Integer projectId) {
    	    	String sql = "SELECT c.category_name FROM category c "
    	    			   + "JOIN project_category pc On c.category_id = pc.category_id "
    	    			   + "WHERE pc.project_id = ?";
    	    	try (Connection conn = DbConnection.getConnection();
    	    		 PreparedStatement stmt = conn.prepareStatement(sql)) {
    	    		
    	    		setParameter(stmt, 1, projectId, Integer.class);
    	    		
    	    		try (ResultSet rs = stmt.executeQuery()) {
    	    			List<String> categories = new ArrayList<>();
    	    			
    	    			while (rs.next()) {
    	    				categories.add(rs.getString("category_name"));
    	    			}
    	    			
    	    			return categories;
    	    		}
    	    	} catch (SQLException e) {
    	    		throw new DbException("Error fetching categories for project ID: " + projectId, e);
    	    	}
    	    }
    	    public boolean modifyProjectDetails(Project project) {
    	    	String sql = ""
    	    		+ "UPDATE " + Project_Table + " SET "
    	    		+ "project_name = ?, "
    	    		+ "estimated_hours = ?, "
    	    		+ "actual_hours = ?, "
    	    		+ "difficulty = ?, "
    	    		+ "notes = ? "
    	    		+ "WHERE project_id = ?";
    	    	
    	    	try (Connection conn = DbConnection.getConnection()) {
    	    		startTransaction(conn);
    	    		
    	    		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
    	    			setParameter(stmt, 1, project.getProjectName(), String.class);
    	    			setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
    	    			setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
    	    			setParameter(stmt, 4, project.getDifficulty(), Integer.class);
    	    			setParameter(stmt, 5, project.getNotes(), String.class);
    	    			setParameter(stmt, 6, project.getProjectId(), Integer.class);
    	    			
    	    			int rowsAffected = stmt.executeUpdate();
    	    			
    	    			commitTransaction(conn);
    	    			return rowsAffected == 1;
    	    		} catch (Exception e) {
    	    			rollbackTransaction(conn);
    	    			throw new DbException("Error updating project details.", e);
    	    		}
    	    	} catch (SQLException e) {
    	    		throw new DbException("Database connection error.", e);
    	    	}
    	    }
    	    public boolean deleteProject(Integer projectId) {
    	        String sql = "DELETE FROM project WHERE project_id = ?";

    	        try (Connection conn = DbConnection.getConnection()) {
    	            startTransaction(conn);

    	            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
    	                setParameter(stmt, 1, projectId, Integer.class);

    	                int rowsAffected = stmt.executeUpdate();

    	                commitTransaction(conn);
    	                return rowsAffected == 1;
    	            } catch (Exception e) {
    	                rollbackTransaction(conn);
    	                throw new DbException("Error deleting project with ID=" + projectId, e);
    	            }
    	        } catch (SQLException e) {
    	            throw new DbException("Database error while deleting project.", e);
    	        }
    	    }
    	}  
