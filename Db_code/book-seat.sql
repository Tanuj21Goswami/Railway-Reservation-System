CREATE OR REPLACE FUNCTION book_seat(pnr UUID,name varchar(50),age integer,gender varchar,pref varchar,left_seats integer,ttno integer,tdoj date)
RETURNS varchar AS
$$
DECLARE
pid uuid;
capacity integer;
coach_no varchar(3);
seat_no integer;
passenger_tn varchar(100);
booked_seats_tn varchar(100);
temp integer;
query varchar(1000);
result varchar(1000);
BEGIN
passenger_tn := 'passenger_' || CAST (ttno as VARCHAR(5));
booked_seats_tn := 'booked_seats_' || CAST(ttno as varchar(5));
pid := uuid_generate_v4();
IF pref = 'a' or pref = 'A'
THEN
        capacity := 18;
ELSE
        capacity := 24;
END IF;
IF left_seats%capacity = 0
THEN    
        temp := left_seats/capacity;
ELSE
        temp := left_seats/capacity +1;
END IF;
IF pref = 'a' or pref = 'A'
THEN
coach_no := 'A' || cast(temp as varchar(2));
ELSE
coach_no := 'S' || cast(temp as varchar(2));
END IF;
IF left_seats % capacity = 0
THEN
                seat_no = capacity;
ELSE
        seat_no = left_seats  % capacity;
END IF;
result:=coach_no || ' , ' || seat_no  ;
query := 'INSERT INTO '|| passenger_tn || ' values($1,$2,$3,$4);';
EXECUTE query USING pid,name,age,gender;
query := 'INSERT INTO '|| booked_seats_tn || ' values($1,$2,$3,$4,$5);';
EXECUTE query USING pid,pnr,coach_no,seat_no,tdoj;
RETURN result;
END;
$$ LANGUAGE 'plpgsql';