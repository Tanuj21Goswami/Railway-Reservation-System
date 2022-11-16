 CREATE OR REPLACE FUNCTION search_procedure(st VARCHAR , en VARCHAR )
    RETURNS VARCHAR AS $$
    DECLARE
    st_train CURSOR (_st varchar) FOR select TNO,DOJ,ENDING_STN,ARR_TIME from running_trains where STARTING_STN = _st;
    en_train CURSOR (_en varchar) FOR select TNO,DOJ,STARTING_STN,DEP_TIME from running_trains where ENDING_STN = _en;
    tno1 integer;
    tno2 integer;
    en_sta varchar(50);
    st_sta varchar(50);
    st2_tno integer;
    en2_tno integer;
    st_time varchar(50);
    en_time varchar(50);
    st_doj DATE;
    en_doj DATE;
    BEGIN
    SELECT TNO1,STARTING_STN as st_stn1 ,ENDING_STN as en_stn1 ,DEP_TIME as dt1, ARR_TIME as at1
    FROM RUNNING_TRAINS WHERE
       
    END;
    $$ LANGUAGE 'plpgsql';