select * 
from item i 
join item_component ic on i.id = ic.parent_id
join item c on c.id = ic.component_id

where i.id = 1365