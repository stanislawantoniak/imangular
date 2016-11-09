package pl.essay.imangular.model;

public class BomRequirementsQueryResult{
	public int forItemId;
	public String forItemName;
	public Boolean forItemIsComposed;
	public String forItemWhereManufactured; 
	public String forItemOtherSources;
	public Integer requiredQuantity;
	public Integer effectiveRequiredQuantity;
	public Integer inStockQuantity;
	public Long stockId;
	public String stockRemarks;
	
	public BomRequirementsQueryResult(
			int foritem, 
			String foritemname, 
			Boolean forItemIsComposed,
			String forItemWhereManufactured, 
			String forItemOtherSources, 
			Integer req,
			Integer efferq,
			Integer stock,
			Long stockId,
			String stockRemarks
			){
		
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