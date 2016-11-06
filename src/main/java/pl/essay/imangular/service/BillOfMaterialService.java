package pl.essay.imangular.service;


import pl.essay.generic.dao.SetWithCountHolder;
import pl.essay.imangular.model.BillOfMaterial;
import pl.essay.imangular.model.BillOfMaterialInStock;

public interface BillOfMaterialService {

	public long addBom(BillOfMaterial bom);
	public void updateBom(BillOfMaterial bom);
	public SetWithCountHolder<BillOfMaterial> listBoms();
	public BillOfMaterial getBomById(long id);
	public void removeBom(long id);
	
	public void removeStockFromBom(long idbom, long idstock);
	public void updateStockInBom(BillOfMaterialInStock stock);
	public void createStockInBom(BillOfMaterialInStock stock);
	
	public void calculateBom(BillOfMaterial bom);


	
}
