package io.openslice.gitlab.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gitlab4j.api.GitLabApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;


/**
 * @author ctranoris
 *
 */
@Configuration
@RefreshScope
public class GitlabClient {

	private static final transient Log logger = LogFactory.getLog( GitlabClient.class.getName());

	@Value("${gitlaburl}")
	private String GITLAB_URL;	
	
	@Value("${gitlabkey}")
	private String GITLAB_KEY;
	
	@Value("${gitlab_main_group}")
	private String MAIN_GROUP;

	private final String hostUrl;
	private final String apiToken;
	private GitLabApi api;
	
	
	public GitlabClient(String hostUrl, String apiToken) {
	    this.hostUrl = hostUrl;
	    this.apiToken = apiToken;
	    this.api = new GitLabApi( hostUrl, apiToken);
	}

	public void createProject(String projectName) {


	}
	
	
}
