package pl.essay.imangular.domain;

/*
 * makes id for entity objects
 *  
 */
public class IdCounter {
	
	int id = 1;
	
	public IdCounter(){};
	
	public int get(){
		return id++;
	}

}
