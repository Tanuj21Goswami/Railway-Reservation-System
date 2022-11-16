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
class QueryRunner implements Runnable
{
   //  Declare socket for client access
   protected Socket socketConnection;
   private Connection con;
   public QueryRunner(Socket clientSocket,Connection con){
       this.socketConnection =  clientSocket;
       this.con  = con;
       
   }
   public void run()
   {
     try{        
            this.con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);  
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
 
           while(true)
           {
               // Read client query
               clientCommand = bufferedInput.readLine();
               StringTokenizer tokenizer = new StringTokenizer(clientCommand);
               queryInput = tokenizer.nextToken();
               int len=queryInput.length();
               if(len==4)
               {
                   String delimSpace = "[ ]+";
                   String[] arr1  = clientCommand.split(delimSpace);
                   inputQ=inputQ+"SELECT * from insert_train(";
                   inputQ=inputQ+arr1[0]+",";
                   inputQ=inputQ+arr1[3]+",";
                   inputQ=inputQ+arr1[2]+",";
                   inputQ=inputQ+"'"+arr1[1]+"');";
                   System.out.println(inputQ);
                   try{
                        this.con.setAutoCommit(false);
                        try {
                            Statement st = this.con.createStatement();
                            st.executeQuery(inputQ);
                            System.out.println("query executed");
                            this.con.commit();
                        } catch (SQLException e) {
                            // TODO Auto-generated catch block
                            this.con.rollback();
                            e.printStackTrace();
                        }
                    }
                    catch(SQLException e){
                        System.out.println("Error in setting auto commit to false");
                    }
                   inputQ="";
               }
               else if(len<4)
               {
                //    System.out.println(pnr);
                   String forfunc ="";
                //    String output="";
                   String dummy="";
                   forfunc=forfunc+"SELECT * from book_tickets(";
                
                   inputQ=inputQ+"SELECT * from is_seat_available(";
                   String delimSpace = "[, ]+";
                   String[] arr1  = clientCommand.split(delimSpace);
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
                   System.out.println(inputQ);
                   try{
                       this.con.setAutoCommit(false);
                   try {
                       Statement st = this.con.createStatement();
                       ResultSet rs = st.executeQuery(inputQ);
                       int canBook = 0;
                       while(rs.next()){
                           canBook =rs.getInt(1);
                           System.out.println(rs.getInt(1));
                           forfunc=forfunc+String.valueOf(canBook);
                        }
                        if(canBook>=0){
                                forfunc=forfunc+",";
                                forfunc=forfunc+arr1[x-3]+",'"+arr1[x-2]+"');";
                                System.out.println(forfunc);
                                try{
                                        st = this.con.createStatement();
                                        rs = st.executeQuery(forfunc);
                                        while(rs.next()){
                                            System.out.println(rs.getString(1));
                                            dummy=rs.getString(1);
                                        }
                                    String[] arr  = dummy.split(delimSpace);  
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

                                    String heading="";
                                    heading="PNR: " + arr[0] + "\n" + "Train No: "+ arr1[x-3]+"\nDate Of Journey: "+arr1[x-2];
                                    System.out.println(heading); 
                                    for(int i=0;i<Integer.parseInt((arr1[0]));i++)
                                    {
                                        System.out.println(output[i]);
                                    }
                                    this.con.commit();
                                }
                                catch (SQLException e) {
                                    // TODO Auto-generated catch block
                                    this.con.rollback();
                                    e.printStackTrace();
                                }
                        }
                    else{
                        System.out.println("Seat can not be booked!");
                    }
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        this.con.rollback();
                        e.printStackTrace();
                    }
                }
                catch(SQLException e){
                    System.out.println("Error in setting auto commit to false");
                }
                    inputQ="";
                   // for (String uniqVal1 : arr1)
                   // {
                   //     System.out.println(uniqVal1);
                   // }
 
               }
               // inputQ
               // for (String uniqVal1 : arr1) {
               // // System.out.println(uniqVal1);
               //     inputQ=inputQ+uniqVal1;
               //     inputQ=inputQ+",";
               // }
 
               // System.out.println(queryInput);
               if(queryInput.equals("Finish"))
               {
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
 
               //-------------- your DB code goes here----------------------------
               // try
               // {
               //    // Thread.sleep(6000);
               // }
               // catch (InterruptedException e)
               // {
               //     e.printStackTrace();
               // }
 
               responseQuery = "******* Dummy result ******";
 
               //----------------------------------------------------------------
              
               //  Sending data back to the client
               printWriter.println(responseQuery);
               // System.out.println("\nSent results to client - "
               //                     + socketConnection.getRemoteSocketAddress().toString() );
              
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
public class ServiceModule
{
   static int serverPort = 7005;
   static int numServerCores = 2 ;
   //------------ Main----------------------
   public static void main(String[] args) throws IOException
   {   
       // Creating a thread pool
       ExecutorService executorService = Executors.newFixedThreadPool(numServerCores);
      
       //Creating a server socket to listen for clients
       ServerSocket serverSocket = new ServerSocket(serverPort); //need to close the port
       Socket socketConnection = null;
      
       // Always-ON server
       while(true)
       {
           System.out.println("Listening port : " + serverPort
                               + "\nWaiting for clients...");
           socketConnection = serverSocket.accept();   // Accept a connection from a client
           System.out.println("Accepted client :"
                               + socketConnection.getRemoteSocketAddress().toString()
                               + "\n");
           //  Create a runnable task
           try {
               Class.forName("org.postgresql.Driver");
               String url = "jdbc:postgresql://localhost:5432/railway";
               String userName = "postgres";       
               String passWord = "hello";
               Connection con;
               try {
                   con = DriverManager.getConnection(url,userName,passWord);
                   Runnable runnableTask = new QueryRunner(socketConnection,con);
                   //  Submit task for execution  
                   executorService.submit(runnableTask);
               } catch (SQLException e) {
                   // TODO Auto-generated catch block
                   e.printStackTrace();
               }
           } catch (ClassNotFoundException e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
           }
          
       }
   }
}
