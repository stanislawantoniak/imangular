package pl.essay.angular.security;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;
import org.hibernate.annotations.Type;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@DynamicInsert
@DynamicUpdate
@Table(uniqueConstraints = {
		@UniqueConstraint(columnNames = "username")})
@NamedQueries(
		@NamedQuery(
				name = "getUserByName",
				query = "select u from UserT u where username = :name"
				)
		)
//@SequenceGenerator(name="user_seq", initialValue=1, allocationSize=100)
public class UserT implements UserDetails{

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)//, generator="user_seq")
	@Column
	private int id;

	@Column
	@NotNull(message="Name must not be empty")
	@Size(min=8, message="Name must be at least 8 characters long")
	private String username = "";

	@Column
	@NotNull(message="Password must not be empty")
	@Size(min=6, message="Password must be at least 6 characters long")
	private String password = "";

	@Column(columnDefinition="boolean") 
	private boolean enabled = false;

	@Column(nullable = false) 
	private String roles = ""; //roles serialized with ; as separator

	public UserT(){}

	public UserT(String name, String pass, String r, boolean e){
		this.username = name;
		this.password = pass;
		this.roles = r;
		this.enabled = e;
	}

	//setters & getters
	public void setId(int id){
		this.id = id;
	}
	public int getId(){
		return this.id;
	}
	public void setUsername(String name){
		this.username = name;
	}
	public String getUsername(){
		return this.username;
	}
	public void setPassword(String pass){
		this.password = pass;
	}
	public String getPassword(){
		return this.password;
	}
	public String getRoles(){
		return this.roles;
	}
	public void setRoles(String r){
		this.roles = r;
	}
	public void addRole(String r){
		this.roles += ";"+r; 
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

	private class Roles{

		Collection<GrantedAuthority> grantedAuthority;
		List<String> rolesList;

		public Roles(String rolesSerialized){
			
			grantedAuthority = new ArrayList<GrantedAuthority>();
			rolesList = new ArrayList<String>();

			if (!"".equals(rolesSerialized)){
				String[] roles = rolesSerialized.split(",");
				for (String role: roles)
					if (!"".equals(role)) {
						grantedAuthority.add(new SimpleGrantedAuthority(role));
						rolesList.add(role);
					}
			}
		}

		public Collection<GrantedAuthority> getGrantedAuthority(){
			return grantedAuthority;
		}

		public List<String> getRolesList(){
			return rolesList;
		}

		private class SimpleGrantedAuthority implements GrantedAuthority, Serializable{

			String role;
			public SimpleGrantedAuthority(String r){
				this.role = r;
			}

			public String getAuthority() {
				return this.role;
			}
			
			@Override 
			public String toString(){
				return this.role;
			}

		}

	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof UserT))
			return false;
		UserT other = (UserT) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override 
	public String toString(){
		return this.id+":"+this.username+":: active->"+this.enabled;
	}
}
