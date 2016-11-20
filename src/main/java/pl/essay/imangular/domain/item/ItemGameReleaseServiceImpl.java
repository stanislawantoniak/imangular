package pl.essay.imangular.domain.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pl.essay.generic.dao.ListingParamsHolder;
import pl.essay.generic.dao.SetWithCountHolder;

@Service
@Transactional
public class ItemGameReleaseServiceImpl implements ItemGameReleaseService{

	@Autowired private ItemGameReleaseDao itemGameReleaseDao;

	@Override
	public void updateItemGameRelease(ItemGameRelease i){
		this.itemGameReleaseDao.update(i);
	}

	@Override
	public int addItemGameRelease(ItemGameRelease i){
		this.itemGameReleaseDao.create(i);
		return i.getId();
	}

	@Override
	@Transactional(readOnly = true)
	public SetWithCountHolder<ItemGameRelease> listItemGameReleases(){
		return this.itemGameReleaseDao.getAll();
	}

	@Override
	@Transactional(readOnly = true)
	public SetWithCountHolder<ItemGameRelease>  listItemGameReleasesPaginated(ListingParamsHolder params){
		return this.itemGameReleaseDao.getAll(params);
	}

	@Override
	@Transactional(readOnly = true)
	public ItemGameRelease getItemGameReleaseById(int id){
		ItemGameRelease item = this.itemGameReleaseDao.get(id);
		return item;
	}

	@Override
	public void removeItemGameRelease(int id){
		ItemGameRelease item = this.itemGameReleaseDao.load(id);
		this.itemGameReleaseDao.delete(item);
	}

}
