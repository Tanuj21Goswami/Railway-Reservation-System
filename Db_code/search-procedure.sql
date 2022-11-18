CREATE OR REPLACE FUNCTION search_procedure(st VARCHAR(50) , en VARCHAR(50) )
    RETURNS TABLE(
    train_1 INTEGER, 
    train_1_starting VARCHAR(50), 
    train_1_ending VARCHAR(50),
    train1_st_time time, 
    train1_en_time time, 
    train_1_doj_st DATE,
    train_1_doj_en DATE,
    train_2 INTEGER, 
    train_2_starting VARCHAR(50), 
    train_2_ending VARCHAR(50),
    train2_st_time time, 
    train2_en_time time, 
    train_2_doj_st DATE,
    train_2_doj_en DATE 
)
    AS $$
    DECLARE

      get_train_at_st1 cursor(sta varchar(50)) for select tno,arr_time,dep_time,doj from train_journey_info where stn = sta;
      get_train_journey1 cursor(ttno integer) for select stn,arr_time,dep_time,doj from train_journey_info where tno = ttno;
      get_train_at_st2 cursor(sta varchar(50)) for select tno,arr_time,dep_time,doj from train_journey_info where stn = sta;
      get_train_journey2 cursor(ttno integer) for select stn,arr_time,dep_time,doj from train_journey_info where tno = ttno;
      tn1 integer;
      ar1 time;
      dep1 time;
      doj1 date;
      itm_st varchar(50);
      itm_ar time;
      itm_dep time;
      itm_date date;
      tn2 integer;
      ar2 time;
      dep2 time;
      doj2 date;
      final_st varchar(50);
      final_ar time;
      final_dep time;
      final_date date;

    BEGIN
    DELETE FROM QUERY_RESULT;
    open get_train_at_st1(st);
    loop
      fetch get_train_at_st1 into tn1,ar1,dep1,doj1;
      exit when not found;
      -- RAISE NOTICE '% % % %',tn1,ar1,dep1,doj1;
      open get_train_journey1(tn1);
      loop
         fetch get_train_journey1 into  itm_st,itm_ar,itm_dep,itm_date;
         exit when not found;
         -- RAISE NOTICE '% % % %',itm_st,itm_ar,itm_dep,itm_date;
         IF (itm_date = doj1 and itm_ar >= dep1) or (itm_date > doj1)
         THEN
            IF itm_st = en
            THEN
               INSERT INTO QUERY_RESULT VALUES(tn1,st,en,dep1,itm_ar,doj1,itm_date);
            ELSE
               open get_train_at_st2(itm_st);
               loop 
                  fetch get_train_at_st2 into tn2,ar2,dep2,doj2;
                  exit when not found;
                  -- RAISE NOTICE '% % % %',tn2,ar2,dep2,doj2;
                  IF tn2 != tn1 and ((itm_date < doj2) or (itm_date = doj2 and  dep2 >= itm_ar)) 
                  THEN
                        open get_train_journey2(tn2);
                        loop
                           fetch get_train_journey2 into final_st,final_ar,final_dep,final_date;
                           exit when not found;
                           -- RAISE NOTICE '% % % %',final_st,final_ar,final_dep,final_date;
                              IF final_st = en and ((final_date = doj2 and final_ar >= dep2 ) or final_date > doj2)                                
                              THEN
                                  INSERT INTO QUERY_RESULT VALUES(tn1,st,itm_st,dep1,itm_ar,doj1,itm_date,tn2,itm_st,en,itm_dep,final_ar,itm_date,final_date);
                                  RAISE NOTICE 'INDIRECT TRAIN JOURNEY FOUND';
                              END IF;
                        end loop;
                        close get_train_journey2;
                  END IF;
               end loop;         
               close get_train_at_st2;
            END IF;
         END IF;
         end loop;
         close get_train_journey1;
    end loop;
    close get_train_at_st1;
    RETURN query (select * from QUERY_RESULT);
    END;
    $$ LANGUAGE 'plpgsql';
