package pl.essay.imangular.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pl.essay.generic.dao.SetWithCountHolder;
import pl.essay.imangular.model.BillOfMaterial;
import pl.essay.imangular.model.BillOfMaterialDao;
import pl.essay.imangular.model.BillOfMaterialFlatListLine;
import pl.essay.imangular.model.BillOfMaterialInStock;
import pl.essay.imangular.model.ItemComponent;
import pl.essay.imangular.model.ItemDao;

@Service
@Transactional
public class BillOfMaterialServiceImpl implements BillOfMaterialService {

	@Autowired 
	private BillOfMaterialDao bomDao;
	
	@Autowired 
	private ItemDao itemDao;

	@Override
	public long addBom(BillOfMaterial bom) {
		this.bomDao.create(bom);
		return bom.getId();
	}

	@Override
	public void updateBom(BillOfMaterial bom) {
		this.bomDao.update(bom);
	}

	@Override
	public SetWithCountHolder<BillOfMaterial> listBoms() {
		return this.bomDao.getAll();
	}

	@Override
	public BillOfMaterial getBomById(long id) {
		BillOfMaterial bom = this.bomDao.get(id);
		return bom;
	}

	@Override
	public void removeBom(long id) {
		this.bomDao.deleteById(id);;
	}

	@Override
	public void calculateBom(BillOfMaterial b){
		BillOfMaterial bom = this.bomDao.get(b.getId());

		//copy stocks entered by user to handy map
		Map<Long, BillOfMaterialInStock> stocksMap = new HashMap<Long, BillOfMaterialInStock>();
		for (BillOfMaterialInStock stock: bom.getStocks()){
			stock.setConsumedStockQuantity(0); //reset consumed stock - it will be recalculated from scratch
			stocksMap.put((long) stock.getForItem().getId(),stock);
		}
		
		//create empty map fornew requirements list, it will be keyed by item id
		Map<Long, BillOfMaterialFlatListLine> bomMap = new HashMap<Long, BillOfMaterialFlatListLine>();

		System.out.println("calculate bom:: "+b.getForItem().getName());
		for (ItemComponent ic: bom.getForItem().getComponents()){
			this.calculateRecursive(ic, bom, 1, stocksMap,  bomMap); //1 as required from parent because it is zero level - it is always multipliet by bom quantity anyway
		}
		
		//old requirements will be removed
		bom.getRequirementsList().removeAll(bom.getRequirementsList());
		//and replaced by new list from map just calculated
		for (Map.Entry<Long, BillOfMaterialFlatListLine> line : bomMap.entrySet()){
			bom.getRequirementsList().add(line.getValue());
		}
		
		this.bomDao.update(bom);
	}

	private void calculateRecursive(ItemComponent ic, BillOfMaterial bom, int requiredFromParent, Map<Long, BillOfMaterialInStock>  stocks, Map<Long, BillOfMaterialFlatListLine> lines){
		
		Long itemId = (long) ic.getComponent().getId(); 
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
		
		System.out.println("    component:: "+ic.getComponentName()+" | requiredEffective:: "+requiredEffective+" | stockToConsume:: "+stockToConsume);

		line.setEffectiveRequiredQuantity( line.getEffectiveRequiredQuantity() + requiredEffective);
		
		if (ic.getComponent().getIsComposed()){
			for (ItemComponent component: ic.getComponent().getComponents()){
				this.calculateRecursive(component, bom, ic.getQuantity(), stocks,  lines);
			}
		} else { //just add requirement to lines
		}
	}

	@Override
	public void removeStockFromBom(long idbom, long idstock) {
		BillOfMaterial bom = this.bomDao.get(idbom);
		
		BillOfMaterialInStock stockToDelete = null;
		for (BillOfMaterialInStock s: bom.getStocks()){
			if (s.getForItem().getId() == idstock){
				stockToDelete = s;
				break;
			}
		}
		bom.getStocks().remove(stockToDelete);
		
		this.bomDao.update(bom);
		
	}

	@Override
	public void updateStockInBom(BillOfMaterialInStock stock) {
		BillOfMaterial bom = this.bomDao.get(stock.getBom().getId());
		
		BillOfMaterialInStock stockToUpdate = null;
		for (BillOfMaterialInStock s: bom.getStocks()){
			if (s.getForItem().getId() == stock.getForItem().getId()){
				s.setInStockQuantity(stock.getInStockQuantity()); //update quantity - that isthe onlu thing that can change
				break;
			}
		}
		this.bomDao.update(bom);
	}

	@Override
	public void createStockInBom(BillOfMaterialInStock stock) {
		BillOfMaterial bom = this.bomDao.get(stock.getBom().getId());
		
		stock.setBom(bom);
		stock.setForItem(itemDao.get(stock.getId()));
		bom.getStocks().add(stock);
		
		this.bomDao.update(bom);
		
	}

}
