package centralindexserver;

import java.io.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

class begin
{
	String filename;           //Declaration filename,peerid and ipAddress
	int peerid;
	String ipAddress;
}

class PortListener implements Runnable {

	ServerSocket server;
	Socket connection;
	BufferedReader br = null;
	Boolean flag;
	public String strVal;
	int port;
	static int maxsize = 0;
	static begin[] myIndexArray = new begin[9000];           //ArrayList Initialisation

	public PortListener(int port) {
		this.port = port;
		flag = true;//Initial Idle state
		strVal = "Awaiting for Peer Connection";
	}

	/* Beginning of Run Method */	
	public void run() {
		if(port==2001)                                  //Listening For Register on port 2001
		{
			try {
				server = new ServerSocket(2001);
				while (true) {
					connection = server.accept();			
					System.out.println(connection.getInetAddress().getHostName()+ " is making a connection for Registering the file on CentralIndexServer");    				   				
					ObjectInputStream in = new ObjectInputStream(connection.getInputStream());
					strVal = (String)in.readObject();
					System.out.println(strVal);
					System.out.println("File registered on CentralIndexServer\n");
					//Split string "strVal" using Space as Delimeter store as {peerid ,filename} format;
					String[] var;
					var = strVal.split(" ");
					int aInt = Integer.parseInt(var[0]);
					String ipstrtmp = connection.getInetAddress().getHostName();
					/* print substrings */
					for(int x = 1; x < var.length ; x++){

						//  myIndexArray[maxsize].peerid =   .;
						begin myitem = new begin();
						myitem.filename = var[x];                              //Storing Peer ID and Filename in the ArrayList       
						myitem.peerid = aInt  ;
						myitem.ipAddress = ipstrtmp;
						myIndexArray[maxsize] = myitem;
						maxsize++;
					}

					in.close();
					connection.close();   				
				}
			} 

			catch(ClassNotFoundException noclass){                                    //To Handle Exceptions for Data Received in Unsupported/Unknown Formats
				System.err.println("Data Format not supported!!!");
			}
			catch(IOException ioException){                                           //To Handle Input-Output Exceptions
				ioException.printStackTrace();
			} finally {
			}

		}
		if(port==2002)                                //Listening for Search on port 2002
		{
			try {
				server = new ServerSocket(2002);

				while (true) {
					connection = server.accept();			
					System.out.println(connection.getInetAddress().getHostName()+" is making a connection for Searching the file on CentralIndexServer");    				   				
					ObjectInputStream in = new ObjectInputStream(connection.getInputStream());
					strVal = (String)in.readObject();
					String retval = "";
					//	Peer-id's separated by space are returned for given file

					for (int idx =0; idx < maxsize ;idx++)                                             //Traversing the ArrayList  
					{                
						if (myIndexArray[idx].filename.equals(strVal))                             //To Compare the filename with the Registered filenames in the ArrayList
						{
							retval = retval + myIndexArray[idx].peerid + "("+myIndexArray[idx].ipAddress +")\n\r ";                  //Returns the list of Peerid's which has the searched file      
						}	
					} 
					if (retval == "") 
					{
						retval = "Cannot find the file!!!\n";
					} 
					System.out.println(retval);
					System.out.println("File Searched on CentralIndexServer\n");

					ObjectOutputStream out = new ObjectOutputStream(connection.getOutputStream());
					out.flush();			
					out.writeObject(retval);                        //Write the List of peer id's to the output stream
					out.flush();			
					in.close();
					out.close();
					connection.close();   				
				}
			} 

			catch(ClassNotFoundException noclass){                                      //To Handle Exceptions for Data Received in Unsupported/Unknown Formats 
				System.err.println("Data Format not supported!!! ");
			}
			catch(IOException ioException){                                             //To Handle Input-Output Exceptions
				ioException.printStackTrace();
			} finally {
			}

		}		
	}
}


/*CentralIndxServer Class Begin*/
public class CentralIndexServer {

	public CentralIndexServer() {
		RegisterRequestThread();                           //RegisterRequest and SearchRequest Threads
		SearchRequestThread();
	}

	public static void main(String[] args) {

		System.out.println("|----------------------Napster Peer To Peer File Sharing System----------------------|");
		System.out.println("\n\n Starting the Indexing Server...........");
                System.out.println("\n Indexing Server started successfully and is ready for incoming connections.\n\n");
        	CentralIndexServer mainFrame = new CentralIndexServer();

	}
	public void RegisterRequestThread()
	{
		Thread rthread = new Thread (new PortListener(2001));                     //Register Request Thread
		rthread.setName("Hear for the registration request");
		rthread.start();
	}
	public void SearchRequestThread()
	{
		Thread sthread = new Thread (new PortListener(2002));                    //Search Request Thread
		sthread.setName("Hear for the searching request");
		sthread.start();

	}
}
