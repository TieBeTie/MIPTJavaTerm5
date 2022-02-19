delete
from tickets
    using (
        Select distinct tickets.ticket_no
        from tickets
                 inner join ticket_flights tf on tickets.ticket_no = tf.ticket_no
                 inner join flights on tf.flight_id = flights.flight_id
        where aircraft_code = ?
    ) as ticket_full_info
where tickets.ticket_no = ticket_full_info.ticket_no;