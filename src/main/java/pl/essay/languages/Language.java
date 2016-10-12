package pl.essay.languages;

public class Language {
	private String name;
	private String acronym;
	private String flag;
	
	private Translator translator;
		
	public String getName(){
		return this.name;
	};
	public void setName(String l){
		this.name = l;
	};
	
	public String getAcronym(){
		return this.acronym;
	};
	public void setAcronym(String l){
		this.acronym = l;
	};
	public String getFlag(){
		return this.flag;
	};
	public void setFlag(String l){
		this.flag = l;
	};
	
	public void setTranslator(Translator t){
		this.translator = t;
	}
	
	public Translator getTranslator(){
		return this.translator;
	}
	
	public void addTranslations(Language l){
		this.translator.addTranslations(l.getTranslator());
	}
}
