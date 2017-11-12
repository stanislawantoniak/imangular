package pl.essay.imangular.domain.bom;

/*
 * not used any more, replaced by refactoring BillOfMaterialFlatListLine - added stock object in properties
 * 
 * object for passing requirement in list to edit bom
 * 
 */
public class BomRequirementsQueryResult {
	public int forItemId;
	public String forItemName;
	public Boolean forItemIsComposed;
	public String forItemWhereManufactured;
	public String forItemOtherSources;
	public Long requiredQuantity;
	public Long effectiveRequiredQuantity;
	public Integer inStockQuantity;
	public Long stockId;
	public String stockRemarks;

	public BomRequirementsQueryResult(int foritem, String foritemname, Boolean forItemIsComposed,
			String forItemWhereManufactured, String forItemOtherSources, Long req, Long efferq, Integer stock,
			Long stockId, String stockRemarks) {

		this.forItemId = foritem;
		this.forItemName = foritemname;
		this.forItemIsComposed = forItemIsComposed;
		this.forItemWhereManufactured = forItemWhereManufactured;
		this.forItemOtherSources = forItemOtherSources;
		this.requiredQuantity = req;
		this.effectiveRequiredQuantity = efferq;
		this.stockId = stockId;
		this.inStockQuantity = stock;
		this.stockRemarks = stockRemarks;

	}
}