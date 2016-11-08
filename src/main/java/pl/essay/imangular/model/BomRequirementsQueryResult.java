package pl.essay.imangular.model;

public class BomRequirementsQueryResult{
	public int forItemId;
	public String forItemName;
	public String forItemWhereManufactured; 
	public String forItemOtherSources;
	public Integer requiredQuantity;
	public Integer effectiveRequiredQuantity;
	public Integer inStockQuantity;
	public Long stockId;
	
	public BomRequirementsQueryResult(
			int foritem, 
			String foritemname, 
			String forItemWhereManufactured, 
			String forItemOtherSources, 
			Integer req,
			Integer efferq,
			Integer stock,
			Long stockId
			){
		
		this.forItemId = foritem;
		this.forItemName = foritemname; 
		this.forItemWhereManufactured = forItemWhereManufactured;
		this.forItemOtherSources = forItemOtherSources;
		this.requiredQuantity = req;
		this.effectiveRequiredQuantity = efferq;
		this.stockId = stockId;
		this.inStockQuantity = stock;
		
	}
}