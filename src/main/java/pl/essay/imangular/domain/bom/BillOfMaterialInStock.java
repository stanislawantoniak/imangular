package pl.essay.imangular.domain.bom;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

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
public class BillOfMaterialInStock {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // , generator="item_seq")
	@Column
	private long id;

	@Column
	private Date dateCreated;

	@ManyToOne
	@Fetch(FetchMode.JOIN)
	@JoinColumn(referencedColumnName = "id", nullable = false)
	@JsonBackReference(value = "stocks")
	private BillOfMaterial bom;

	@ManyToOne
	@Fetch(FetchMode.JOIN)
	@JoinColumn(referencedColumnName = "id", nullable = false)
	private Item forItem;

	@Column
	// @NotNull
	private Boolean ignoreRequirement = false;

	@Column
	@NotNull
	@DecimalMin("0")
	private Integer inStockQuantity;

	@Column
	@NotNull
	@DecimalMin("0")
	private Integer consumedStockQuantity;

	@Column
	private String remarks;

	public BillOfMaterialInStock() {
	};

	public BillOfMaterialInStock(long id) {
		this.id = id;
	};

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getDateCreated() {
		return this.dateCreated;
	}

	public void setDateCreated(Date d) {
		this.dateCreated = d;
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

	public Boolean getIgnoreRequirement() {
		return this.ignoreRequirement;
	}

	public void setIgnoreRequirement(Boolean i) {
		this.ignoreRequirement = i;
	}

	public int getInStockQuantity() {
		return this.inStockQuantity;
	}

	public void setInStockQuantity(int q) {
		this.inStockQuantity = q;
	}

	public int getConsumedStockQuantity() {
		return this.consumedStockQuantity;
	}

	public void setConsumedStockQuantity(int q) {
		this.consumedStockQuantity = q;
	}

	public String getRemarks() {
		return this.remarks;
	};

	public void setRemarks(String w) {
		this.remarks = w;
	};

	@Override
	public String toString() {
		return "stock :: " + this.getId() + ", stock.bom :: " + this.bom.getId()
				+ (this.getForItem() != null ? ", stock.forItem :: " + this.getForItem() : "")
				+ (this.inStockQuantity != null ? ", stock.qty :: " + this.inStockQuantity : "")
				+ (this.consumedStockQuantity != null ? ", consumedStock.qty :: " + this.consumedStockQuantity : "")
				+ (this.remarks != null ? ", stock.remarks :: " + this.remarks : "");
	}

	public boolean equals(Object other) {
		if (this == other)
			return true;

		if (!(other instanceof BillOfMaterialInStock))
			return false;

		final BillOfMaterialInStock b2 = (BillOfMaterialInStock) other;

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
