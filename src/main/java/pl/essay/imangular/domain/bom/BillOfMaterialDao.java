package pl.essay.imangular.domain.bom;

import java.util.List;

import pl.essay.angular.security.UserT;
import pl.essay.generic.dao.GenericDaoHbn;

public interface BillOfMaterialDao extends GenericDaoHbn<BillOfMaterial>{

	public List<BomRequirementsQueryResult> getRequirementsOfBom(long id);
	
	public void moveBomsFromAnonymousToUser(String anonymous, UserT user);

}
