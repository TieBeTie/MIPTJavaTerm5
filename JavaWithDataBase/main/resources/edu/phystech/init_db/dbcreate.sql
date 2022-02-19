create table if not exists aircrafts
(
    aircraft_code char(3) primary key,
    model         jsonb   not null,
    range         integer not null
);

create table if not exists airports
(
    airport_code char(3) primary key,
    airport_name jsonb not null,
    city         jsonb not null,
    coordinates  point not null,
    timezone     text  not null
);

create table if not exists boarding_passes
(
    ticket_no   char(13)   not null,
    flight_id   integer    not null,
    boarding_no integer    not null,
    seat_no     varchar(4) not null
);

create table if not exists bookings
(
    book_ref     char(6) primary key,
    book_date    timestamptz    not null,
    total_amount numeric(10, 2) not null
);

create table if not exists flights
(
    flight_id           serial primary key,
    flight_no           char(6)     not null,
    scheduled_departure timestamptz not null,
    scheduled_arrival   timestamptz not null,
    departure_airport   char(3)     not null,
    arrival_airport     char(3)     not null,
    status              varchar(20) not null,
    aircraft_code       char(3)     not null,
    actual_departure    timestamptz,
    actual_arrival      timestamptz
);

create table if not exists seats
(
    aircraft_code   char(3)     not null,
    seat_no         varchar(4)  not null,
    fare_conditions varchar(10) not null
);

create table if not exists ticket_flights
(
    ticket_no       char(13)       not null,
    flight_id       integer        not null,
    fare_conditions varchar(10)    not null,
    amount          numeric(10, 2) not null
);

create table if not exists tickets
(
    ticket_no      char(13) primary key,
    book_ref       char(6)     not null,
    passenger_id   varchar(20) not null,
    passenger_name text        not null,
    contact_data   jsonb
);