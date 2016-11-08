package pl.essay.imangular.model;

import java.util.List;

import org.springframework.stereotype.Repository;

import pl.essay.generic.dao.GenericDaoHbnImpl;


@Repository
public class BillOfMaterialDaoImpl extends GenericDaoHbnImpl<BillOfMaterial> implements BillOfMaterialDao{

	@SuppressWarnings("unchecked")
	@Override
	public List<BomRequirementsQueryResult> getRequirementsOfBom(long id){
		/*
		 * BomRequirementsQueryResult
		public int forItemId;
		public String forItemName;
		
		public String forItemWhereManufactured; 
		public String forItemOtherSources, 
		
		public int requiredQuantity;
		public int effectiveRequiredQuantity;
		
		public int inStockQuantity;
		public long stockId;
		 */

		return (List<BomRequirementsQueryResult>) getSession()
				.createQuery(
						"select new pl.essay.imangular.model.BomRequirementsQueryResult("+
								"forItem.id, forItem.name, "+
								"forItem.whereManufactured, forItem.otherSources, "+
								"line.requiredQuantity, line.effectiveRequiredQuantity,"+
								"stock.inStockQuantity, stock.id )"+
								"from BillOfMaterialFlatListLine line "+
								"inner join line.forItem forItem "+
								"inner join line.bom bom "+
								"left outer join bom.stocks stock "+
								"where bom.id=:id and "+
								"( stock is null or stock.forItem.id = forItem.id) "+
						"order by line.forItem.name") 
				.setParameter("id", id)
				.list();
	}
}
