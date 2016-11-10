package pl.essay.imangular.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pl.essay.generic.dao.SetWithCountHolder;
import pl.essay.imangular.model.BillOfMaterial;
import pl.essay.imangular.model.BillOfMaterialDao;
import pl.essay.imangular.model.BillOfMaterialFlatListLine;
import pl.essay.imangular.model.BillOfMaterialInStock;
import pl.essay.imangular.model.BillOfMaterialInStockDao;
import pl.essay.imangular.model.BomRequirementsQueryResult;
import pl.essay.imangular.model.Item;
import pl.essay.imangular.model.ItemComponent;
import pl.essay.imangular.model.ItemDao;

@Service
@Transactional
public class BillOfMaterialServiceImpl implements BillOfMaterialService {

	@Autowired 
	private BillOfMaterialDao bomDao;
	
	@Autowired 
	private BillOfMaterialInStockDao bomStockDao;
	
	@Autowired 
	private ItemDao itemDao;

	@Override
	public long addBom(BillOfMaterial bom) {
		this.bomDao.create(bom);
		this.bomDao.update(this.calculateBom(bom));
		return bom.getId();
	}

	@Override
	public void updateBom(BillOfMaterial bom) {
		//to fix shoul be recalculated
		this.bomDao.update(bom);
	}

	@Override
	public SetWithCountHolder<BillOfMaterial> listBoms() {
		return this.bomDao.getAll();
	}

	@Override
	public BillOfMaterial getBomById(long id) {
		BillOfMaterial bom = this.bomDao.get(id);
		
		//load lazy loaded list while in transaction
		bom.getRequirementsList().size(); 
		bom.getStocks().size();
		
		return bom;
	}

	@Override
	public void removeBom(long id) {
		this.bomDao.deleteById(id);;
	}

	@Override
	public BillOfMaterial calculateBom(BillOfMaterial bom){
		//BillOfMaterial bom = this.bomDao.get(b.getId());

		//copy stocks entered by user to handy map
		Map<Long, BillOfMaterialInStock> stocksMap = new HashMap<Long, BillOfMaterialInStock>();
		for (BillOfMaterialInStock stock: bom.getStocks()){
			stock.setConsumedStockQuantity(0); //reset consumed stock - it will be recalculated from scratch
			stocksMap.put((long) stock.getForItem().getId(),stock);
		}
		
		//create empty map fornew requirements list, it will be keyed by item id
		Map<Long, BillOfMaterialFlatListLine> bomMap = new HashMap<Long, BillOfMaterialFlatListLine>();

		System.out.println("calculate bom:: "+bom.getForItem().getName());
		
		for (ItemComponent ic: bom.getForItem().getComponents()){
			this.calculateRecursive(ic, bom, 1, stocksMap,  bomMap); //1 as required from parent because it is zero level - it is always multipliet by bom quantity anyway
		}
		
		//old requirements will be removed
		bom.getRequirementsList().clear();
		
		//and replaced by new list from map just calculated
		System.out.println("req count::"+bomMap.size());
		for (Map.Entry<Long, BillOfMaterialFlatListLine> line : bomMap.entrySet()){
			System.out.println("requirement generated :: "+line.getValue());
			bom.getRequirementsList().add(line.getValue());
		}
		
		return bom;
	}

	private void calculateRecursive(ItemComponent ic, BillOfMaterial bom, int requiredFromParent, Map<Long, BillOfMaterialInStock>  stocks, Map<Long, BillOfMaterialFlatListLine> lines){
		
		Long itemId = (long) ic.getComponent().getId(); 
		Item item = this.itemDao.get(ic.getComponent().getId());
		
		BillOfMaterialFlatListLine line;
		
		if (lines.containsKey( itemId ))
			line = lines.get(itemId);
		else {
			line = new BillOfMaterialFlatListLine();
			line.setForItem(ic.getComponent());
			line.setEffectiveRequiredQuantity(0);
			line.setBom(bom);
			lines.put(itemId, line);
		}

		int stockToConsume = 0;
		int required = ic.getQuantity() * bom.getRequiredQuantity() * requiredFromParent;
		int requiredEffective;

		BillOfMaterialInStock stock = null;
		if (stocks.containsKey(itemId)){
			stock = stocks.get(itemId);
			stockToConsume = stock.getInStockQuantity() - stock.getConsumedStockQuantity();
		}

		if (stockToConsume == 0){ //no stock to consume, all required is required effectively
			requiredEffective = required;
		} else {
			if (stockToConsume >= required) { //some of stock will be used, but all required is fulfilled
				requiredEffective = 0;
				stockToConsume -= required;
			} else { //whole remaining stock will be used
				requiredEffective = required - stockToConsume;
				stockToConsume = 0;
			}
		}

		if (stock != null){
			stock.setConsumedStockQuantity(stock.getInStockQuantity() - stockToConsume);
		}
		
		System.out.println("    component:: "+item.getName()+" | requiredEffective:: "+requiredEffective+" | stockToConsume:: "+stockToConsume);

		line.setEffectiveRequiredQuantity( line.getEffectiveRequiredQuantity() + requiredEffective);

		if (item.getIsComposed()){
			for (ItemComponent component: item.getComponents()){
				this.calculateRecursive(component, bom, ic.getQuantity(), stocks,  lines);
			}
		} else { //just add requirement to lines
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
		System.out.println("req size 1::"+bom.getRequirementsList().size());
		
		BillOfMaterial bom2 = this.calculateBom(bom);
		
		System.out.println("req size 2::"+bom2.getRequirementsList().size());
		
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
	public List<BomRequirementsQueryResult>  getBomRequirements(long id){
		return this.bomDao.getRequirementsOfBom(id);
	}

}
