package pl.essay.imports;

import java.util.HashMap;
import java.util.Map;

public class Product {

	public String id;
	public String name;
	public boolean isComposed;
	public boolean isBuilding = false;
	public String whereManufactured = "";
	public boolean otherSources;
	public String otherSourceName = "";

	public int itemId;

	public Map<String, Component> components = new HashMap<String, Component>();

	public Product() {
	};

	public String toString() {

		StringBuilder b = new StringBuilder();

		b.append(name + " :: is composed = " + isComposed + ", isBuilding = " + isBuilding + ", whereManufactured = "
				+ whereManufactured + ", otherSources = " + otherSources + ", otherSourceName = " + otherSourceName
				+ "\n");

		if (this.isComposed && components.size() > 0) {
			b.append("\tcomponents:\n");
			for (Map.Entry<String, Component> c : components.entrySet())
				b.append("\t\t" + c.getValue() + "\n");
		}
		return b.toString();
	}

	public class Component {

		public String pid;
		public String product;
		public String cid = "";
		public String component = "";
		public int quantity;
		public String desc = "";

		public Component() {
		};

		public String toString() {
			return component + "[" + cid + "], qty = " + quantity + ", desc = " + desc;
		}
	}

}
