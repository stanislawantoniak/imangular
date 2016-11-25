package pl.essay.imangular.domain.item;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.validation.constraints.*;

@Entity
@Table
@NamedQueries({
	@NamedQuery(
			name = "getComponentsByParent",
			query = "select ic from ItemComponent ic where parent.id = :id"
			),
	@NamedQuery(
			name = "getComponentsByUsedIn",
			query = "select ic from ItemComponent ic where component.id = :id"
			)
}		)
//@SequenceGenerator(name="itemcomponent_seq", initialValue=1, allocationSize=100)
public class ItemComponent {

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)//, generator="itemcomponent_seq")
	@Column
	private int id;

	@Column
	private Date dateCreated;

	@ManyToOne//(fetch = FetchType.EAGER)
	@Fetch(FetchMode.JOIN)
	@JoinColumn(referencedColumnName = "id", nullable = false)
	//@JsonBackReference(value="component")
	private Item parent;

	@ManyToOne//(fetch = FetchType.EAGER)
	@Fetch(FetchMode.JOIN)
	@NotNull(message="Component must not be empty")
	@JoinColumn(referencedColumnName = "id", nullable = false)
	//@JsonBackReference(value="usedIn")
	private Item component;

	@Column
	@NotNull
	@DecimalMin("1")
	private int quantity;

	@Column
	private String remarks = "";

	public ItemComponent(){
	}

	public ItemComponent(Item parent){
		this.parent = parent;
	}

	//setters & getters
	public Item getParent(){
		return this.parent;
	}
	public void setParent(Item i){
		this.parent = i;
	}	
	public void setId(int i){
		this.id = i;
	}
	public int getId(){
		return this.id;
	}

	public void setQuantity(int q){
		this.quantity = q;
	}
	public int getQuantity(){
		return this.quantity;
	}
	public void setRemarks(String r){
		this.remarks = r;
	}
	public String getRemarks(){
		return this.remarks;
	}
	public void setComponent(Item q){
		this.component = q;
	}
	public Item getComponent(){
		return this.component;
	}
	
	public Date getDateCreated(){
		return this.dateCreated;
	}
	
	public void setDateCreated(Date d){
		this.dateCreated = d;
	}
	
	public String getComponentName(){
		return (this.component != null ? this.component.getName() : "");
	}
	public int getComponentId(){
		return (this.component != null ? this.component.getId() : 0 );
	}
	public String toString(){
		return "component id: "+this.getId()+" parent :: "+this.getParent().getId()+ ":: item: #"+this.getComponentId()+" "+this.getComponentName()+":: qty: "+this.getQuantity();
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other) return true;

		if ( !(other instanceof ItemComponent) ) return false;

		final ItemComponent otherIC = (ItemComponent) other;

		EqualsBuilder eb = new EqualsBuilder();
		eb.append(otherIC.parent.getName(), this.parent.getName());
		eb.append(otherIC.component.getName(), this.component.getName());
		
		return eb.isEquals();
	}

	@Override
	public int hashCode() {
		HashCodeBuilder hcb = new HashCodeBuilder();
		hcb.append(this.parent.getName());
		hcb.append(this.component.getName());
		return hcb.toHashCode();
	}
}
