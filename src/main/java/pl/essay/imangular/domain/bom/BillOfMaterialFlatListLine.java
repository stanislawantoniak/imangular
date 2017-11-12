package pl.essay.imangular.domain.bom;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.DecimalMin;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonBackReference;

import pl.essay.imangular.domain.item.Item;

@Entity
@DynamicInsert
@DynamicUpdate
// @Table(uniqueConstraints = {
// @UniqueConstraint(columnNames = {"bom","for_item"})}
// )

public class BillOfMaterialFlatListLine {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // , generator="item_seq")
	@Column
	private long id;

	@ManyToOne
	@Fetch(FetchMode.JOIN)
	@JoinColumn(referencedColumnName = "id", nullable = false)
	@JsonBackReference(value = "requirementsList")
	private BillOfMaterial bom;

	@ManyToOne
	@Fetch(FetchMode.JOIN)
	@JoinColumn(referencedColumnName = "id", nullable = false)
	private Item forItem;

	@OneToOne(cascade = { CascadeType.ALL })
	@JoinColumn(referencedColumnName = "id", nullable = true)
	private BillOfMaterialInStock stock;

	@Column
	// @NotNull
	@DecimalMin("0")
	private Long requiredQuantity;

	@Column
	// @NotNull
	@DecimalMin("0")
	private Long effectiveRequiredQuantity;

	public BillOfMaterialFlatListLine() {
	};

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public BillOfMaterial getBom() {
		return this.bom;
	}

	public void setBom(BillOfMaterial b) {
		this.bom = b;
	}

	public Item getForItem() {
		return this.forItem;
	}

	public void setForItem(Item item) {
		this.forItem = item;
	}

	public Long getRequiredQuantity() {
		return this.requiredQuantity;
	}

	public void setRequiredQuantity(Long q) {
		this.requiredQuantity = q;
	}

	public Long getEffectiveRequiredQuantity() {
		return this.effectiveRequiredQuantity;
	}

	public void setEffectiveRequiredQuantity(Long q) {
		this.effectiveRequiredQuantity = q;
	}

	public BillOfMaterialInStock getStock() {
		return this.stock;
	}

	public void setStock(BillOfMaterialInStock s) {
		this.stock = s;
	}

	@Override
	public String toString() {
		return "requirement :: (" + this.getId() + ", qtyRequired = " + this.requiredQuantity + ", effective ="
				+ this.effectiveRequiredQuantity + ")" + ", bom :: (" + this.bom + ")" + ", forItem :: (" + this.forItem
				+ ")";
	}

	public boolean equals(Object other) {
		if (this == other)
			return true;

		if (!(other instanceof BillOfMaterialFlatListLine))
			return false;

		final BillOfMaterialFlatListLine b2 = (BillOfMaterialFlatListLine) other;

		EqualsBuilder eb = new EqualsBuilder();

		eb.append(b2.getForItem(), this.getForItem());
		eb.append(b2.getBom(), this.getBom());

		return eb.isEquals();

	}

	@Override
	public int hashCode() { // todo - add extra fields like user/session when it is time
		HashCodeBuilder hcb = new HashCodeBuilder();
		hcb.append(this.getBom().hashCode());
		hcb.append(this.getForItem().hashCode());
		return hcb.toHashCode();
	}

}
