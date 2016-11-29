package pl.essay.imangular.domain.gamerelease;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(
		uniqueConstraints = @UniqueConstraint(
				columnNames={"game_release_id", "seq"}
				)
		)
@DynamicInsert
@DynamicUpdate

public class GameReleaseStep {
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column
	private int id;

	@ManyToOne
	@Fetch(FetchMode.JOIN)
	@JoinColumn(referencedColumnName = "id", nullable = false)
	@JsonBackReference("steps")
	private ItemGameRelease gameRelease;

	@Column
	@NotNull(message="Order must not be empty")
	private int seq;

	@Column
	private String name;

	@Column
	private String description;

	@Column @Type(type="text")
	private String lines;	

	//setters & getters
	public void setId(int id){
		this.id = id;
	}
	public int getId(){
		return this.id;
	}

	public ItemGameRelease getGameRelease(){
		return this.gameRelease;
	}

	public void setGameRelease(ItemGameRelease i){
		this.gameRelease = i;
	}

	public void setSeq(int o){
		this.seq = o;
	}
	public int getSeq(){
		return this.seq;
	}

	public void setName(String name){
		this.name = name;
	}
	public String getName(){
		return this.name;
	}

	public void setDescription(String d){
		this.description = d;
	}
	public String getDescription(){
		return this.description;
	}

	public void setLines(String d){
		this.lines = d;
	}
	public String getLines(){
		return this.lines;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) return true;

		if ( !(other instanceof GameReleaseStep) ) return false;

		final GameReleaseStep otherStep = (GameReleaseStep) other;

		EqualsBuilder eb = new EqualsBuilder();
		eb.append(otherStep.gameRelease, this.gameRelease);
		eb.append(otherStep.seq, this.seq);

		return eb.isEquals();
	}

	@Override
	public int hashCode() {
		HashCodeBuilder hcb = new HashCodeBuilder();
		hcb.append(this.gameRelease);
		hcb.append(this.seq);
		return hcb.toHashCode();
	}

}
