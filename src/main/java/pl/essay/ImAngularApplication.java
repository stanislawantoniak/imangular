package pl.essay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@ComponentScan({
	"pl.essay.imangular",
	"pl.essay.session",
	"pl.essay.generic",
	"pl.essay.angular"
})

//@Import({ WebSecurityConfig.class })
@ImportResource({
	"classpath:/datasource-config.xml",
	"classpath:/language-beans.xml"
})
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
			    .antMatchers("/login.html","/","/logout","/common/labels").permitAll().anyRequest()
			    .authenticated().and()
			  .csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());

		}
	}
	
	public static void main(String[] args) {
		SpringApplication.run(ImAngularApplication.class, args);
	}

}
