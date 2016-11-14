package pl.essay;

import java.net.URI;
import java.net.URISyntaxException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.orm.jpa.vendor.HibernateJpaSessionFactoryBean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import pl.essay.toolbox.EmailSender;

@SpringBootApplication
@ComponentScan({
	"pl.essay.imangular",
	"pl.essay.generic",
	"pl.essay.angular",
	"pl.essay.languages"
})

//@Import({ WebSecurityConfig.class })
@ImportResource({
	"classpath:/datasource-config.xml",
	"classpath:/language-beans.xml"
})
@EnableGlobalMethodSecurity(prePostEnabled=true)
public class ImAngularApplication {

	@Configuration
	@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
	protected static class SecurityConfiguration extends WebSecurityConfigurerAdapter {
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			/*		
			CharacterEncodingFilter filter = new CharacterEncodingFilter();
			filter.setEncoding("UTF-8");
			filter.setForceEncoding(true);
			http.addFilterBefore(filter,CsrfFilter.class);
			System.out.println("after utf8 filter reg");	
			 */	
			http
			.httpBasic()
			
			.and()
			.authorizeRequests()
			.antMatchers(
					"/fonts/**","/img/**","/vendors/**",
					"/login", "/","/logout","/common/**",
					"/register","/userexists","/forgotpass",
					"/boms/**","/bomrest/**","/bomstockrest/**",
					"/items/**", "/itemscount/**", "/itemrest/**").permitAll().anyRequest().authenticated().
			
			and()
			.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());

		}
	}

	@Bean
	public HibernateJpaSessionFactoryBean sessionFactory() {
		return new HibernateJpaSessionFactoryBean();
	}
	
	@Bean
	public EmailSender emailSender(){
		//tofix - move setup to props
		return new EmailSender("itemmaker@wp.pl");
	}

	//from:
	//http://www.programcreek.com/java-api-examples/index.php?source_dir=categolj2-backend-master/backend-api/src/main/java/am/ik/categolj2/config/InfraConfig.java
	@Configuration 
	public static class PropertyDbConfiguration { 

		@Autowired 
		DataSourceProperties dataSourceProperties; 

		@Bean 
		DataSourceBuilder realDataSourceBuilder() throws URISyntaxException { 

			System.out.println("full url from props::"+this.dataSourceProperties.getUrl());

			URI uri = new URI(this.dataSourceProperties.getUrl());

			String url;
			String username;
			String password;
			
			System.out.println("uri user::"+uri.getUserInfo());
			
			if ( uri.getUserInfo() != null){ //in case we have user and pass in url
				url = "jdbc:postgresql://" + uri.getHost() + ':' + uri.getPort() + uri.getPath();
				username = uri.getUserInfo().split(":")[0];
				password = uri.getUserInfo().split(":")[1];
			} else {
				url = this.dataSourceProperties.getUrl();
				username = this.dataSourceProperties.getUsername();
				password = this.dataSourceProperties.getPassword();		
			}
			System.out.println("url from props::"+url);
			System.out.println("name from props::"+username);
			System.out.println("pass from props::"+password);

			DataSourceBuilder factory = DataSourceBuilder 
					.create(this.dataSourceProperties.getClassLoader()) 
					.url(url) 
					.username(username) 
					.password(password); 
			return factory; 
		}

		@Bean
		@Primary
		public DataSource dataSource(DataSourceBuilder factory) { 
			DataSource dataSource = factory.build(); 
			return dataSource;
		} 
	} 

	public static void main(String[] args) {
		SpringApplication.run(ImAngularApplication.class, args);
	}

}
