package pl.essay.imangular.service;


import java.util.List;

import pl.essay.generic.dao.SetWithCountHolder;
import pl.essay.imangular.model.BillOfMaterial;
import pl.essay.imangular.model.BillOfMaterialInStock;
import pl.essay.imangular.model.BomRequirementsQueryResult;

public interface BillOfMaterialService {

	public long addBom(BillOfMaterial bom);
	public void updateBom(BillOfMaterial bom);
	public SetWithCountHolder<BillOfMaterial> listBoms();
	public BillOfMaterial getBomById(long id);
	public void removeBom(long id);
	
	public void removeStockFromBom( long idstock);
	public void updateStockInBom(BillOfMaterialInStock stock);
	public void createStockInBom(BillOfMaterialInStock stock);
	
	public BillOfMaterial calculateBom(BillOfMaterial bom);
	public List<BomRequirementsQueryResult>  getBomRequirements(long id);
}
