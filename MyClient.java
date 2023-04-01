import java.net.*;  
import java.io.*;  
class MyClient{  
public static void main(String args[])throws Exception{  
	Socket s=new Socket("localhost",50000);  //port  
	DataOutputStream dout=new DataOutputStream(s.getOutputStream());  
	BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));

	String str="",str2="";
	dout.write(("HELO\n").getBytes());
	dout.flush(); 
	str=in.readLine();
	String username = System.getProperty("user.name");
	dout.write(("AUTH "+username+"\n").getBytes());
	dout.flush(); 
	str=in.readLine();

	boolean state = true;
	int jobID = 0, maxCores = 0, nRecs = 0, n = 0, cores = 0;
	String maxType="";
	String[] Array = null;
	while(!str.equals("NONE")){
		dout.write(("REDY\n").getBytes());
		dout.flush();
		str2=in.readLine();
		if(state){
			dout.write(("GETS All\n").getBytes());
			dout.flush();
			str=in.readLine();
			Array = str.split(" ");
			nRecs = Integer.parseInt(Array[1]);
			dout.write(("OK\n").getBytes());
			dout.flush();
			for(int i=0;i<nRecs;i++){
				String[] Array2=null;
				str=in.readLine();
				Array2 = str.split(" ");
				cores = Integer.parseInt(Array2[4]);
				if(cores > maxCores){
					maxType = Array2[0];
					maxCores = cores;
				}
				if(maxType.equals(Array2[0])) n = Integer.parseInt(Array2[1]);
			}
			n++;
			dout.write(("OK\n").getBytes());
			dout.flush();
			str=in.readLine();
			state = false;
		}

		if(str2.contains("JOBN")){
			dout.write(("SCHD "+jobID+" "+maxType+" "+jobID%n+"\n").getBytes());
			dout.flush();
			jobID++;
			str=in.readLine();
		}
		else str = str2;
	}

	dout.write(("QUIT\n").getBytes());
	dout.flush();
	dout.close(); 
	s.close();
}}