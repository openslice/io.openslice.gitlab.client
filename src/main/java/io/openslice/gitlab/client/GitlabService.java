package io.openslice.gitlab.client;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.GroupParams;
import org.gitlab4j.api.models.Namespace;
import org.gitlab4j.api.models.Project;
import org.gitlab4j.api.models.User;
import org.gitlab4j.api.models.Visibility;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;


@SpringBootApplication
//@CamelZipkin
@EnableDiscoveryClient
//@EnableRetry
@RefreshScope
@EnableAutoConfiguration
@EnableConfigurationProperties
public class GitlabService {

	public static void main(String[] args) {

		//SpringApplication.run( GitlabService.class, args);
		
		
		
		// Create a GitLabApi instance to communicate with your GitLab server
		GitLabApi gitLabApi = new GitLabApi("https://gitlab.patras5g.eu/", "X1asbSxiPZUrXxx_gQtb");

		// Get the list of projects your account has access to
		try {
			List<Project> projects = gitLabApi.getProjectApi().getProjects();
			for (Project project : projects) {
				System.out.println( project.getName() + " - " + project.getPathWithNamespace() );				
				
			}
			
			//create default group
			var groups = gitLabApi.getGroupApi().getGroups("OpensliceDev");
			if ( groups.size() == 0 ) {
				GroupParams gparams = new GroupParams();
				DateTimeFormatter dt = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
				LocalDateTime now = LocalDateTime.now();

				gparams
					.withName( "OpensliceDev" )
					.withPath( "OpensliceDev" )
					.withVisibility( "internal" )
					.withDescription( "Automatically created by Openslice at " + now );
				gitLabApi.getGroupApi().createGroup( gparams  );
			}

			//create subgroup
			groups = gitLabApi.getGroupApi().getGroups("OpensliceDev");
			if ( groups.size() > 0 ) {
				Long parentGroup = groups.get(0).getId();
				GroupParams gparams = new GroupParams();

				groups = gitLabApi.getGroupApi().getGroups("OpensliceDev/tranoris");
				if ( groups.size() == 0) {
					DateTimeFormatter dt = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
					LocalDateTime now = LocalDateTime.now();
					gparams
						.withName( "tranoris" )
						.withPath( "tranoris" )
						.withParentId( parentGroup )
						.withVisibility( "internal" )
						.withDescription( "Automatically created by Openslice at " + now );
					gitLabApi.getGroupApi().createGroup( gparams  );
				}
			}
			

			//create subproject
			//based on https://gitlab.patras5g.eu/terraform/templates/admin/provision-k8s-cluster-with-kubeadm.git

			DateTimeFormatter dt = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
			LocalDateTime now = LocalDateTime.now();
			Project project = new Project();
			project.setName( "ServiceOrder" + UUID.randomUUID());
			project.setDescription( "Automatically created by Openslice at " + now );
			project.setVisibility(Visibility.INTERNAL);
			project.setInitializeWithReadme(false);
			

			groups = gitLabApi.getGroupApi().getGroups("OpensliceDev/tranoris");
			Namespace ns = new Namespace();
			ns.setId( groups.get(0).getId() );
			project.setNamespace(ns);
			//project.setPath( "OpensliceDev/tranoris/" + "ServiceOrder" + UUID.randomUUID());
			gitLabApi.getProjectApi().createProject(project, 
					"https://ctranoris:X1asbSxiPZUrXxx_gQtb@gitlab.patras5g.eu/terraform/templates/admin/provision-k8s-cluster-with-kubeadm.git");
			
//			//create user
//			var gitlabuser = gitLabApi.getUserApi().getUser("tranoris");
//			if ( gitlabuser == null ) {
//				CharSequence charSequence = new StringBuffer("tranoris");
//				User user = new User();
//				user
//					.withName("chris tranoris")
//					.withUsername("tranoris")
//					.withEmail("tranoris@ece.upatras.gr");
//				gitLabApi.getUserApi().createUser( user , charSequence , true);
//			}
			
		} catch (GitLabApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	

}
