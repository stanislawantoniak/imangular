package pl.essay.imangular.domain.news;


import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.*;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import pl.essay.angular.security.UserT;

@Entity
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@DynamicInsert
@DynamicUpdate
public class NewsItem {

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column
	private int id;

	@Column @Type(type="yes_no")
	private Boolean isPublished = false;

	@Column
	@NotNull(message="Title must not be empty")
	private String title;
	
	@Column @Type(type="text")
	private String content;

	@ManyToOne
	@JoinColumn(referencedColumnName = "id", nullable = true)
	//@JsonIgnore //never serialize userdetails - there is pass in it!
	UserT createdBy;
	
	@Column
	private Date dateCreated;
	
	@Column 
	private String category;
	
	@Column 
	private String bgmColor;

	@Column 
	private Integer priority;

	public NewsItem(){
	}

	public NewsItem(int id){
		this.id = id;
	}

	//setters & getters
	public void setId(int id){
		this.id = id;
	}
	public int getId(){
		return this.id;
	}
	
	public void setTitle(String name){
		this.title = name;
	}
	
	public String getTitle(){
		return this.title;
	}
	public void setContent(String name){
		this.content = name;
	}
	
	public String getContent(){
		return this.content;
	}
	
	public void setIsPublished(Boolean p){
		this.isPublished = p;
	}
	public boolean getIsPublished(){
		return this.isPublished;
	}

	public UserT getCreatedBy(){
		return this.createdBy;
	}
	
	public void setCreatedBy(UserT u){
		this.createdBy = u;
	}
	
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd,HH:00", timezone="CET")
	public Date getDateCreated(){
		return this.dateCreated;
	}
	
	public void setDateCreated(Date d){
		this.dateCreated = d;
	}
	
	public void setPriority(Integer o){
		this.priority = o;
	}
	public Integer getPriority(){
		return this.priority;
	}
	
	public void setCategory(String c){
		this.category = c;
	}
	
	public String getCategory(){
		return this.category;
	}
	
	public void setBgmColor(String c){
		this.bgmColor = c;
	}
	
	public String getBgmColor(){
		return this.bgmColor;
	}
	
	@JsonIgnore
	public String getDefaulSortColumn(){
		return "order";
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) return true;

		if ( !(other instanceof NewsItem) ) return false;

		final NewsItem b2 = (NewsItem) other;

		EqualsBuilder eb = new EqualsBuilder();
		eb.append(b2.title, this.title);
		eb.append(b2.category, this.category);
		return eb.isEquals();
	}

	@Override
	public int hashCode() {
		HashCodeBuilder hcb = new HashCodeBuilder();
		hcb.append(this.title);
		hcb.append(this.category);
		return hcb.toHashCode();
	}

}
