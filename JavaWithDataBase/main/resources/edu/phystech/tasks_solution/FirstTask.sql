Select city, string_agg(airport, ', ') as cnt from
(Select city->>'ru' as city, airport_name->>'ru' as airport
from airports) as city_info
group by city
having count(1) > 1;