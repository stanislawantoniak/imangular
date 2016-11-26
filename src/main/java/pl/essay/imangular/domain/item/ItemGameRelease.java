package pl.essay.imangular.domain.item;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@DynamicInsert
@DynamicUpdate
public class ItemGameRelease {
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)//, generator="item_seq")
	@Column
	private int id;

	@Column
	@NotNull(message="Release name must not be empty")
	private String name;

	@Column
	private String description;

	@Column
	private Date releaseDate;

	//setters & getters
	public void setId(int id){
		this.id = id;
	}
	public int getId(){
		return this.id;
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

	public void setReleaseDate(Date d){
		this.releaseDate = d;
	}
	
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd", timezone="CET")
	public Date getReleaseDate(){
		return this.releaseDate;
	}

}
