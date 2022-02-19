delete
from ticket_flights
    using (
        Select flight_id
        from flights
        where aircraft_code = ?
    ) as flights_to_delete
where ticket_flights.flight_id = flights_to_delete.flight_id;