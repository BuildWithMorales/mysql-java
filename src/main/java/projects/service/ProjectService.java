package projects.service;

import projects.dao.ProjectDao;
import projects.entity.Project;
import java.util.List;

public class ProjectService {
	
	private ProjectDao projectDao = new ProjectDao();
	
	public Project addProject(Project project) {
		return projectDao.insertProject(project);
	}
	// This method connects to the DAO and returns all projects from the database 
	public List<Project> fetchAllProjects() {
		return projectDao.fetchAllProjects(); // Call the DAO method to retrieve data
	}
	public Project fetchProjectById(Integer projectId) {
		return projectDao.fetchProjectById(projectId);
	}
	public List<String> fetchMaterialsByProjectId(Integer projectId) {
		return projectDao.fetchMaterialsByProjectId(projectId);
	}
	public List<String> fetchStepsByProjectId(Integer projectId) {
		return projectDao.fetchStepsByProjectId(projectId);
	}
	public List<String> fetchCategoriesByProjectId(Integer projectId) {
		return projectDao.fetchCategoriesByProjectId(projectId);
	}

}
