package pl.essay.angular.security;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import pl.essay.languages.*;

@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserSession implements Serializable {

	protected static final Logger logger = LoggerFactory.getLogger(UserSession.class);

	@Autowired
	UserService userService;

	@Autowired(required = true)
	@Qualifier(value = "polish")
	private Language languageSelected;

	@Autowired(required = true)
	@Qualifier(value = "languages")
	private Languages languages;

	private String name = "Guest";

	private String anonymousSessionId;

	private boolean authenticated = false;

	private boolean isAdmin = false;
	private boolean isUser = false;
	private boolean isSupervisor = false;

	private UserT user;

	public UserSession() {
	}

	public void setLanguageSelected(String c) {
		this.languageSelected = this.languages.getLanguage(c);
	}

	public Language getLanguageSelected() {
		return this.languageSelected;
	}

	public UserT getUser() {
		return this.user;
	}

	public void setUser(UserT u) {
		this.user = u;
	}

	public void setAuthenticated(boolean u) {
		this.authenticated = u;
	}

	public boolean getAuthenticated() {
		return this.authenticated;
	}

	public void updateOnAuthentication() {

		if (!this.authenticated) {

			Authentication auth = SecurityContextHolder.getContext().getAuthentication();

			if (auth != null) {

				if (auth.isAuthenticated()) {

					Object principal = auth.getPrincipal();

					String username;

					if (principal instanceof UserDetails) {
						username = ((UserDetails) principal).getUsername();
						if (principal instanceof UserT) {
							this.user = (UserT) principal;
							logger.trace("before lastloggedin");
							this.userService.setDateLastLoggedIn(user);
						}
					} else {
						username = principal.toString();
					}

					this.name = username;

					for (GrantedAuthority a : auth.getAuthorities()) {
						if (a.getAuthority().equals(UserForm.roleAdmin))
							this.isAdmin = true;
						if (a.getAuthority().equals(UserForm.roleSupervisor))
							this.isSupervisor = true;
						if (a.getAuthority().equals(UserForm.roleUser))
							this.isUser = true;
					}

				}
			}
		}
	}

	public void setAnonymousSessionId(String id) {
		if (this.anonymousSessionId == null)
			this.anonymousSessionId = id;
	}

	public String getAnonymousSessionId() {
		return this.anonymousSessionId;
	}

	public String toJson() {

		return "{\n" + "\"name\":\"" + this.name + "\",\n" + "\"languageSelectedFlag\":\""
				+ this.languageSelected.getFlag() + "\"\n" + (this.isAdmin ? ",\"isAdmin\":" + this.isAdmin : "")
				+ (this.isSupervisor ? ",\"isSupervisor\":" + this.isSupervisor : "")
				+ (this.isUser ? ",\"isUser\":" + this.isUser + "\n" : "") + "}";
	}

}
