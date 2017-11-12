package pl.essay.angular.sessioncatcher;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonFormat;

import pl.essay.angular.security.UserT;

@Entity
@DynamicInsert
@DynamicUpdate

public class SessionLogEntry {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private int id;

	@Column
	private String ip;

	@Column
	private String sessionId;

	@Column
	private Date dateCreated;

	@ManyToOne
	@JoinColumn(referencedColumnName = "id", nullable = true)
	UserT user;

	public SessionLogEntry() {
	}

	// setters & getters
	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

	public String getIp() {
		return this.ip;
	}

	public void setIp(String i) {
		this.ip = i;
	}

	public String getSessionId() {
		return this.sessionId;
	}

	public void setSessionId(String i) {
		this.sessionId = i;
	}

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd, HH:mm:ss", timezone = "CET")
	public Date getDateCreated() {
		return this.dateCreated;
	}

	public void setDateCreated(Date d) {
		this.dateCreated = d;
	}

	public void setUser(UserT u) {
		this.user = u;
	}

	public UserT getUser() {
		return this.user;
	}

	public String toString() {
		return this.getId() + " ip:: " + this.getIp();
	}

}
