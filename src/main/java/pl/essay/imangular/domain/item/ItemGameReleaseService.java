package pl.essay.imangular.domain.item;

import pl.essay.generic.dao.ListingParamsHolder;
import pl.essay.generic.dao.SetWithCountHolder;

public interface ItemGameReleaseService {
	
	public void updateItemGameRelease(ItemGameRelease i);
	
	public int addItemGameRelease(ItemGameRelease i);

	public SetWithCountHolder<ItemGameRelease> listItemGameReleases();
	
	public SetWithCountHolder<ItemGameRelease>  listItemGameReleasesPaginated(ListingParamsHolder params);
	
	public ItemGameRelease getItemGameReleaseById(int id);
	
	public void removeItemGameRelease(int id);
}
