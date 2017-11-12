package pl.essay.imports;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.*;

public class XLSProductsImporter {

	private InputStream getIS(String filename) {
		return getClass().getClassLoader().getResourceAsStream(filename);
	}

	private Map<String, Product> products = new HashMap<String, Product>();

	public Map<String, Product> getProducts() {
		return this.products;
	}

	public XLSProductsImporter() {
	}

	public void importFile(String filename) {
		try {
			InputStream file = getIS(filename);

			XSSFWorkbook workbook = new XSSFWorkbook(file);

			XSSFSheet sheet = workbook.getSheetAt(0);

			Iterator<Row> rowIterator = sheet.iterator();
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				if (row.getRowNum() != 0) {
					Product product = new Product();
					product.id = row.getCell(0).getStringCellValue().trim();
					product.name = row.getCell(1).getStringCellValue().trim();

					// System.out.println(product.name);
					product.isComposed = ("N".equals(row.getCell(2).getStringCellValue()) ? true : false);
					product.isBuilding = ("T".equals(this.getCellAtIndexAsString(3, row)) ? true : false);
					product.whereManufactured = this.getCellAtIndexAsString(4, row);
					product.otherSources = ("T".equals(this.getCellAtIndexAsString(5, row)) ? true : false);
					product.otherSourceName = this.getCellAtIndexAsString(6, row);

					// System.out.print(product);

					products.put(product.id, product);
				}
			}

			sheet = workbook.getSheetAt(1);

			int i = 0;

			rowIterator = sheet.iterator();
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();

				if (row.getRowNum() != 0) {

					String productId = row.getCell(0).getStringCellValue().trim();

					Product product = products.get(productId);

					if (product != null) {

						Product.Component component = product.new Component();

						component.pid = productId;

						component.product = row.getCell(1).getStringCellValue().trim();
						component.cid = this.getCellAtIndexAsString(2, row);
						component.component = this.getCellAtIndexAsString(3, row);
						component.quantity = Integer
								.valueOf(String.format("%.0f", row.getCell(4).getNumericCellValue()));
						component.desc = this.getCellAtIndexAsString(5, row);

						product.components.put(component.cid, component);

						// System.out.println(component);
					} else {
						i++;

						System.out.println("#" + i + "  " + productId + " not found");
					}
				}
			}

			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	String getCellAtIndexAsString(int i, Row row) {
		Cell cell = row.getCell(i, Row.RETURN_BLANK_AS_NULL);
		if (cell == null)
			return "";
		else
			return (cell.getCellType() == Cell.CELL_TYPE_NUMERIC ? String.format("%.0f", cell.getNumericCellValue())
					: cell.getStringCellValue().trim());
	}

}
