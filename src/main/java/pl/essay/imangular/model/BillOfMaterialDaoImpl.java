package pl.essay.imangular.model;

import java.util.List;

import org.springframework.stereotype.Repository;

import pl.essay.angular.security.UserT;
import pl.essay.generic.dao.CriteriaBuilder;
import pl.essay.generic.dao.GenericDaoHbnImpl;
import pl.essay.generic.dao.SetWithCountHolder;


@Repository
public class BillOfMaterialDaoImpl extends GenericDaoHbnImpl<BillOfMaterial> implements BillOfMaterialDao{

	public void moveBomsFromAnonymousToUser(String anonymous, UserT user){

		System.out.println("user::"+user);

		this.getSession()
		.createQuery(
				"update BillOfMaterial bom "+
						"set bom.userOwner = :userOwner "+
						"where bom.anonymousOwner = :anonymousOwner"
				)
		.setParameter("anonymousOwner", anonymous)
		.setParameter("userOwner", user)
		.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<BomRequirementsQueryResult> getRequirementsOfBom(long id){
		/*
		 * 
		pl.essay.imangular.model.BomRequirementsQueryResult - fields for reference
		public int forItemId;
		public String forItemName;
		public Boolean forItemIsComposed;

		public String forItemWhereManufactured; 
		public String forItemOtherSources, 

		public int requiredQuantity;
		public int effectiveRequiredQuantity;

		public int inStockQuantity;
		public long stockId;

		public String stockRemarks;
		 */

		return (List<BomRequirementsQueryResult>) getSession()
				.createQuery(
						"select "+
								"new pl.essay.imangular.model.BomRequirementsQueryResult("+
								"line.forItem.id, line.forItem.name, line.forItem.isComposed, "+
								"line.forItem.whereManufactured, line.forItem.otherSources, "+
								"line.requiredQuantity, line.effectiveRequiredQuantity,"+
								"stock.inStockQuantity, stock.id, "+
								"stock.remarks )"+
								"from BillOfMaterialFlatListLine line "+
								"left outer join line.bom.stocks stock "+
								"where line.bom.id=:id "+
								" and ( stock is null or stock.forItem.id = line.forItem.id) "+
								"order by "+
								"line.forItem.isComposed desc, "+
						"line.forItem.name") 
				.setParameter("id", id)
				.list();
	}
}
