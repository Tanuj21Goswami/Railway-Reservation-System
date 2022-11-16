CREATE OR REPLACE FUNCTION insert_train(_tno integer,_no_a_coaches integer,_no_s_coaches integer,_doj date)
RETURNS INTEGER AS $$
DECLARE
        a_seats integer;
        s_seats integer;
        a_capacity integer;
        s_capacity integer;
        global_constant record;
        old_train record;
        query VARCHAR(500);
        passenger_tn varchar(100);
        tble_count integer;
        booked_seats_tn varchar(100);
BEGIN
        IF _tno <0 or _tno > 99999
        THEN
                RETURN 0;
        END IF;
        
        select count(*) into tble_count from running_trains where tno = _tno and _doj = doj;
        IF tble_count !=0
        THEN
                RETURN 2;
        END IF;
        select * into a_capacity,s_capacity from global_constants limit 1;
        a_seats := a_capacity *_no_a_coaches;
        s_seats := s_capacity *_no_s_coaches;

        INSERT INTO running_trains VALUES(_tno,_no_a_coaches,_no_s_coaches,_doj,a_seats,s_seats);
        passenger_tn := 'passenger_' || CAST (_tno as VARCHAR(5));
        booked_seats_tn := 'booked_seats_' || CAST(_tno as varchar(5));
        query := 'create table ' || passenger_tn ||
                '(PID uuid NOT NULL,
                Name varchar(50) NOT NULL,
                Age INTEGER NOT NULL,
                Gender varchar NOT NULL,
                PRIMARY KEY(PID));';
IF NOT (SELECT EXISTS ( SELECT FROM information_schema.tables WHERE  table_name =  passenger_tn))
THEN 
                execute query;
END IF;
	     
            query := 'create table '|| booked_seats_tn ||
                 e'\(    PID uuid NOT NULL,
                          PNR UUID NOT NULL,
                          COACH_NO VARCHAR(3) NOT NULL,
                          SEAT_NO INTEGER NOT NULL,
		   DOJ DATE NOT NULL,
                          PRIMARY KEY(PNR,PID),
                          FOREIGN KEY(PID) REFERENCES '
  || passenger_tn ||  e'\);' ;
        IF NOT (SELECT EXISTS ( SELECT FROM information_schema.tables WHERE  table_name = booked_seats_tn))
        THEN
                        execute query;
        END IF;
        RETURN 1;
END;
$$ LANGUAGE 'plpgsql';

