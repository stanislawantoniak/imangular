package pl.essay.imangular.domain.bom;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pl.essay.angular.security.UserSession;
import pl.essay.angular.security.UserT;
import pl.essay.generic.dao.SetWithCountHolder;
import pl.essay.generic.servicefacade.GenericServiceImpl;
import pl.essay.imangular.domain.item.Item;
import pl.essay.imangular.domain.item.ItemComponent;
import pl.essay.imangular.domain.item.ItemDao;

@Service
@Transactional
public class BillOfMaterialServiceImpl extends GenericServiceImpl<BillOfMaterial> implements BillOfMaterialService {
	
	protected static final Logger logger = LoggerFactory.getLogger(BillOfMaterialController.class);

	@Autowired 
	private BillOfMaterialDao bomDao;

	@Autowired 
	private BillOfMaterialInStockDao bomStockDao;

	@Autowired 
	private ItemDao itemDao;

	@Autowired
	private UserSession userSession;

	@Override
	public Serializable addEntity(BillOfMaterial bom) {

		//set both user and anonymous user 
		//if user is not logged in then null is placed in owner property - that is fine
		bom.setAnonymousUser(this.userSession.getAnonymousSessionId());
		bom.setUserOwner(this.userSession.getUser());

		this.bomDao.create(bom);
		this.bomDao.update(this.calculateBom(bom));
		return bom.getId();
	}

	@Override
	/*
	 * we update only required quantity
	 * 
	 * @see pl.essay.imangular.service.BillOfMaterialService#updateBom(pl.essay.imangular.model.BillOfMaterial)
	 */
	public void updateEntity(BillOfMaterial bom) {
		BillOfMaterial bomFromDb = this.bomDao.get(bom.getId());
		bomFromDb.setRequiredQuantity(bom.getRequiredQuantity());
		bom = this.calculateBom(bomFromDb);
		this.bomDao.update(bom);
	}

	@Override
	public SetWithCountHolder<BillOfMaterial> listBomsByAnonymousUser(String user) {
		return this.bomDao.getListByStrictPropertyMatch("anonymousOwner", user);
	}

	@Override
	public SetWithCountHolder<BillOfMaterial> listBomsByUser(UserT user) {
		return this.bomDao.getListByStrictPropertyMatch("userOwner", user);
	}

	@Override
	public void moveBomsFromAnonymousToUser(String anonymousSessionId, UserT user) {
		this.bomDao.moveBomsFromAnonymousToUser(anonymousSessionId, user);
	}

	@Override
	public BillOfMaterial getEntityById(Serializable id) {
		BillOfMaterial bom = this.bomDao.get(id);

		//load lazy loaded list while in transaction
		bom.getRequirementsList().size(); 
		bom.getStocks().size();

		return bom;
	}

	@Override
	public BillOfMaterial calculateBom(BillOfMaterial bom){

		//copy stocks entered by user to a handy map
		Map<Integer, BillOfMaterialInStock> stocksMap = new HashMap<Integer, BillOfMaterialInStock>();
		for (BillOfMaterialInStock stock: bom.getStocks()){
			stock.setConsumedStockQuantity(0); //reset consumed stock - it will be recalculated from scratch
			stocksMap.put( stock.getForItem().getId(), stock);
		}

		//create empty map fornew requirements list, it will be keyed by item id
		Map<Integer, BillOfMaterialFlatListLine> requirementsMap = new HashMap<Integer, BillOfMaterialFlatListLine>();
		
		Item bomItem = this.itemDao.get(bom.getForItem().getId()); //get item so all lazy loaded collection are available
		for (ItemComponent ic: bomItem.getComponents()){
			//System.out.println("starting recursion:: ");
			Item item = this.itemDao.get(ic.getComponent().getId()); //get item so all lazy loaded collection are available
			this.calculateRecursive(item, bom.getRequiredQuantity() * ic.getQuantity(), stocksMap,  requirementsMap); 
		}

		//old requirements will be removed
		logger.debug("old req list count::"+requirementsMap.size());
		bom.getRequirementsList().clear();

		//and replaced by new list from map just calculated
		//System.out.println("new req list count::"+requirementsMap.size());
		for (Map.Entry<Integer, BillOfMaterialFlatListLine> line : requirementsMap.entrySet()){
			logger.debug("requirement generated :: "+line.getValue());
			line.getValue().setBom(bom);
			bom.getRequirementsList().add(line.getValue());
		}

		return bom;
	}

	private void calculateRecursive(Item item, long requiredFromParent, Map<Integer, BillOfMaterialInStock>  stocks, Map<Integer, BillOfMaterialFlatListLine> requirementLines){

		logger.debug("calculating recursive for item component::"+item+", required from parent = "+requiredFromParent);

		BillOfMaterialFlatListLine requirementLine;

		BillOfMaterialInStock stock = stocks.get(item.getId());

		int freeStock = ( stock != null 
				? stock.getInStockQuantity() - stock.getConsumedStockQuantity() 
						:	0
				);

		//calculate required effective and subtract from free stock
		Long requiredEffective;
		if (freeStock >= requiredFromParent) { 	//some of stock will be used, but all required is fulfilled
			requiredEffective = 0L;
			freeStock -= requiredFromParent;
		} else { 							//whole remaining stock will be used
			requiredEffective = requiredFromParent - freeStock;
			freeStock = 0;
		}

		if (stock != null){
			stock.setConsumedStockQuantity(stock.getInStockQuantity() - freeStock);
		}

		logger.debug("    component:: "+item.getName()+" | requiredEffective:: "+requiredEffective+" | stockToConsume:: "+freeStock);

		//add required quantities from current component
		requirementLine = requirementLines.get(item.getId());
		if (requirementLine == null) {
			//System.out.println("create req::");
			requirementLine = new BillOfMaterialFlatListLine();
			requirementLine.setForItem(item);
			requirementLine.setEffectiveRequiredQuantity( 0L );
			requirementLine.setRequiredQuantity( 0L );
			requirementLine.setStock(stock);
			requirementLines.put(item.getId(), requirementLine);
		}

		requirementLine.setEffectiveRequiredQuantity( requirementLine.getEffectiveRequiredQuantity() + requiredEffective); 
		requirementLine.setRequiredQuantity(requirementLine.getRequiredQuantity()+requiredFromParent);

		if (stock == null ? true : !stock.getIgnoreRequirement())
			if (item.getIsComposed() && requirementLine.getEffectiveRequiredQuantity() > 0){
				for (ItemComponent component: item.getComponents()){
					logger.debug("==> starting recursion:: ");
					this.calculateRecursive( component.getComponent(), requiredEffective * component.getQuantity(), stocks,  requirementLines);
				}
			} 
	}

	@Override
	public void removeStockFromBom(long idstock) {

		//get stock from db
		BillOfMaterialInStock stockToDelete = this.bomStockDao.get(idstock);

		BillOfMaterial bom = stockToDelete.getBom();

		//remove from associated
		bom.getStocks().remove(stockToDelete);

		//finally recalculate and update bom 
		this.bomDao.update(this.calculateBom(bom));

	}

	@Override
	public void updateStockInBom(BillOfMaterialInStock stock) {

		BillOfMaterial bom = this.bomDao.get(stock.getBom().getId());

		stock.setBom(bom);
		stock.setForItem(this.itemDao.get(stock.getForItem().getId()));

		//remove stock from bom if exists and add new one
		bom.getStocks().remove(stock);
		bom.getStocks().add(stock);

		//finally recalculate and update bom 
		BillOfMaterial bom2 = this.calculateBom(bom);

		this.bomDao.update( bom2 );
	}

	@Override
	public void createStockInBom(BillOfMaterialInStock stock) {

		BillOfMaterial bom = this.bomDao.get(stock.getBom().getId());

		//set bom and forItem properties - stock comes from rest
		//so we do not have initialized object - only ids
		stock.setBom(bom);
		stock.setForItem(itemDao.get(stock.getForItem().getId()));
		bom.getStocks().add(stock);

		this.bomDao.update(this.calculateBom(bom));

	}
	@Override
	/*
	 * not used any more
	 * 
	 */
	public List<BomRequirementsQueryResult>  getBomRequirements(long id){

		BillOfMaterial bom = this.bomDao.get(id);
		Map<Integer, BillOfMaterialInStock> stocks = this.getMapBomInStockFromSet(bom.getStocks());

		List<BomRequirementsQueryResult> theList = new ArrayList<BomRequirementsQueryResult>();
		for (BillOfMaterialFlatListLine req : bom.getRequirementsList() ){

			/*
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
			Integer forItemId = req.getForItem().getId();
			BillOfMaterialInStock stock = stocks.get(forItemId);
			BomRequirementsQueryResult responseLine = new BomRequirementsQueryResult(
					req.getForItem().getId(),
					req.getForItem().getName(),
					req.getForItem().getIsComposed(),
					req.getForItem().getWhereManufactured(),
					req.getForItem().getOtherSources(),
					req.getRequiredQuantity(),
					req.getEffectiveRequiredQuantity(),
					(stock == null ? null : stock.getInStockQuantity()),
					(stock == null ? null : stock.getId()),
					(stock == null ? null : stock.getRemarks())
					);
			theList.add(responseLine);

		}

		return theList;
	}

	private Map<Integer, BillOfMaterialInStock> getMapBomInStockFromSet(Set<BillOfMaterialInStock> set){
		Map<Integer, BillOfMaterialInStock> map = new HashMap<Integer, BillOfMaterialInStock>();
		for (BillOfMaterialInStock stock: set){
			map.put(stock.getForItem().getId(), stock);
		}
		return map;
	}

}
