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
        OPEN st_train(st);
        LOOP
            FETCH st_train INTO tno1,st_doj,en_sta,en_time;
            EXIT WHEN st_train %NOTFOUND;
            LOOP
                OPEN en_train(en);
                FETCH en_train INTO tno2,en_doj,st_sta,st_time;
                EXIT WHEN en_train%NOTFOUND;
                IF st_doj <= en_doj and isbefore(en_time , st_time) and en_sta = st_sta
                THEN
                    RAISE NOTICE '% - %',tno1,tno2;
                END IF;
            END LOOP;
        END LOOP;
    END;
    $$ LANGUAGE 'plpgsql';