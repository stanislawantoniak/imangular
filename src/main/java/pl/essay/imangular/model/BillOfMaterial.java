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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@DynamicInsert
@DynamicUpdate

public class BillOfMaterial {
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)//, generator="item_seq")
	@Column
	private long id;

	@ManyToOne
	@Fetch(FetchMode.JOIN)
	@JoinColumn(referencedColumnName = "id", nullable = false)
	private Item forItem;	

	@OneToMany(orphanRemoval = true, fetch = FetchType.EAGER, mappedBy = "bom", cascade={CascadeType.ALL})
	@JsonManagedReference(value="stocks")
	private Set<BillOfMaterialInStock> stocks = new HashSet<BillOfMaterialInStock>();

	@OneToMany(orphanRemoval = true, fetch = FetchType.EAGER, mappedBy = "bom", cascade={CascadeType.ALL})
	@JsonManagedReference(value="requirementsList")
	private Set<BillOfMaterialFlatListLine> requirementsList = new HashSet<BillOfMaterialFlatListLine>();

	@Column
	@NotNull
	@DecimalMin("1")
	private int requiredQuantity;

	public BillOfMaterial(){};
	
	public BillOfMaterial(long id){ this.id = id;};

	public long getId(){
		return this.id;
	}

	public void setId(long id){
		this.id = id;
	}

	public Item getForItem(){
		return this.forItem;
	}

	public void setForItem(Item item){
		this.forItem = item;
	}

	public Set<BillOfMaterialInStock> getStocks(){
		return this.stocks;
	}

	public void setStocks(Set<BillOfMaterialInStock> s){
		this.stocks = s;
	}

	public Set<BillOfMaterialFlatListLine> getRequirementsList(){
		return this.requirementsList;
	}

	public void setRequirementsList(Set<BillOfMaterialFlatListLine> r){
		this.requirementsList = r;
	}
	public int getRequiredQuantity(){
		return this.requiredQuantity;
	}

	public void setRequiredQuantity(int q){
		this.requiredQuantity = q;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) return true;

		if ( !(other instanceof BillOfMaterial) ) return false;

		final BillOfMaterial b2 = (BillOfMaterial) other;

		EqualsBuilder eb = new EqualsBuilder();
		eb.append(b2.getForItem().getName(), this.getForItem().getName());

		return eb.isEquals();

	}
	@Override
	public int hashCode() { //todo - add extra fields like user/session when it is time
		HashCodeBuilder hcb = new HashCodeBuilder();
		hcb.append(this.getForItem().hashCode());
		return hcb.toHashCode();
	}
}
