CREATE OR REPLACE FUNCTION book_tickets(n_tickets INTEGER,names varchar(1000) ,pref varchar,left_seats integer,ttno integer,tdoj date)
RETURNS varchar AS
$$
DECLARE
Starting_seat integer;
pnr uuid;
Age integer;
gender varchar;
Curr varchar(10);
query varchar(1000);
query_result varchar(1000);
result varchar(1000);
i  integer;
 
BEGIN
Starting_seat:=left_seats+1;
Age :=10;
gender:='M' ;
pnr :=  uuid_generate_v4();
result := '';
result:= result || pnr || ' ';
For i in 1..n_tickets
   Loop
   Curr := split_part(names,' ',i );
   Select book_seat into query_result from book_seat(pnr,Curr,age,gender,pref,Starting_seat,ttno,tdoj);
   result:=result || query_result || ' ';
   -- query:= 'Select * from book_seat( ' || pnr || ','''|| Curr || ''',' || age || ',''' || gender || ''', '''|| pref || ''' ,''' || Starting_seat || ',' || ttno || ',''' || tdoj || ''');';
   Starting_seat=Starting_seat+1;
   END Loop;
   RETURN result;
END;
$$ LANGUAGE 'plpgsql';