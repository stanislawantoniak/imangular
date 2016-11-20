package pl.essay.imangular.domain.item;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@DynamicInsert
@DynamicUpdate
public class ItemGameRelease {
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)//, generator="item_seq")
	@Column
	private int id;

	@Column
	@NotNull(message="Name must not be empty")
	private String name;

	@Column
	private String description;
	
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
	
}
