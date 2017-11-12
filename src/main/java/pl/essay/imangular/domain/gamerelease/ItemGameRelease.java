package pl.essay.imangular.domain.gamerelease;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@DynamicInsert
@DynamicUpdate
public class ItemGameRelease {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // , generator="item_seq")
	@Column
	private int id;

	@Column
	@NotNull(message = "Release name must not be empty")
	private String name;

	@Column
	@Type(type = "text")
	private String description;

	@Column
	private Date releaseDate;

	@OneToMany(orphanRemoval = true, fetch = FetchType.EAGER, mappedBy = "gameRelease", cascade = { CascadeType.ALL })
	@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	@JsonManagedReference("steps")
	private Set<GameReleaseStep> steps = new HashSet<GameReleaseStep>();

	// setters & getters
	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setDescription(String d) {
		this.description = d;
	}

	public String getDescription() {
		return this.description;
	}

	public void setReleaseDate(Date d) {
		this.releaseDate = d;
	}

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "CET")
	public Date getReleaseDate() {
		return this.releaseDate;
	}

	public Set<GameReleaseStep> getSteps() {
		return this.steps;
	}

	public void setSteps(Set<GameReleaseStep> s) {
		this.steps = s;
	}

}
