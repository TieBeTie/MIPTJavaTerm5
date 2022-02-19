with city_info as (
    Select city ->> 'ru' as city, airport_code as airport_code
    from airports
),
all_flights_info as (
    Select city, arrival_airport, avg_time
    from city_info
             inner join
         (
             Select flight_no, departure_airport, arrival_airport, avg(actual_arrival - actual_departure) as avg_time
             from flights
             where actual_arrival IS NOT NULL
               and actual_departure IS NOT NULL
             group by flight_no, departure_airport, arrival_airport
         ) as flights_info
         on city_info.airport_code = flights_info.departure_airport
)
Select all_flights_info.city, city_info.city, avg_time
from all_flights_info
         inner join city_info on arrival_airport = city_info.airport_code
where avg_time = (
    Select min(avg_time)
    from all_flights_info as helper
    where all_flights_info.city = helper.city
)
order by avg_time
;