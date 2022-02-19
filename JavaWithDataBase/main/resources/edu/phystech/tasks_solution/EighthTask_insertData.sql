select * from tickets;
insert into tickets (ticket_no, book_ref, passenger_id, passenger_name, contact_data)
values (?, ?, ?, ?, ?);
insert into ticket_flights (ticket_no, flight_id, fare_conditions, amount)
values (?, ?, ?, ?);