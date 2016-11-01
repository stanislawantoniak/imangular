package pl.essay.imangular.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.*;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;
import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@DynamicInsert
@DynamicUpdate
@Table(uniqueConstraints = {
		@UniqueConstraint(columnNames = "name")}
		)
@NamedQueries({
	@NamedQuery(
			name = "getItemByName",
			query = "select i from Item i where name = :nameParam"
			),
	@NamedQuery(
			name = "getAllItems",
			query = "select i.id, i.name, i.isComposed from Item i order by i.name"
			)
})
//@SequenceGenerator(name="item_seq", initialValue=1, allocationSize=100)
public class Item {
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)//, generator="item_seq")
	@Column
	private int id;

	@Column
	@NotNull(message="Name must not be empty")
	@Size(min=3, message="Name must be at least 3 characters long")
	private String name;

	@Column @Type(type="yes_no")
	private Boolean isComposed = false;

	@OneToMany(orphanRemoval = true, fetch = FetchType.EAGER, mappedBy = "parent", cascade={CascadeType.ALL})
	@JsonManagedReference(value="component")
	private Set<ItemComponent> components = new HashSet<ItemComponent>();

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "component")
	@JsonManagedReference(value="usedIn")
	private Set<ItemComponent> usedIn = new HashSet<ItemComponent>();

	public Item(){
	}

	public Item(int id){
		this.id = id;
	}

	public Item(String name, Boolean composed_TF){
		this.setName(name);
		this.setIsComposed(composed_TF);
	}

	//setters & getters
	public void setId(int id){
		this.id = id;
	}
	public void setName(String name){
		this.name = name;
	}
	public void setIsComposed(Boolean composed_TF){
		this.isComposed = composed_TF;
	}
	public boolean getIsComposed(){
		return this.isComposed;
	}
	public int getId(){
		return this.id;
	}
	public String getName(){
		return this.name;
	}

	public Set<ItemComponent> getComponents() {
		return this.components;
	}

	public void addComponent(ItemComponent ic){
		this.removeComponent(ic.getId());			
		this.components.add(ic);
		ic.setParent(this);
		this.isComposed = true;
		ic.setParent(this);
	}

	public void removeComponent(int icId){
		System.out.println("components size (1): "+components.size());
		ItemComponent icToRemove = null;
		for (ItemComponent ic : components){
			if (icId == ic.getId()){
				icToRemove = ic;
				System.out.println("ic: "+icId+" current comp:"+ic);
				break;
			}
		}
		System.out.println("to remove "+icToRemove);
		components.remove(icToRemove);
		System.out.println("components size (2): "+components.size());
		if (components.size() == 0)
			this.isComposed = false;
	}

	public void setComponents(Set<ItemComponent> ic) {
		this.components = ic;
	}

	public Set<ItemComponent> getUsedIn() {
		return this.usedIn;
	}

	public void setUsedIn(Set<ItemComponent> ic) {
		this.usedIn = ic;
	}

	public String toString(){
		return this.getId() + ":: name : "+this.getName()+":: is composed : "+this.isComposed;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) return true;

		if ( !(other instanceof Item) ) return false;

		final Item b2 = (Item) other;

		EqualsBuilder eb = new EqualsBuilder();
		eb.append(b2.getName(), this.getName());

		return eb.isEquals();
	}

	@Override
	public int hashCode() {
		HashCodeBuilder hcb = new HashCodeBuilder();
		hcb.append(this.getName());
		return hcb.toHashCode();
	}

}
