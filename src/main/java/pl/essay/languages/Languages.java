package pl.essay.languages;

import java.util.List;

public class Languages {

	private List<Language> languages;
	private Language defaultLanguage;
	
	public void init(){
		//System.out.println("init languages");
		//System.out.println("default is"+this.getDefaultLanguage().getName());
		for (Language l : this.languages){
			//System.out.println("processing "+l.getName());
			if (l != this.getDefaultLanguage()){ //comparing objects and that is ok!
				//System.out.println(" languages different");
				l.addTranslations(this.getDefaultLanguage()); //add translations form default ie english
			}
		}
	}
	
	public void setLanguages(List<Language> x){
		this.languages = x;
	}
	
	public void setDefaultLanguage(Language l){
		this.defaultLanguage = l;
	}
	
	public List<Language> getLanguages(){
		return this.languages;
	}
			
	public Language getLanguage(String l){
		for (Language lang: languages)
			if (l.equals(lang.getAcronym())) 
					return lang; 
		return this.getDefaultLanguage();
	}
	
	public Language getDefaultLanguage(){
		return this.defaultLanguage;
	}
	
}
