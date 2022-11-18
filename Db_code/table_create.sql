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
);
