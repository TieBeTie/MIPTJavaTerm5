delete
from flights
where aircraft_code = ?
returning flight_id;