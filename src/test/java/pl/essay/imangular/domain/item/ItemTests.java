package pl.essay.imangular.domain.item;

import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

import org.springframework.test.context.TestPropertySource;
import pl.essay.imangular.domain.ItemTestData;
import org.mockito.runners.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:test.properties")
public class ItemTests {

	@Test
	/*
	 * test 1. if is used is set correctly in addComponent and removeComponent in
	 * Item class 2. whether us used list contains correct item_components
	 */
	public void test_is_used() throws Exception {

		/*
		 * A = can be split = true 2 * B 3 * C 1 * D B - can be split = false 2 * D 3 *
		 * E C - can be split = true 2 * B 1 * F
		 * 
		 * B,C,D - can be recovered from A B,F - can be recovered from C E - cannot be
		 * recovered
		 * 
		 */

		ItemTestData testData = new ItemTestData();

		assertEquals("item A can be split", true, testData.item("A").getCanBeSplit());
		assertEquals("item B can be split", false, testData.item("B").getCanBeSplit());
		assertEquals("item C can be split", true, testData.item("C").getCanBeSplit());

		assertFalse("A can be recovered", testData.item("A").getIsUsed());
		assertTrue("B can be recovered", testData.item("B").getIsUsed());
		assertTrue("C can be recovered", testData.item("C").getIsUsed());
		assertTrue("D can be recovered", testData.item("D").getIsUsed());
		assertFalse("E can be recovered", testData.item("E").getIsUsed());
		assertTrue("F can be recovered", testData.item("F").getIsUsed());

		assertEquals("A isUsedList empty", 0, testData.item("A").getUsedIn().size());
		assertEquals("B isUsedList size 2", 2, testData.item("B").getUsedIn().size());
		assertEquals("C isUsedList size 1", 1, testData.item("C").getUsedIn().size());
		assertEquals("D isUsedList size 2", 2, testData.item("D").getUsedIn().size());
		assertEquals("E isUsedList size 1", 1, testData.item("E").getUsedIn().size());
		assertEquals("F isUsedList size 1", 1, testData.item("F").getUsedIn().size());

		String usedIn = "";
		for (ItemComponent ic : testData.item("B").getUsedIn())
			usedIn += ic.getParent();

		assertTrue("B is used contains A", usedIn.contains("A"));
		assertTrue("B is used contains C", usedIn.contains("C"));

		usedIn = "";
		for (ItemComponent ic : testData.item("C").getUsedIn())
			usedIn += ic.getParent();
		assertTrue("C is used contains A", usedIn.contains("A"));

	}

	@Test
	/*
	 * test 1. if is used is set correctly in addComponent and removeComponent in
	 * Item class 2. whether us used list contains correct item_components
	 */
	public void test_removing_last_used_in_component() throws Exception {

		/*
		 * A = can be split = true 2 * B 3 * C 1 * D B - can be split = false 2 * D 3 *
		 * E C - can be split = true 2 * B 1 * F
		 * 
		 * B,C,D - can be recovered from A B,F - can be recovered from C E - cannot be
		 * recovered
		 */
		ItemTestData testData = new ItemTestData();

		ItemComponent forFinC = null;
		for (ItemComponent ic : testData.item("C").getComponents())
			if ("F".equals(ic.getComponent().getName())) {
				forFinC = ic;
				break;
			}

		assertEquals("for F in C is 'F'", "F", forFinC.getComponent().getName());
		testData.item("C").removeComponent(forFinC);
		assertEquals("F isUsedList size 0", 0, testData.item("F").getUsedIn().size());
		assertFalse("F can be recovered", testData.item("F").getIsUsed());

	}

}
