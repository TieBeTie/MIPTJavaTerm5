Select *
from seats
         inner join flights f on seats.aircraft_code = f.aircraft_code
where flight_id = ? and seat_no = ?;