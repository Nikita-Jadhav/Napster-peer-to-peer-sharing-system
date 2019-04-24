import java.io.*;
import java.net.*;
import java.util.Date;
import java.util.Scanner;

class PortListenerSend implements Runnable 
{

	int port;
	public String strVal;
	Boolean flag;                            //declarations
	ServerSocket server;
	Socket connection;
	BufferedReader br = null;

	public PortListenerSend(int port) 
	{
		this.port = port;
		flag = true;//Initial Idle state
		strVal = "Awaiting for Peer Connection";
	}
	/* Beginning of Run Method */
	public void run() 
	{
		try {
			server = new ServerSocket(port);

			while (true) 
			{                                                                       //Listen for Download request
				connection = server.accept();			
				System.out.println(connection.getInetAddress().getHostName()+" is making a connection for Downloading the file\n");    				   				
				ObjectInputStream in = new ObjectInputStream(connection.getInputStream());
				strVal = (String)in.readObject();

				ObjectOutputStream out = new ObjectOutputStream(connection.getOutputStream());
				out.flush();		

				String str="";

				try
				{
					FileReader fr = new FileReader(strVal);                 //Reads the filename into Filereader
					BufferedReader br = new BufferedReader(fr);		
					String value=new String();
					while((value=br.readLine())!=null)                //Appending the content read from the BufferedReader object until it is null and stores it in str
						str=str+value+"\r\n";                       
					br.close();
					fr.close();
				} catch(Exception e)
				{
					System.out.println("Unable to open the file");
				}

				out.writeObject(str);
				out.flush();
				in.close();
				connection.close();   				
			}
		     } 

		catch(ClassNotFoundException noclass)
		{                                            //To Handle Exception for Data Received in Unsupported or Unknown Formats
			System.err.println("Data Format not supported");
		}
		catch(IOException ioException)
		{                                                   //To Handle Input-Output Exceptions
			ioException.printStackTrace();
		} 
		finally 
		{
		}
	}
}

/* PeerServer Class Begin */
public class PeerServer {

	//public String CIS_ip = "10.0.2.15";       //============>IP-address of the CentralIndxServer has to be specified here
	public String CIS_ip = "192.168.56.1";       //============>IP-address of the CentralIndxServer has to be specified here
	public String Clientid = "1001";
	String regmessage,searchfilename;
	ObjectOutputStream out;
	Socket requestSocket;

	public PeerServer() {

		System.out.println("\n|----------------------------------Napster Peer to Peer File Sharing----------------------------------|\n\n");
		
		try
		{
			FileReader fr = new FileReader("indxip.txt");//read the filename in to filereader object    
			String val1=new String();
			BufferedReader br = new BufferedReader(fr);	
			val1 = br.readLine();
			System.out.println("For CentralIndexServer IP-\n" + val1);
			CIS_ip = val1;
			br.close();
			fr.close();
		} catch(Exception e){
			System.out.println("Unable to read the CentralIndexServer IP from indxip.txt");
		}

		System.out.println("\nChoose the operation to perform from the following list:\n");
		
		while (true){

			System.out.println("1) Register the File on CentralIndexserver\n");
			System.out.println("2) Search/Lookup for the File\n");
			System.out.println("3) Download the File from the Peers on the network\n");
			System.out.println("4) Exit the Napster Peer to Peer File Sharing\n");

			Scanner in = new Scanner(System.in);
			regmessage = in.nextLine();
			if (regmessage.equals("1")){

				System.out.println("Enter the Peer ID (4 digit) along with the file you want to register with a space amongst the two");
				regmessage = in.nextLine();
				String[] val;
				val = regmessage.split(" ");                        //Split the peerid and filename separated with a space
				int PearPort  = Integer.parseInt(val[0]);			
				RegisterWithIServer();                          //Register Method call
				AttendFileDownloadRequest(PearPort);
					
			}		
			if (regmessage.equals("2")){
				SearchWithIServer();                            //Search Method call
			}
			if (regmessage.equals("3")){
				DownloadFromPeerServer();                       //Download Method call 
			}
			if (regmessage.equals("4")){
				System.out.println("Exiting Napster Peer to Peer File Sharing.\n");
				System.exit(0);   		
			}

		}
	}

	public static void main(String[] args) {

		PeerServer psFrame = new PeerServer();

	}
	public void RegisterWithIServer()                             //Register with CentralIndxServer Method
	{
		try{
			//1. Creating a socket to connect to the server
			requestSocket = new Socket(CIS_ip, 2001);
			System.out.println("\n"+CIS_ip+" Connected with CentralIndexServer on port 2001\n");
			//2. To Get Input and Output streams
			out = new ObjectOutputStream(requestSocket.getOutputStream());
			out.flush();			
			out.writeObject(regmessage);
			out.flush();
			System.out.println(CIS_ip+" Registered Successfully\n");
		}
		catch(UnknownHostException unknownHost){                                             //To Handle Unknown Host Exception
			System.err.println("Unable to Connect to an Unknown Host!");
		}
		catch(IOException ioException){                                                      //To Handle Input-Output Exception
			ioException.printStackTrace();
		} 
		finally{
			//4: Closing connection
			try{
				out.close();
				requestSocket.close();
			}
			catch(IOException ioException){
				ioException.printStackTrace();
			}
		}

	}
	public void SearchWithIServer()                              //Search on the CentralIndexServer Method
	{
		try{
			System.out.println("Enter the name of the file for the lookup:");
			Scanner in1 = new Scanner(System.in);                                        //Takes Input from the Peer to search the desired file
			searchfilename = in1.nextLine();

			//1. Creating a socket to connect to the Index server
			requestSocket = new Socket(CIS_ip, 2002);
			System.out.println("\n"+CIS_ip+" Connected with CentralIndexServer on port 2002\n");
			//2. To Get Input and Output streams
			out = new ObjectOutputStream(requestSocket.getOutputStream());
			out.flush();			
			out.writeObject(searchfilename);                                            //Writes the Search Filename to the Output Stream
			out.flush();
			ObjectInputStream in = new ObjectInputStream(requestSocket.getInputStream());
			String strVal = (String)in.readObject();
			//  For File Not Found Print Condition 
			if  (strVal.equals("File cannot be found\n")) {

				System.out.println("FILE Does Not Exist\n");
			}
			else {
				System.out.println( "The file named '"+searchfilename+ "' can be found at peer:"+strVal+"\n");     
			}		

		}
		catch(UnknownHostException unknownHost){                                           //To Handle Unknown Host Exception
			System.err.println("Unable to connect to an Unknown Host!");
		}
		catch(IOException ioException){                                                    //To Handle Input-Output Exception
			ioException.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		finally{
			//4: Closing connection
			try{
				out.close();
				requestSocket.close();
			}
			catch(IOException ioException){
				ioException.printStackTrace();
			}
		}
	}

	public void writetoFile(String s)
	{
		try
		{  
			//To Append String s to Existing File
			String fname = searchfilename;
			FileWriter fw = new FileWriter(fname,true);
			fw.write(s);                                      //Write to file, the contents
			fw.close();

		} catch(Exception e){
			System.out.println("");
			
		}

	}

	public void DownloadFromPeerServer()                            //Download Function Method 
	{

		System.out.println("Enter Peer ID:");                       
		Scanner in1 = new Scanner(System.in);                       //Takes from the user the 4Digit Peer ID as input 
		String peerid = in1.nextLine();

		System.out.println("Enter IP Address of the Peer where the file exists:");
		String ipadrs = in1.nextLine();
		System.out.println("Enter the name of the file to be downloaded:");      
		searchfilename = in1.nextLine();                              //Takes from user the desired filename to be downloaded

		int peerid1 = Integer.parseInt(peerid);
		try{

			//1. Creating a socket to connect to the Index server
			requestSocket = new Socket(ipadrs, peerid1);
			//System.out.println("\nNow we are connected to peerID : "+peerid1+"\n");
			//2. To Get Input and Output streams
			out = new ObjectOutputStream(requestSocket.getOutputStream());
			out.flush();			
			out.writeObject(searchfilename);
			out.flush();
			ObjectInputStream in = new ObjectInputStream(requestSocket.getInputStream());
			String strVal = (String)in.readObject();
			System.out.println( searchfilename+": has now been downloaded on this peer\n");
			writetoFile(strVal);
		}
		catch(UnknownHostException unknownHost){                                             //To Handle Unknown Host Exception
			System.err.println("Unable to connect to an unknown host!");
		}
		catch(IOException ioException){                                                      //To Handle Input-Output Exception

			System.err.println("FILE cannot be found at this peer");      
			System.err.println("Please enter your peer ID again:");      // To Avoid StackTrace Print on Console and Inform User
			DownloadFromPeerServer();                    // Calling Download Function Again to enable user to enter valid Filename and Port Number
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		finally{
			//4: Closing connection
			try{
				//	in.close();
				out.close();
				requestSocket.close();
			}
			catch(IOException ioException){
				ioException.printStackTrace();
			}
		}
	}
	public void AttendFileDownloadRequest(int peerid)                                //FileDownload Request Thread   
	{
		Thread dthread = new Thread (new PortListenerSend(peerid));
		dthread.setName("AttendFileDownloadRequest");
		dthread.start();
	}
}
