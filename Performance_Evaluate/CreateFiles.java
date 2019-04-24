import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import java.net.*;
class CreateFiles{
 public static void main(String[] args){
CreateFiles cf = new CreateFiles();
cf.createTextFiles();

}

public void createTextFiles()
	{
	BufferedWriter output = null;
	String filename=("/home/nikita3/Desktop/performance_eval/Dummy_Files/");    //Enter the Path TO Create 1000 files
	String text ="This is the first line of file ";
    
	for(int i=0;i<1000;i++)
	try {
		String t = String.valueOf(i);
		
        File file = new File(filename+t+".txt");
        output = new BufferedWriter(new FileWriter(file));
        output.write(text+"test"+t+".txt");
    } catch ( IOException e ) {
        e.printStackTrace();
    } finally {
        if ( output != null ) 
        	try
        {
        	output.close();
        }
        catch(Exception ex)
        {
        	System.out.println(ex.getMessage());
        }
    }
	
	}


}
