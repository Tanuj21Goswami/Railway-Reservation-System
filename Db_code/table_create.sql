CREATE TABLE RUNNING_TRAINS(
        TNO INTEGER NOT NULL,
        DOJ DATE NOT NULL,
        PRIMARY KEY(TNO,DOJ)
);

CREATE TABLE train_journey_info(
    tno INTEGER,
    stn varchar(50),
    arr_time time,
    dep_time time,
    doj date,
    PRIMARY KEY(tno,stn,doj)
);

CREATE TABLE QUERY_RESULT(
        
)