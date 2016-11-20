package pl.essay.imangular.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.springframework.test.context.TestPropertySource;

import pl.essay.angular.security.UserSession;
import pl.essay.imangular.domain.bom.BillOfMaterial;
import pl.essay.imangular.domain.bom.BillOfMaterialFlatListLine;
import pl.essay.imangular.domain.bom.BillOfMaterialInStock;
import pl.essay.imangular.domain.bom.BillOfMaterialInStockDao;
import pl.essay.imangular.domain.bom.BillOfMaterialServiceImpl;
import pl.essay.imangular.domain.item.ItemDao;

import org.mockito.runners.*;

import java.util.HashMap;
import java.util.Map;


@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations="classpath:test.properties")
public class BillOfMaterialServiceImplCalculateBomTests {

	@InjectMocks
	private BillOfMaterialServiceImpl service;
	
	@Mock 
	private BillOfMaterialInStockDao bomStockDao;

	@Mock 
	private ItemDao itemDao;
	
	@Mock
	private UserSession userSession;
	
	ItemTestData testData = new ItemTestData();
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	//b == 10 is more than required
	public void calculateRecursive_A_1_withStock_B_10() throws Exception{

		BillOfMaterial bom = new BillOfMaterial();
		bom.setForItem(testData.item("A")); //B has only 1 level structure
		bom.setRequiredQuantity(1);
		bom.setId(1000L);
		
		BillOfMaterialInStock stock = new BillOfMaterialInStock();
		stock.setBom(bom);
		stock.setInStockQuantity(10);
		stock.setForItem(testData.item("B"));
		stock.setId(1500L);
		bom.getStocks().add(stock);
				
		//int id = 0;
		when(itemDao.get(1)).thenReturn( testData.item(1) );
		when(itemDao.get(2)).thenReturn( testData.item(2) );
		when(itemDao.get(3)).thenReturn( testData.item(3) );
		when(itemDao.get(4)).thenReturn( testData.item(4) );
		when(itemDao.get(5)).thenReturn( testData.item(5) );
		when(itemDao.get(6)).thenReturn( testData.item(6) );
		when(itemDao.get(7)).thenReturn( testData.item(7) );
		when(itemDao.get(8)).thenReturn( testData.item(8) );
		

		bom = service.calculateBom(bom);
		
		printReq(bom);
		
		Map<String, BillOfMaterialFlatListLine> requirementsByName = reqMap(bom);
		/*
		 * Full A
		 * 		2 * B (0)	
		 * 				2 * D (0)
		 * 				3 * E (0)
		 * 		3 * C (3)
		 * 				2 * B (0)
		 * 						2 * D (0)
		 * 						3 * E (0)
		 * 				1 * F (3)
		 * 		1 * D (1)
		 * 
		 * flat list for A required quantity 1:
		 *  B = 0
		 *  C = 3
		 *  D = 1
		 *  E = null
		 * 	F = 3 
		 * */
		
		assertEquals("requirements list lenght", 4, bom.getRequirementsList().size());
		assertEquals("requirement B", 0, (long) requirementsByName.get("B").getEffectiveRequiredQuantity());
		assertEquals("requirement C", 3, (long) requirementsByName.get("C").getEffectiveRequiredQuantity());
		assertEquals("requirement D", 1, (long) requirementsByName.get("D").getEffectiveRequiredQuantity());
		assertEquals("requirement E not existing", false, requirementsByName.containsKey("E"));
		assertEquals("requirement F", 3, (long) requirementsByName.get("F").getEffectiveRequiredQuantity());

	}
	@Test
	//b == 5 is less than required
	public void calculateRecursive_A_1_withStock_B_5() throws Exception{

		BillOfMaterial bom = new BillOfMaterial();
		bom.setForItem(testData.item("A")); //B has only 1 level structure
		bom.setRequiredQuantity(1);
		bom.setId(1000L);
		
		BillOfMaterialInStock stock = new BillOfMaterialInStock();
		stock.setBom(bom);
		stock.setInStockQuantity(5);
		stock.setForItem(testData.item("B"));
		stock.setId(1500L);
		bom.getStocks().add(stock);
				
		//int id = 0;
		when(itemDao.get(1)).thenReturn( testData.item(1) );
		when(itemDao.get(2)).thenReturn( testData.item(2) );
		when(itemDao.get(3)).thenReturn( testData.item(3) );
		when(itemDao.get(4)).thenReturn( testData.item(4) );
		when(itemDao.get(5)).thenReturn( testData.item(5) );
		when(itemDao.get(6)).thenReturn( testData.item(6) );
		when(itemDao.get(7)).thenReturn( testData.item(7) );
		when(itemDao.get(8)).thenReturn( testData.item(8) );
		

		bom = service.calculateBom(bom);
		
		printReq(bom);
		
		Map<String, BillOfMaterialFlatListLine> requirementsByName = reqMap(bom);
		/*
		 * Full A
		 * 		2 * B (0)	
		 * 				2 * D (0)
		 * 				3 * E (0)
		 * 		3 * C (3)
		 * 				2 * B (3)
		 * 						2 * D (6)
		 * 						3 * E (9)
		 * 				1 * F (3)
		 * 		1 * D (1)
		 * 
		 * flat list for A required quantity 1:
		 *  B = 3
		 *  C = 3
		 *  D = 7
		 *  E = 9
		 * 	F = 3 
		 * */
		
		assertEquals("requirements list lenght", 5, bom.getRequirementsList().size());
		assertEquals("requirement B", 3, (long) requirementsByName.get("B").getEffectiveRequiredQuantity());
		assertEquals("requirement C", 3, (long) requirementsByName.get("C").getEffectiveRequiredQuantity());
		assertEquals("requirement D", 7, (long) requirementsByName.get("D").getEffectiveRequiredQuantity());
		assertEquals("requirement E", 9, (long) requirementsByName.get("E").getEffectiveRequiredQuantity());
		assertEquals("requirement F", 3, (long) requirementsByName.get("F").getEffectiveRequiredQuantity());

	}
	
	@Test
	//b == 10 is less than required
	public void calculateRecursive_A_2_withStock_B_10() throws Exception{

		BillOfMaterial bom = new BillOfMaterial();
		bom.setForItem(testData.item("A")); //B has only 1 level structure
		bom.setRequiredQuantity(2);
		bom.setId(1000L);
		
		BillOfMaterialInStock stock = new BillOfMaterialInStock();
		stock.setBom(bom);
		stock.setInStockQuantity(10);
		stock.setForItem(testData.item("B"));
		stock.setId(1500L);
		bom.getStocks().add(stock);
				
		//int id = 0;
		when(itemDao.get(1)).thenReturn( testData.item(1) );
		when(itemDao.get(2)).thenReturn( testData.item(2) );
		when(itemDao.get(3)).thenReturn( testData.item(3) );
		when(itemDao.get(4)).thenReturn( testData.item(4) );
		when(itemDao.get(5)).thenReturn( testData.item(5) );
		when(itemDao.get(6)).thenReturn( testData.item(6) );
		when(itemDao.get(7)).thenReturn( testData.item(7) );
		when(itemDao.get(8)).thenReturn( testData.item(8) );
		

		bom = service.calculateBom(bom);
		
		printReq(bom);
		
		Map<String, BillOfMaterialFlatListLine> requirementsByName = reqMap(bom);
		/*
		 * Full A (2)
		 * 		2 * B (0)	
		 * 				2 * D (0)
		 * 				3 * E (0)
		 * 		3 * C (6)
		 * 				2 * B (6)
		 * 						2 * D (12)
		 * 						3 * E (18)
		 * 				1 * F (6)
		 * 		1 * D (2)
		 * 
		 * flat list for A required quantity 1:
		 *  B = 6
		 *  C = 6
		 *  D = 14
		 *  E = 18
		 * 	F = 6 
		 * */
		
		assertEquals("requirements list lenght", 5, bom.getRequirementsList().size());
		assertEquals("requirement B", 6, (long) requirementsByName.get("B").getEffectiveRequiredQuantity());
		assertEquals("requirement C", 6, (long) requirementsByName.get("C").getEffectiveRequiredQuantity());
		assertEquals("requirement D", 14, (long) requirementsByName.get("D").getEffectiveRequiredQuantity());
		assertEquals("requirement E", 18, (long) requirementsByName.get("E").getEffectiveRequiredQuantity());
		assertEquals("requirement F", 6, (long) requirementsByName.get("F").getEffectiveRequiredQuantity());

	}
	
	@Test
	public void calculateRecursive_A_1_withStock_D_10() throws Exception{

		BillOfMaterial bom = new BillOfMaterial();
		bom.setForItem(testData.item("A")); //B has only 1 level structure
		bom.setRequiredQuantity(1);
		bom.setId(1000L);
		
		BillOfMaterialInStock stock = new BillOfMaterialInStock();
		stock.setBom(bom);
		stock.setInStockQuantity(10);
		stock.setForItem(testData.item("D"));
		stock.setId(1500L);
		bom.getStocks().add(stock);
				
		//int id = 0;
		when(itemDao.get(1)).thenReturn( testData.item(1) );
		when(itemDao.get(2)).thenReturn( testData.item(2) );
		when(itemDao.get(3)).thenReturn( testData.item(3) );
		when(itemDao.get(4)).thenReturn( testData.item(4) );
		when(itemDao.get(5)).thenReturn( testData.item(5) );
		when(itemDao.get(6)).thenReturn( testData.item(6) );
		when(itemDao.get(7)).thenReturn( testData.item(7) );
		when(itemDao.get(8)).thenReturn( testData.item(8) );
		

		bom = service.calculateBom(bom);
		Map<String, BillOfMaterialFlatListLine> requirementsByName = reqMap(bom);
				
		assertEquals("requirements list lenght", 5, bom.getRequirementsList().size());
		assertEquals("requirement B", 8, (long) requirementsByName.get("B").getEffectiveRequiredQuantity());
		assertEquals("requirement C", 3, (long) requirementsByName.get("C").getEffectiveRequiredQuantity());
		assertEquals("requirement D", 17-10, (long) requirementsByName.get("D").getEffectiveRequiredQuantity());
		assertEquals("requirement E", 24, (long) requirementsByName.get("E").getEffectiveRequiredQuantity());
		assertEquals("requirement F", 3, (long) requirementsByName.get("F").getEffectiveRequiredQuantity());

		//printReq(bom);

	}
	
	void printReq( BillOfMaterial  bom){
		for(BillOfMaterialFlatListLine line : bom.getRequirementsList())
			System.out.println(line.getForItem().getName()+" = "+line.getEffectiveRequiredQuantity());

	}
	
	@Test
	public void calculateBom_testA_qty_1() throws Exception{

		BillOfMaterial bom = new BillOfMaterial();
		bom.setForItem(testData.item("A"));
		bom.setRequiredQuantity(1);
		
		//int id = 0;
		when(itemDao.get(1)).thenReturn( testData.item(1) );
		when(itemDao.get(2)).thenReturn( testData.item(2) );
		when(itemDao.get(3)).thenReturn( testData.item(3) );
		when(itemDao.get(4)).thenReturn( testData.item(4) );
		when(itemDao.get(5)).thenReturn( testData.item(5) );
		when(itemDao.get(6)).thenReturn( testData.item(6) );
		when(itemDao.get(7)).thenReturn( testData.item(7) );
		when(itemDao.get(8)).thenReturn( testData.item(8) );

		bom = service.calculateBom(bom);
		Map<String, BillOfMaterialFlatListLine> requirementsByName = reqMap(bom);
				
		assertEquals("requirements list lenght", 5, bom.getRequirementsList().size());
		assertEquals("requirement B", 8, (long) requirementsByName.get("B").getEffectiveRequiredQuantity());
		assertEquals("requirement C", 3, (long) requirementsByName.get("C").getEffectiveRequiredQuantity());
		assertEquals("requirement D", 17, (long) requirementsByName.get("D").getEffectiveRequiredQuantity());
		assertEquals("requirement E", 24, (long) requirementsByName.get("E").getEffectiveRequiredQuantity());
		assertEquals("requirement F", 3, (long) requirementsByName.get("F").getEffectiveRequiredQuantity());
		
		assertEquals("requirement effective equals req B", requirementsByName.get("B").getRequiredQuantity(), requirementsByName.get("B").getEffectiveRequiredQuantity());
		assertEquals("requirement effective equals req C", requirementsByName.get("C").getEffectiveRequiredQuantity(), requirementsByName.get("C").getEffectiveRequiredQuantity());
		assertEquals("requirement effective equals req D", requirementsByName.get("D").getEffectiveRequiredQuantity(), requirementsByName.get("D").getEffectiveRequiredQuantity());
		assertEquals("requirement effective equals req E", requirementsByName.get("E").getEffectiveRequiredQuantity(), requirementsByName.get("E").getEffectiveRequiredQuantity());
		assertEquals("requirement effective equals req F", requirementsByName.get("F").getEffectiveRequiredQuantity(), requirementsByName.get("F").getEffectiveRequiredQuantity());
	
	}
	
	@Test
	public void calculateBom_testA_qty_3() throws Exception{

		BillOfMaterial bom = new BillOfMaterial();
		bom.setForItem(testData.item("A"));
		bom.setRequiredQuantity(3);
		
		//int id = 0;
		when(itemDao.get(1)).thenReturn( testData.item(1) );
		when(itemDao.get(2)).thenReturn( testData.item(2) );
		when(itemDao.get(3)).thenReturn( testData.item(3) );
		when(itemDao.get(4)).thenReturn( testData.item(4) );
		when(itemDao.get(5)).thenReturn( testData.item(5) );
		when(itemDao.get(6)).thenReturn( testData.item(6) );
		when(itemDao.get(7)).thenReturn( testData.item(7) );
		when(itemDao.get(8)).thenReturn( testData.item(8) );

		bom = service.calculateBom(bom);
		Map<String, BillOfMaterialFlatListLine> requirementsByName = reqMap(bom);
				
		assertEquals("requirements list lenght", 5, bom.getRequirementsList().size());
		assertEquals("requirement B", 8*3, (long) requirementsByName.get("B").getEffectiveRequiredQuantity());
		assertEquals("requirement C", 3*3, (long) requirementsByName.get("C").getEffectiveRequiredQuantity());
		assertEquals("requirement D", 17*3, (long) requirementsByName.get("D").getEffectiveRequiredQuantity());
		assertEquals("requirement E", 24*3, (long) requirementsByName.get("E").getEffectiveRequiredQuantity());
		assertEquals("requirement F", 3*3, (long) requirementsByName.get("F").getEffectiveRequiredQuantity());
		
		assertEquals("requirement effective equals req B", requirementsByName.get("B").getRequiredQuantity(), requirementsByName.get("B").getEffectiveRequiredQuantity());
		assertEquals("requirement effective equals req C", requirementsByName.get("C").getEffectiveRequiredQuantity(), requirementsByName.get("C").getEffectiveRequiredQuantity());
		assertEquals("requirement effective equals req D", requirementsByName.get("D").getEffectiveRequiredQuantity(), requirementsByName.get("D").getEffectiveRequiredQuantity());
		assertEquals("requirement effective equals req E", requirementsByName.get("E").getEffectiveRequiredQuantity(), requirementsByName.get("E").getEffectiveRequiredQuantity());
		assertEquals("requirement effective equals req F", requirementsByName.get("F").getEffectiveRequiredQuantity(), requirementsByName.get("F").getEffectiveRequiredQuantity());
	
	}
	
	Map<String, BillOfMaterialFlatListLine> reqMap(BillOfMaterial bom){
		Map<String, BillOfMaterialFlatListLine> map = new HashMap<String, BillOfMaterialFlatListLine>();
		for(BillOfMaterialFlatListLine line : bom.getRequirementsList())
			map.put(line.getForItem().getName(), line);
		return map;
	}
	
		
}
