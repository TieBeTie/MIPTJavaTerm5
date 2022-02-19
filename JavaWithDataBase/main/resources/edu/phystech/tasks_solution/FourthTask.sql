select to_char(scheduled_departure, 'Month') as month, count(1) from flights
where status = 'Cancelled'
group by month;