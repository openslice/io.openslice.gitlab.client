package io.openslice.gitlab.client;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.http.HttpClientConfigurer;
import org.apache.camel.component.http.HttpComponent;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Value;



public class GitlabRouteBuilder  extends RouteBuilder {

	private static final transient Log logger = LogFactory.getLog( GitlabRouteBuilder.class.getName() );
	
	
	@Value("${gitlaburl}")
	private String GITLAB_URL;	
	
	@Value("${gitlabkey}")
	private String GITLAB_KEY;
	
	@Value("${gitlab_main_group}")
	private String MAIN_GROUP;
	
	

	public void configure() {
		HttpComponent httpComponent = getContext().getComponent("https", HttpComponent.class);
		httpComponent.setHttpClientConfigurer(new MyHttpClientConfigurer());


//		/**
//		 * Create user route, from activemq:topic:users.create
//		 */
//		
//		from("activemq:topic:users.create").routeId( "users-create-route" )
//		.unmarshal().json( JsonLibrary.Jackson, io.openslice.model.PortalUser.class, true)
//		.bean( BugzillaClient.class, "transformUser2BugzillaUser")
//		.marshal().json( JsonLibrary.Jackson,  true)
//		.convertBodyTo( String.class ).to("stream:out")
////		.errorHandler(deadLetterChannel("direct:dlq_users")
////				.maximumRedeliveries( 4 ) //let's try 10 times to send it....
////				.redeliveryDelay( 60000 ).useOriginalMessage()
////				.deadLetterHandleNewException( false )
////				//.logExhaustedMessageHistory(false)
////				.logExhausted(true)
////				.logHandled(true)
////				//.retriesExhaustedLogLevel(LoggingLevel.WARN)
////				.retryAttemptedLogLevel( LoggingLevel.WARN) )
//		.setHeader(Exchange.HTTP_METHOD, constant(org.apache.camel.component.http.HttpMethods.POST))
//		.toD( usedBUGZILLAURL + "/rest.cgi/user?api_key="+ BUGZILLAKEY +"&throwExceptionOnFailure=true")
//		.to("stream:out");
//		
	}
	
	
	
	public class MyHttpClientConfigurer implements HttpClientConfigurer {

		@Override
		public void configureHttpClient(HttpClientBuilder hc) {
			try {
				SSLContext sslContext;
				sslContext = new SSLContextBuilder().loadTrustMaterial(null, (certificate, authType) -> true).build();

				//hc.setSSLContext(sslContext).setSSLHostnameVerifier(new NoopHostnameVerifier()).build();

				SSLConnectionSocketFactory sslConnectionFactory = new SSLConnectionSocketFactory( sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
				hc.setSSLSocketFactory(sslConnectionFactory);
				Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
				        .register("https", sslConnectionFactory)
				        .build();

				HttpClientConnectionManager ccm = new BasicHttpClientConnectionManager(registry);

				hc.setConnectionManager(ccm);

			} catch (KeyManagementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (KeyStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}
