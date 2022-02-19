with city_info as (
    Select city ->> 'ru' as city, airport_code as airport_code
    from airports
),
     flights_info as (
         Select departure_airport,
                arrival_airport,
                extract(dow from scheduled_departure) as week_day,
                to_char(scheduled_departure, 'Day')   as week_day_char
         from flights
     )
Select direction, week_day_char, cnt
from (
         Select 'Из Москвы' as direction, week_day, week_day_char, count(1) as cnt
         from flights_info
                  inner join city_info on flights_info.departure_airport = city_info.airport_code
         where city = 'Москва'
         group by week_day, week_day_char
         union
         Select 'В Москву' as direction, week_day, week_day_char, count(1) as cnt
         from flights_info
                  inner join city_info on flights_info.arrival_airport = city_info.airport_code
         where city = 'Москва'
         group by week_day, week_day_char
     ) as unsorted_result
order by direction, week_day
;