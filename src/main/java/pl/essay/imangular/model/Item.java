package pl.essay.imangular.model;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

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

import com.fasterxml.jackson.annotation.JsonIgnore;

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

	@OneToMany(orphanRemoval = true, fetch = FetchType.LAZY, mappedBy = "parent", cascade={CascadeType.ALL})
	//@JsonManagedReference(value="component")
	@JsonIgnore
	private Set<ItemComponent> components = new HashSet<ItemComponent>();

	@Column @Type(type="yes_no")
	private Boolean isUsed = false;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "component")
	//@JsonManagedReference(value="usedIn")
	@JsonIgnore 
	private Set<ItemComponent> usedIn = new HashSet<ItemComponent>();

	@Column @Type(type="yes_no")
	private Boolean isBuilding;

	@Column 
	private String whereManufactured;

	@Column @Type(type="yes_no")
	private Boolean isAvailableInOtherSources = false;

	@Column 
	private String otherSources;

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

	public void setIsUsed(Boolean used_TF){
		this.isUsed = used_TF;
	}
	public boolean getIsUsed(){
		return this.isUsed;
	}

	public int getId(){
		return this.id;
	}
	public String getName(){
		return this.name;
	}
	/*
	 * return components organized in a TreeSet to force sorting
	 * there will be few components so sorting in this layer will be efficient
	 * 
	 * sorting is by remarks (first non empty asc, then empty) and then by name asc 
	 */
	public Set<ItemComponent> getComponents() {
		Set<ItemComponent> sortedComponents = new TreeSet<ItemComponent>(
				new Comparator<ItemComponent>(){
					@Override 
					public int compare(ItemComponent i1, ItemComponent i2){

						//convert nulls in remarks to ""
						String remarks1 = (i1.getRemarks() == null ? "" :i1.getRemarks().toLowerCase() );
						String remarks2 = (i2.getRemarks() == null ? "" :i2.getRemarks().toLowerCase() );
						
						if (remarks1.equals(remarks2)){
							
							return i1.getComponentName().toLowerCase().compareTo(i2.getComponentName().toLowerCase());
						
						} else {
							
							if (!"".equals(remarks1) && "".equals(remarks2))
								return -1;
							if ("".equals(remarks1) && !"".equals(remarks2))
								return 1;
							else 
								return remarks1.compareTo(remarks2);
						
						}
					}
				});
		sortedComponents.addAll(this.components);
		return sortedComponents;
	}

	public void addComponent(ItemComponent ic){
		this.components.remove(ic);			
		this.components.add(ic);
		this.isComposed = true;
		ic.getComponent().setIsUsed(true);
		ic.setParent(this);
	}

	public void removeComponent(ItemComponent ic){
		this.components.remove(ic);			
		this.isComposed = (this.components.size() == 0 ? false : true );
		ic.getComponent().setIsUsed( (ic.getComponent().getUsedIn().size() == 0 ? false : true ) );
		ic.setParent(this);
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

	public Boolean getIsBuilding(){
		return this.isBuilding;
	}
	public void setIsBuilding(Boolean b){
		this.isBuilding = b;
	}

	public String getWhereManufactured(){
		return this.whereManufactured;
	};
	public void setWhereManufactured(String w){
		this.whereManufactured = w;
	};

	public Boolean getIsAvailableInOtherSources(){
		return this.isAvailableInOtherSources;
	}
	public void setIsAvailableInOtherSources(Boolean b){
		this.isAvailableInOtherSources = b;
	}

	public String getOtherSources(){
		return this.otherSources;
	}
	public void setOtherSources(String o){
		this.otherSources = o;
	}

	public String toString(){
		return this.getId() + ":: name : "+this.getName()+":: is composed : "+this.isComposed;
	}

	@JsonIgnore
	public String getDafaulSortColumn(){
		return "name";
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) return true;

		if ( !(other instanceof Item) ) return false;

		final Item b2 = (Item) other;

		EqualsBuilder eb = new EqualsBuilder();
		eb.append(b2.name, this.name);

		return eb.isEquals();
	}

	@Override
	public int hashCode() {
		HashCodeBuilder hcb = new HashCodeBuilder();
		hcb.append(this.name);
		return hcb.toHashCode();
	}

}
