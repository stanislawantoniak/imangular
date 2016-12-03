select * 
from item_component ic
join item c on c.id = ic.component_id

where c.id = 1361