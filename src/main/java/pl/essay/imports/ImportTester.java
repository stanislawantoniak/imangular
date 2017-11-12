package pl.essay.imports;

import java.util.Map;

public class ImportTester {

	public static void __main(String[] args) {

		XLSProductsImporter importer = new XLSProductsImporter();
		importer.importFile("skladniki.xlsx");

		for (Map.Entry<String, Product> product : importer.getProducts().entrySet())
			System.out.print(product.getKey() + ":: " + product.getValue());

		System.out.println("products total: " + importer.getProducts().size());

	}

}
