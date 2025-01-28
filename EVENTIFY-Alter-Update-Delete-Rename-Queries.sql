-- UPDATE USER Details------
UPDATE users 
SET email = 'naag6889@gmail.com'
WHERE id = 3;

-- **----------- ALTER Event Table-----------
-- to add column name------------
ALTER TABLE events
ADD organizer_id BIGINT;

ALTER TABLE events
ADD CONSTRAINT fk_events_organizer
FOREIGN KEY (organizer_id) REFERENCES users(id);

-- ----adding status-id column with default value 1-------
ALTER TABLE events
ADD COLUMN status_id INT DEFAULT 1;

ALTER TABLE events
ADD CONSTRAINT fk_status_id FOREIGN KEY (status_id) REFERENCES event_status_options(id);


ALTER TABLE events
MODIFY COLUMN status_id INT NOT NULL;

-- --------TO DROP Column----------
ALTER TABLE events
DROP COLUMN description;

-- DELETE QUERY----
delete from events where organizer_id is null;

-- -----------UPDATE EVENT-------
UPDATE events
SET event_end_time = '2025-02-15 16:30:00'
WHERE id = 43;


-- ***********Alter operations on USER TICKETs DETAILS******------------
-- ADDING REMAINDERSENT COLUMN TO USER TICKETS TABLE----------
 alter table user_tickets_details add column remainder_sent boolean  default false;
 

 RENAME TABLE user_booked_tickets_details TO user_tickets_details;
 
 -- ------- RENAME COLUMN NAME---------
   ALTER TABLE   user_tickets_details
RENAME COLUMN tickets_booked TO seats_booked;

ALTER TABLE user_tickets_details 
RENAME COLUMN checkIn_count to check_in_count;

-- ------ADD COLUMNN NAME------
ALTER TABLE   user_tickets_details
ADD COLUMN checkOut_count int;

-- ---------UPDATE -----
UPDATE user_tickets_details
SET seats_booked = 1
WHERE id =10;

UPDATE user_tickets_details
SET ticket_booked_time = '2025-01-01 12:00:00'
WHERE ticket_booked_time IS NULL;


