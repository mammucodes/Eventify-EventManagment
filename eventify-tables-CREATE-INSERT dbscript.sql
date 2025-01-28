use eventify_db;
-- ------INSERT INTO USERS-------
INSERT INTO users (name, email, password, isorganizer, registered_on)
VALUES 
('John Doe', 'john.doe@example.com', 'securepassword1', 1, NOW()),
('Jane Smith', 'jane.smith@example.com', 'securepassword2', 1, NOW()),
('Alex Johnson', 'alex.johnson@example.com', 'securepassword3', 1, NOW());

-- ------INSERT INTO EVENTS-------
INSERT INTO events (name, performer, city, category, event_start_time, event_end_time, descrption, organizer_id,status_id)
VALUES
('TollyWood Night', 'Raaj Bhai', 'dallas', 'Concert', '2025-01-24 20:00:00', '2025-01-24 22:00:00', 'A magical night with Bollywood songs', 5,1), -- Organizer ID 1
(' Music Evening', 'Limbu Mahadevan', 'dallas', 'Music', '2025-01-24 21:00:00', '2025-01-24 22:00:00', 'Indian classical performances', 6,1), -- Organizer ID 2
('Sleeping comedy', 'riya Khan', 'dallas', 'Comedy', '2025-01-24 19:00:00', '2025-01-24 23:00:00', NULL, 5,1), -- Organizer ID 3
('Dance Fiesta', 'Prabhu Deva', 'Austin', 'Dance', '2025-02-10 17:00:00', '2025-02-10 20:00:00', 'A night of electrifying dance performances', 7), -- Organizer ID 4
('Theater Showcase', 'Irrfan Khan', 'Plano', 'Theater', '2025-01-20 16:00:00', '2025-01-20 19:00:00', NULL, 5); -- Organizer ID 5

 


-- --------- create STATUS TABLE----------
CREATE   TABLE event_status_options(id INT auto_increment primary key, status varchar(50) not null  unique);

  -- INSERT INTO EVENT STATUS OPTION------------   
INSERT INTO  event_status_options(status)VALUES('available'),('cancelled'),('completed');

-- --------------- CREATE EventTicketTable-----------
CREATE TABLE event_tickets(id INT auto_increment primary key, available_tickets INT ,ticekt_price  int,max_ticekts_allowed_to_book int, 
 event_id BIGINT not null , 
    CONSTRAINT fk_events_ticekts FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE);
    
  -- ------------- INSERT INTO EVENT_TICKETS---------------   
INSERT into event_tickets(available_tickets,ticekt_price,max_ticekts_allowed_to_book,event_id)values(3,20,4,19),
(10,10,4,20);


-- -------- CREATE USER BOOKED TICKET TABLE----------
create table user_tickets_details(id int auto_increment primary key,
event_id  bigint,
 constraint fk_event_id foreign key(event_id) REFERENCES events(id) ON DELETE SET NULL,
 user_id bigint ,
 constraint fk_user_id foreign key(user_id) references users(id) on delete cascade,
 count int default 0);
 
 -- ------INSERT INTO user_tickets_details-------
 insert into  user_tickets_details(event_id,user_id,seats_booked,check_in_count,check_out_count)
 values
 (41,3,2,0,0),
 (42,3,1,0,0),
 (43,2,3,0,0);


-- ------CREATE  EMail Verifcation TABLE-------
create table email_otp_verification(id int  auto_increment primary key ,email varchar(200), otp varchar(50));

select * from email_otp_verification;
drop table email_otp_verification;
-- ------INSERT into email_otp_verfication table--------
insert into   email_otp_verification(email,otp)values();









