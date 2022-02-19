Select city, count(1) as cancelled_cnt from
(
    Select city->>'ru' as city, airport_code as airport_code
    from airports
) as city_info
    inner join
(
    Select departure_airport from flights
    where status = 'Cancelled'
) as flights_info
    on city_info.airport_code = flights_info.departure_airport
group by city
order by cancelled_cnt desc
limit 10;