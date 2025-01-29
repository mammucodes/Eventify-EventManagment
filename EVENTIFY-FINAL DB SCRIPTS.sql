CREATE database EVENTIFY_2;
USE EVENTIFY_2;
-- ---------CREATE USERS TABLE----------
create  table users(id  bigInt auto_increment primary key,name varchar(20),email varchar(250) not null,
password varchar(250) not null ,isorganizer boolean  not null ,registered_on datetime not null);


-- ---------CREATE EVENTS TABLE--------------
create table events (id   bigInt auto_increment primary key, name  varchar(100) not null, performer  varchar(100) not null,
city  varchar(100) not null, category varchar(100) not null, event_start_time timestamp not null, event_end_time timestamp not null ,
description varchar(100),
organizer_id bigInt ,
constraint fk_organizer_id  foreign key( organizer_id) REFERENCES users(id),
 status_id INT DEFAULT 1,
  CONSTRAINT fk_status_id FOREIGN KEY (status_id) REFERENCES event_status_options(id));

-- --------- CREATE STATUS TABLE----------
CREATE   TABLE event_status_options(id INT auto_increment primary key, status varchar(50) not null  unique);

-- --------------- CREATE EventTicketTable-----------
CREATE TABLE event_tickets(id INT auto_increment primary key, available_tickets INT not null ,ticekt_price  int not null,max_ticekts_allowed_to_book int not null, 
 event_id BIGINT not null , 
    CONSTRAINT fk_events_ticekts FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE);
    
    -- -------- CREATE USER  TICKET  DETAILS TABLE----------
create table user_tickets_details(id int auto_increment primary key,
event_id  bigint,
 constraint fk_events_organizer foreign key(event_id) REFERENCES events(id) ON DELETE SET NULL,
 user_id bigint ,
 constraint fk_user_id foreign key(user_id) references users(id) on delete cascade,
 check_in_count int default 0 not null,
 seats_booked int not null,
 ticket_booked_time  datetime not null,
  check_out_count int default 0 not null, 
 remainder_sent  boolean  default false NOT NULL );
 
 -- ------CREATE  EMail Verifcation TABLE-------
create table email_otp_verification(id int  auto_increment primary key ,email varchar(200)not null, otp varchar(50) not null,
otp_created_on datetime not null);
select * from email_otp_verification;


 
