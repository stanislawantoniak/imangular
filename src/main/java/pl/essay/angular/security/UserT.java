package pl.essay.angular.security;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@DynamicInsert
@DynamicUpdate
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = "username") })
@NamedQueries(@NamedQuery(name = "getUserByName", query = "select u from UserT u where username = :name"))
// @SequenceGenerator(name="user_seq", initialValue=1, allocationSize=100)
public class UserT implements UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // , generator="user_seq")
	@Column
	private int id;

	@Column
	private Date dateCreated;

	@Column
	private Date lastLoggedIn;

	@Column
	@NotNull(message = "Name must not be empty")
	private String username = "";

	@Column
	// @NotNull(message="Password must not be empty")
	// @Size(min=6, message="Password must be at least 6 characters long")
	private String password = "";

	@Column(columnDefinition = "boolean")
	private boolean enabled = false;

	@Column(nullable = false)
	private String roles = ""; // roles serialized with , as separator

	@Column(nullable = true)
	private String forgotPasswordHash;

	@Column(nullable = true)
	private Date forgotPasswordHashDate;

	public UserT() {
	}

	public UserT(String name, String pass, String r, boolean e) {
		this.username = name;
		this.password = pass;
		this.roles = r;
		this.enabled = e;
	}

	// setters & getters
	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

	public void setUsername(String name) {
		this.username = name;
	}

	public String getUsername() {
		return this.username;
	}

	public void setPassword(String pass) {
		this.password = pass;
	}

	public String getPassword() {
		return this.password;
	}

	public void setForgotPasswordHash(String h) {
		this.forgotPasswordHash = h;
	}

	public String getForgotPasswordHash() {
		return this.forgotPasswordHash;
	}

	public String getRoles() {
		return this.roles;
	}

	public void setRoles(String r) {
		this.roles = r;
	}

	public void addRole(String r) {
		this.roles += ";" + r;
	}

	public Collection<GrantedAuthority> getAuthorities() {
		Roles roles = new Roles(this.roles);
		return roles.getGrantedAuthority();
	}

	public List<String> getRolesList() {
		Roles roles = new Roles(this.roles);
		return roles.getRolesList();
	}

	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return true;
	}

	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	public void setEnabled(boolean e) {
		this.enabled = e;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd,HH:mm", timezone = "CET")
	public Date getDateCreated() {
		return this.dateCreated;
	}

	public void setDateCreated(Date d) {
		this.dateCreated = d;
	}

	public void setLastLoggedIn(Date d) {
		this.lastLoggedIn = d;
	}

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd,HH:mm", timezone = "CET")
	public Date getLastLoggedIn() {
		return this.lastLoggedIn;
	}

	public Date getForgotPasswordHashDate() {
		return this.forgotPasswordHashDate;
	}

	public void setForgotPasswordHashDate(Date d) {
		this.forgotPasswordHashDate = d;
	}

	public String getDafaulSortColumn() {
		return "username";
	}

	private class Roles {

		Collection<GrantedAuthority> grantedAuthority;
		List<String> rolesList;

		public Roles(String rolesSerialized) {

			grantedAuthority = new ArrayList<GrantedAuthority>();
			rolesList = new ArrayList<String>();

			if (!"".equals(rolesSerialized)) {
				String[] roles = rolesSerialized.split(",");
				for (String role : roles)
					if (!"".equals(role)) {
						grantedAuthority.add(new SimpleGrantedAuthority(role));
						rolesList.add(role);
					}
			}
		}

		public Collection<GrantedAuthority> getGrantedAuthority() {
			return grantedAuthority;
		}

		public List<String> getRolesList() {
			return rolesList;
		}

		private class SimpleGrantedAuthority implements GrantedAuthority, Serializable {

			String role;

			public SimpleGrantedAuthority(String r) {
				this.role = r;
			}

			public String getAuthority() {
				return this.role;
			}

			@Override
			public String toString() {
				return this.role;
			}

		}

	}

	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;

		if (!(other instanceof UserT))
			return false;

		final UserT b2 = (UserT) other;

		EqualsBuilder eb = new EqualsBuilder();
		eb.append(b2.username, this.username);

		return eb.isEquals();
	}

	@Override
	public int hashCode() {
		HashCodeBuilder hcb = new HashCodeBuilder();
		hcb.append(this.username);
		return hcb.toHashCode();
	}

	@Override
	public String toString() {
		return this.id + ":" + this.username + ":: active->" + this.enabled;
	}
}
