package pl.essay.imangular.domain.bom;

import java.util.List;

import pl.essay.angular.security.UserT;
import pl.essay.generic.dao.SetWithCountHolder;
import pl.essay.generic.servicefacade.GenericService;

public interface BillOfMaterialService extends GenericService<BillOfMaterial> {

	public void removeStockFromBom(long idstock);

	public void updateStockInBom(BillOfMaterialInStock stock);

	public void createStockInBom(BillOfMaterialInStock stock);

	public BillOfMaterial calculateBom(BillOfMaterial bom);

	public List<BomRequirementsQueryResult> getBomRequirements(long id);

	public SetWithCountHolder<BillOfMaterial> listBomsByAnonymousUser(String user);

	public SetWithCountHolder<BillOfMaterial> listBomsByUser(UserT user);

	public void moveBomsFromAnonymousToUser(String anonymous, UserT user);
}
