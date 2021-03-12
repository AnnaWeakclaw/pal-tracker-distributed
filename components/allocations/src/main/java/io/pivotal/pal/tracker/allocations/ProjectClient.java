package io.pivotal.pal.tracker.allocations;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.web.client.RestOperations;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class ProjectClient {

    private final RestOperations restOperations;
    private final String registrationServerEndpoint;
    private final Map<Long, ProjectInfo> projectCacheMap = new ConcurrentHashMap<>();
    private final Logger logger = Logger.getLogger(ProjectClient.class.getName());

    public ProjectClient(RestOperations restOperations, String registrationServerEndpoint) {
        this.restOperations= restOperations;
        this.registrationServerEndpoint = registrationServerEndpoint;
    }

    @CircuitBreaker(name = "project", fallbackMethod = "getProjectFromCache")
    public ProjectInfo getProject(long projectId) {
        ProjectInfo projectInfo = restOperations.getForObject(registrationServerEndpoint + "/projects/" + projectId, ProjectInfo.class);
        projectCacheMap.put(projectId, projectInfo);
        return projectInfo;
    }

    public ProjectInfo getProjectFromCache(long projectId, Throwable cause) {
//        logger.log(Level.ALL, "Getting project with id {} from cache", projectId);
        return projectCacheMap.get(projectId);
    }
}
