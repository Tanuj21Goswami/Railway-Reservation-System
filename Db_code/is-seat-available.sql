CREATE OR REPLACE FUNCTION is_seat_available(ttno integer, tdoj date,preference varchar(1), req_seats integer)
RETURNS INTEGER AS $$
DECLARE
        left_seats integer;
        train_tn varchar(50);
        query varchar(100);
        tble_count integer;
BEGIN
        select count(*) into tble_count from running_trains where tno = ttno and doj = tdoj;
        IF tble_count =0
        THEN
                RETURN -1;
        END IF;
        train_tn := 'train_' || CAST (ttno as VARCHAR(5));    
        IF preference = 'A' or preference = 'a'
        THEN
                query := 'select left_seats_a from '|| train_tn || ' where doj = $1 limit 1;';
                execute query into left_seats using tdoj;
        ELSE
                query := 'select left_seats_s from '|| train_tn || ' where doj = $1 limit 1;';
                execute query into left_seats using tdoj;
        END IF;
        RAISE NOTICE '%',left_seats;
        IF left_seats >= req_seats
        THEN
        left_seats:= left_seats-req_seats;
        IF preference = 'A' or preference = 'a'
        THEN
                query := 'UPDATE ' || train_tn|| ' SET left_seats_a = $1 WHERE doj = $3;';
                execute query using left_seats,ttno,tdoj;
        ELSE
                query := 'UPDATE ' || train_tn|| ' SET left_seats_s = $1 WHERE doj = $3;';
                execute query using left_seats,ttno,tdoj;
        END IF;
        RETURN left_seats;
        ELSE RETURN -1;
        END IF;
        END;
$$ LANGUAGE 'plpgsql';
