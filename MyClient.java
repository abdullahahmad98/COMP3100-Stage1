import java.net.*;  
import java.io.*;  

class MyClient{  
    public static void main(String args[])throws Exception{  
    Socket s=new Socket("localhost",50000);  
    
     DataInputStream din=new DataInputStream(s.getInputStream());  
    DataOutputStream dout=new DataOutputStream(s.getOutputStream());  
    BufferedReader in =new BufferedReader(new InputStreamReader(s.getInputStream()));  
      
    	String str = "";
	
	
    	dout.write(("HELO\n").getBytes()); 
    	dout.flush();
    	System.out.println("HELO has been sent");
    	str = in.readLine();
    	System.out.println("Server says: "+ str);
    	
    	dout.write(("AUTH Abdullah\n").getBytes()); 
    	dout.flush();
    	System.out.println("Auth has been sent");
    	str = in.readLine();
    	System.out.println("Server says: "+ str);
  
    	dout.write(("REDY\n").getBytes()); 
    	dout.flush();
    	System.out.println("REDY has been sent");
    	str = in.readLine();
    	System.out.println("Server says: "+ str);
    	
    	//Remove the first 4 words of the server's response.
    	int index = str.indexOf(" ");
    	for (int i=0; i<3; i++){
    		index = str.indexOf(" ", index + 1);
    	}
    	String jobn = str.substring(index + 1);
    	
    	dout.write(("GETS Capable " + jobn + "\n").getBytes()); 
    	dout.flush();
    	System.out.println("GETS has been sent");
    	str = in.readLine();
    	System.out.println("Server says: "+ str);

		index = str.indexOf(" ");
		int startIndex = index;
		index = str.indexOf(" ", index + 1);
		int endIndex = index;
		String numberServers = str.substring(startIndex + 1, endIndex);


    	dout.write(("OK\n").getBytes()); 
    	dout.flush();
    	System.out.println("OK has been sent");

		int numServers = Integer.parseInt(numberServers); 
		String strArr[] = new String[numServers];
		String str2dArr[][] = new String[numServers][];
		for (int i=0; i < numServers; i++) {
    		str = in.readLine();
			strArr[i] = str;
    		System.out.println("Server says: "+ str);
		}
		for (int i=0; i < numServers; i++) {
			str2dArr[i] = strArr[i].split(" ");
		}

		/*
		for (int i=0; i < str2dArr.length; i++) {
			for (int j=0; j < str2dArr[i].length; j++) {
				System.out.print(str2dArr[i][j] + " ");
			}
			System.out.println();
		}
		*/

    	dout.write(("OK\n").getBytes()); 
    	dout.flush();
    	System.out.println("OK has been sent");
    	str = in.readLine();
    	System.out.println("Server says: "+ str);
  
    	dout.write(("REDY\n").getBytes()); 
    	dout.flush();
    	System.out.println("REDY has been sent");
    	str = in.readLine();
    	System.out.println("Server says: "+ str);
  
    	dout.write(("SCHD 0 medium 0\n").getBytes()); 
    	dout.flush();
    	System.out.println("SCHD has been sent");
    	str = in.readLine();
    	System.out.println("Server says: "+ str);
    	
    	dout.write(("QUIT\n").getBytes()); 
    	dout.flush();
    	System.out.println("Quit has been sent");
    	str = in.readLine();
    	System.out.println("Server says: "+ str);
      
    dout.close();  
    s.close();  
    }}  