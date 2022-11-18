import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.sql.*;
import java.io.File;
import java.io.FileReader;
class QueryRunner implements Runnable
{
   //  Declare socket for client access
   protected Socket socketConnection;
   private Connection con;
   private int max_try;
   public QueryRunner(Socket clientSocket,Connection con){
       this.socketConnection =  clientSocket;
       this.con  = con;
       this.max_try  = 10;
       
   }
   public void run()
   {
     try{        
            this.con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE); 
            this.con.setAutoCommit(false); 
            // Reading data from client
           InputStreamReader inputStream = new InputStreamReader(socketConnection
                                                       .getInputStream());
           BufferedReader bufferedInput = new BufferedReader(inputStream) ;
           OutputStreamWriter outputStream = new OutputStreamWriter(socketConnection                                                                    .getOutputStream()) ;
           BufferedWriter bufferedOutput = new BufferedWriter(outputStream) ;
           PrintWriter printWriter = new PrintWriter(bufferedOutput, true) ;
           String clientCommand = "" ;
           String responseQuery = "" ;
           String queryInput = "" ;
           String inputQ="";
        //    int query=0;
           while(true)
           {
                // query=query+1;
               // Read client query
               clientCommand = bufferedInput.readLine();
               StringTokenizer tokenizer = new StringTokenizer(clientCommand);
               queryInput = tokenizer.nextToken();
            //    System.out.println("query input: "+queryInput);
               int len=queryInput.length();
               if(queryInput.equals("#")){
                   String returnMsg = "Connection Terminated - client : "
                                       + socketConnection.getRemoteSocketAddress().toString();
                   System.out.println(returnMsg);
                   inputStream.close();
                   bufferedInput.close();
                   outputStream.close();
                   bufferedOutput.close();
                   printWriter.close();
                   socketConnection.close();
                   return;
               }
               String delimSpace = "[ ]+";
               String[] sample  = clientCommand.split(delimSpace);
               len =sample.length;
               if(len==4)
               {
                   delimSpace = "[ ]+";
                   String[] arr1  = clientCommand.split(delimSpace);
                   inputQ=inputQ+"SELECT * from insert_train(";
                   inputQ=inputQ+arr1[0]+",";
                   inputQ=inputQ+arr1[2]+",";
                   inputQ=inputQ+arr1[3]+",";
                   inputQ=inputQ+"'"+arr1[1]+"');";
                //    System.out.println(inputQ);
                   try{
                        
                        try {
                            Statement st = this.con.createStatement();
                            ResultSet rs =st.executeQuery(inputQ);
                            while(rs.next())
                            {
                                // System.out.println(rs.getInt(1));
                                if(rs.getInt(1)==1)
                                {
                                    responseQuery = "Train " + arr1[0] + " has been inserted on " + arr1[1];
                                }
                                else if(rs.getInt(1)==0)
                                {
                                    responseQuery = "Train " + arr1[0] + " is an invalid train";
                                }
                                else
                                {
                                    responseQuery = "Train " + arr1[0] + " already exists for " + arr1[1];
                                }
                            }
                            // System.out.println("query executed");
                            this.con.commit();
                        } catch (SQLException e) {
                            // TODO Auto-generated catch block
                            this.con.rollback();
                            e.printStackTrace();
                        }
                    } catch (SQLException e) {
                        System.out.println("Error in setting auto commit to false");
                    }
                   inputQ="";
               }
               else
               {
                //    System.out.println(pnr);
                   String forfunc ="";
                //    String output="";
                   String dummy="";
                   forfunc=forfunc+"SELECT * from book_tickets(";
                   inputQ=inputQ+"SELECT * from is_seat_available(";
                   String delim = "[, ]+";
                   String[] arr1  = clientCommand.split(delim);
                   int x =arr1.length;
                   inputQ=inputQ+arr1[x-3]+",";
                   inputQ=inputQ+"'"+arr1[x-2]+"',";
                   inputQ=inputQ+"'"+arr1[x-1].charAt(0)+"',";
                   inputQ=inputQ+arr1[0]+");"; 
                   forfunc=forfunc+arr1[0]+",";
                   String names="'";
                   for(int i=1;i<=Integer.parseInt(arr1[0]);i++)
                   {
                       names=names+arr1[i]+" ";
                   }
                   names=names+"',";
                   forfunc=forfunc+names;
                   forfunc=forfunc+"'";
                   forfunc=forfunc+arr1[x-1].charAt(0)+"',";
 
                   String []output=new String[Integer.parseInt(arr1[0])];
                //    System.out.println(inputQ);
                   int canBook = -1;
                   int counter = 0;
                   int serverBusy = 0;
                   while(counter < max_try){
                        try{
                            Statement st = this.con.createStatement();
                            ResultSet rs = st.executeQuery(inputQ);
                            this.con.commit();
                            while(rs.next()){
                                canBook =rs.getInt(1);
                                // System.out.println(rs.getInt(1));
                                forfunc=forfunc+String.valueOf(canBook);
                            }
                               
                                counter = max_try;
                            }
                            catch(SQLException e){
                                this.con.rollback();
                                counter++;
                                if(counter == max_try)
                                     serverBusy =1;

                                e.printStackTrace();
                        }
                    }
                    try{
                        if(canBook>=0&&serverBusy==0){
                            forfunc=forfunc+",";
                            forfunc=forfunc+arr1[x-3]+",'"+arr1[x-2]+"');";
                            // System.out.println(forfunc);
                            Statement st = this.con.createStatement();
                            ResultSet rs = st.executeQuery(forfunc);
                            this.con.commit();
                            while(rs.next()){
                                // System.out.println(rs.getString(1));
                                dummy=rs.getString(1);
                            }
                            // System.out.println("2 " + dummy);
                            String[] arr  = dummy.split(delim);  
                            int l=arr.length;
                            int in=1;  
                            for(int i=0;i<Integer.parseInt(arr1[0]) && in<l;i++)
                            {
                                output[i]=String.valueOf(i+1)+") "+arr1[i+1]+" "+arr[in]+" "+arr[in+1];
                                in=in+2;
                            }
                            if(arr[1].charAt(0)=='A')
                            {
                                String []AC={"LB","LB","UB","UB","SL","SU"};
                                in=1;
                                for(int i=0;i<Integer.parseInt((arr1[0]));i++)
                                {
                                    int ber=Integer.parseInt((arr[in+1]));
                                    output[i]=output[i]+" "+AC[ber%6];
                                    in=in+2;
                                }
                            }
                            else
                            {
                                String []SL={"LB","MB","UB","lB","MB","UB","SL","SU"};
                                in=1;
                                for(int i=0;i<Integer.parseInt((arr1[0]));i++)
                                {
                                    int ber=Integer.parseInt((arr[in+1]));
                                    output[i]=output[i]+" "+SL[ber%8];
                                    in=in+2;
                                }
                            }

                            // String heading="#Query:";
                            String heading ="";
                            // String q=Integer.toString(query);
                            // heading=heading+q+"\n";
                            // System.out.println(heading);
                            // System.out.println(query);
                            heading=heading+"PNR: " + arr[0] + "\n" + "Train No: "+ arr1[x-3]+"\nDate Of Journey: "+arr1[x-2];
                            // System.out.println(heading);
                            responseQuery=heading; 
                            for(int i=0;i<Integer.parseInt((arr1[0]));i++)
                            {
                                responseQuery=responseQuery+"\n   ";
                                responseQuery=responseQuery+output[i];
                            }  
                            responseQuery=responseQuery+"\n\n\n\n";
                        }
                    else{
                        String heading="#Query:";
                            // String q=Integer.toString(query);
                            heading="";
                            // System.out.println(heading);
                            // System.out.println(query);
                            heading=heading+"Train No: "+ arr1[x-3]+"\nDate Of Journey: "+arr1[x-2];
                            // System.out.println(heading);
                            responseQuery=heading; 
                            responseQuery=responseQuery+"\nSeat can not be booked!\n\n\n\n";
                        // System.out.println("Seat can not be booked!");
                    }
                    // System.out.println("ended");
                }
                catch(SQLException e){
                    this.con.rollback();
                    e.printStackTrace();
                }
                
            }
                    inputQ="";
                   // for (String uniqVal1 : arr1)
                   // {
                   //     System.out.println(uniqVal1);
                   // }
 
               // inputQ
               // for (String uniqVal1 : arr1) {
               // // System.out.println(uniqVal1);
               //     inputQ=inputQ+uniqVal1;
               //     inputQ=inputQ+",";
               // }
 
               // System.out.println(queryInput);
            
 
               //-------------- your DB code goes here----------------------------
               // try
               // {
               //    // Thread.sleep(6000);
               // }
               // catch (InterruptedException e)
               // {
               //     e.printStackTrace();
               // }
 
            //    responseQuery = "******* Dummy result ******";
 
               //----------------------------------------------------------------
              
               //  Sending data back to the client
               printWriter.println(responseQuery);
               // System.out.println("\nSent results to client - "
               //                     + socketConnection.getRemoteSocketAddress().toString() );
            //   query=query+1;
           }
       }
       catch(Exception e)
       {
           return;
       }
   }
}


/**
 * Main Class to controll the program flow
 */
public class ServiceModule {
    static int serverPort = 7005;
    static int numServerCores = 2;

    // ------------ Main----------------------
    public static String getSQLcommand(String filepath){
        File file = new File(filepath);
        BufferedReader bf;
        String SQLcommand = "";
        try{
            bf = new BufferedReader(new FileReader(file));
            String line = bf.readLine();
            while(line!=null){
                SQLcommand = SQLcommand + line + "\n";
                line = bf.readLine();
            }
        }
        catch(Exception e){
            System.out.println("Error in reading the file " + filepath);
        }
        return SQLcommand;
    }
    public static void initialize_db(Connection con){
        String[] sql_files = {"book_seat.sql","book_tickets.sql","insert_train.sql","is_seat_available.sql","search_procedure.sql","table_create.sql"};
        for(int i=0;i<sql_files.length;i++){
        try{
            String getCommand = getSQLcommand("./Db_code/" + sql_files[i]);
            // System.out.println(getCommand);
            Statement st = con.createStatement();
            st.execute(getCommand);
        }
        catch(SQLException e){
            System.out.println("Error in executing sql command \n");
            e.printStackTrace();
        }
    }
      
    }
    public static void main(String[] args) throws IOException {
        // Creating a thread pool
        ExecutorService executorService = Executors.newFixedThreadPool(numServerCores);

        // Creating a server socket to listen for clients
        ServerSocket serverSocket = new ServerSocket(serverPort); // need to close the port
        Socket socketConnection = null;
        try{
            Class.forName("org.postgresql.Driver");
            String url = "jdbc:postgresql://localhost:5432/";
            String userName = "postgres";
            String passWord = "hello";
            Connection con_fr_init;
            try {
                con_fr_init = DriverManager.getConnection(url, userName, passWord);
                initialize_db(con_fr_init);
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            // Always-ON server
            while (true) {
                System.out.println("Listening port : " + serverPort
                        + "\nWaiting for clients...");
                socketConnection = serverSocket.accept(); // Accept a connection from a client
                System.out.println("Accepted client :"
                        + socketConnection.getRemoteSocketAddress().toString()
                        + "\n");
                // Create a runnable task
                    Connection con;
                    try {
                        con = DriverManager.getConnection(url, userName, passWord);
                        Runnable runnableTask = new QueryRunner(socketConnection, con);
                        // Submit task for execution
                        executorService.submit(runnableTask);
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
            } 

        }
    catch(ClassNotFoundException e){
        e.printStackTrace();
    }
}
    
}
