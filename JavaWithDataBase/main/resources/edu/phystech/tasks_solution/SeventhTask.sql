with
     data_cancel_from as (
         Select to_date(?, 'dd.mm.yy')
     ),
     data_cancel_to as (
         Select to_date(?, 'dd.mm.yy') + interval '1 day'
     ),
     city_info as (
         Select city ->> 'ru' as city, airport_code as airport_code
         from airports
     ),
     flights_info as (
         Select flight_id,
                departure_airport,
                arrival_airport,
                scheduled_departure,
                scheduled_arrival
         from flights
     ),
     flights_to_cancel as (
         Select flight_id
         from flights_info
                  inner join city_info on flights_info.departure_airport = city_info.airport_code
         where city = 'Москва'
           and scheduled_departure >= (Select * from data_cancel_from)
           and scheduled_departure < (Select * from data_cancel_to)
         union
         Select flight_id
         from flights_info
                  inner join city_info on flights_info.arrival_airport = city_info.airport_code
         where city = 'Москва'
           and scheduled_arrival >= (Select * from data_cancel_from)
           and scheduled_arrival < (Select * from data_cancel_to)
     ),
     update_flights as (
         Update flights
             set status = 'Cancelled'
             where status <> 'Cancelled'
                 and flight_id in (Select * from flights_to_cancel)
             returning flight_id, scheduled_departure::date as flight_date)
SELECT flight_date, sum(amount)
from update_flights
         inner join ticket_flights on update_flights.flight_id = ticket_flights.flight_id
group by flight_date;