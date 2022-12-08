# Railway-Reservation-System

## About

This is the implementation of a scalable Postgres database system for booking railway tickets. Through concurrency control protocols, it can manage multiple users accessing the database system. Through the use of straightforward queries, schedules can be added and travel plans may be quickly searched.

## How to Run the Code (Linux)
Assuming all the below files are under the same directory
### 1. Start server
Compile the server program : " javac ServiceModule.java "

Run the Server exe : " java ServiceModule "

<img width="614" alt="Screenshot_20221208_144245" src="https://user-images.githubusercontent.com/93069420/206407650-7ade5ebd-9271-4584-90f5-23dcbea295ca.png">

### 2. Load input files
// only 5 users (threads) launched from the client

<img width="411" alt="Screenshot_20221208_144351" src="https://user-images.githubusercontent.com/93069420/206408096-beb022ab-4055-482c-ae33-b266cb2b7d5f.png">

### 3. Run client
Compile the client program: " javac client.java "

Run the client exe : " java client "

<img width="457" alt="Screenshot_20221208_144406" src="https://user-images.githubusercontent.com/93069420/206408459-7ce19de8-48b1-4dda-8a5a-9d976e120e0c.png">

### 4. Response in the server, Connecting 5-requests
<img width="488" alt="Screenshot_20221208_144421" src="https://user-images.githubusercontent.com/93069420/206408965-56e9b5a4-6d80-44a5-891c-a80ebf7f7e21.png">

### 5. Output files are generated
<img width="500" alt="Screenshot_20221208_144429" src="https://user-images.githubusercontent.com/93069420/206409159-8e786fc0-af91-4cf7-b76d-018d2cca691d.png">
