
SHOW VARIABLES LIKE 'port';
-- ******* USER SELECT QUERIES********* 
select * from users;
select * from users where id = 1;

-- ----------EVENTS Select Queries----------
select * from events;
select * from events where id = 20;
select * from events where city ="dallas"and status_id = 1;
select * from events where city = 'dallas' and name like '%Bolly%';

SELECT * 
FROM events e 
WHERE e.city = 'Dallas'
  AND (null IS NULL OR e.performer LIKE CONCAT('%', 'Doe', '%'))
  AND ('Bolly' IS NULL OR e.name LIKE CONCAT('%', 'Bolly', '%'));

 select e1_0.id,e1_0.category,e1_0.city,e1_0.description,e1_0.event_start_time,e1_0.event_end_time,e1_0.name,e1_0.performer 
 from events e1_0 where e1_0.performer='rakhi'  and e1_0.event_start_time='2025-02-12 10:23:45.123';

-- *******EVENT TICKET DETAILS SELECT Queries**********---
select * from event_tickets;


-- ----******USER TICKET DETAILS select Queries***********----------
select * from user_tickets_details;

select * from 
user_tickets_details ut
where ut.id=3;

select  * from user_tickets_details b 
join events e on e.id = b.event_id 
join users u on u.id = b.user_id 
where b.id = 3;

select  u.email,e.name,e.city,e.event_start_time,e.performer, ut.id as 'ticketId',ut.seats_booked
 from  user_tickets_details ut  
join events e on  e.id = ut.event_id and e.event_start_time between 
"2025-02-12 00:00:00" and "2025-02-12 23:59:59" and e.status_id=1 
join users  u  on u.id = ut.user_id;

select ut1_0.id,ut1_0.check_in_count,ut1_0.check_out_count,ut1_0.event_id,ut1_0.seats_booked,ut1_0.ticket_booked_time,ut1_0.user_id
 from user_tickets_details ut1_0 join events e1_0 on e1_0.id=ut1_0.event_id join users u1_0 
on u1_0.id=ut1_0.user_id where e1_0.event_start_time between "2024-01-24 22:14:00"  and  "2026-01-24 23:00:00" and  e1_0.status_id=1;



-- -----------EVENT STATUS OPTION QUERIES------------
select * from event_status_options;