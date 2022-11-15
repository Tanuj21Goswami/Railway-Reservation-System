CREATE OR REPLACE FUNCTION is_seat_available(ttno integer, tdoj date,preference varchar(1), req_seats integer)
RETURNS INTEGER AS $$
DECLARE
        left_seats integer;
BEGIN
        IF preference = 'A' or preference = 'a'
        THEN
                left_seats := (select rt.seats_left_a from running_trains rt where rt.tno = ttno and rt.doj  =tdoj limit 1);
        ELSE
                left_seats := (select rt.seats_left_s from running_trains rt where rt.tno = ttno and rt.doj =tdoj limit 1);
        END IF;
        IF left_seats >= req_seats
        THEN
        left_seats:= left_seats-req_seats;
        IF preference = 'A' or preference = 'a'
        THEN
                UPDATE running_trains 
                SET seats_left_a = left_seats
                WHERE tno = ttno and doj = tdoj;
        ELSE
                UPDATE running_trains 
                SET seats_left_s = left_seats
                WHERE tno = ttno and doj = tdoj;
        END IF;
        RETURN left_seats;
        ELSE RETURN -1;
        END IF;
        END;
$$ LANGUAGE 'plpgsql';
