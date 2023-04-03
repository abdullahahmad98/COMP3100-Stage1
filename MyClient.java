import java.net.*;
import java.io.*;

class MyClient{
	public static void main(String args[])throws Exception{
		Socket s=new Socket("localhost",50000);
		DataOutputStream dout=new DataOutputStream(s.getOutputStream());
		BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
	
		// Send HELO message and receive response
		String serverResponse = sendAndReceive(dout, in, "HELO\n");
	
		// Send AUTH message with user name and receive response
		String user = System.getProperty("user.name");
		serverResponse = sendAndReceive(dout, in, "AUTH "+user+"\n");
	
		// Initialize variables for scheduling jobs
		boolean firstTime = true;
		int jobID = 0, maxCores = 0, nRecs = 0, n = 0, cores = 0;
		String maxType="";
		String serverResponse2 = "";
		String[] jobFields = null;
		
		// Loop until there are no more jobs
		while(!serverResponse.equals("NONE")){
			// Send REDY message and receive response
			serverResponse2=sendAndReceive(dout, in, "REDY\n");
			if(firstTime){
				// Send GETS All message and receive response
				serverResponse=sendAndReceive(dout, in, "GETS All\n");
				jobFields = serverResponse.split(" ");
				nRecs = Integer.parseInt(jobFields[1]);
				send(dout, "OK\n");
				// Find the server type with the maximum cores
				for(int i=0;i<nRecs;i++){
					String[] jobFields2=null;
					serverResponse=receive(in);
					jobFields2 = serverResponse.split(" ");
					cores = Integer.parseInt(jobFields2[4]);
					if(cores > maxCores){
						maxType = jobFields2[0];
						maxCores = cores;
					}
					if(maxType.equals(jobFields2[0])) n = Integer.parseInt(jobFields2[1]);
				}
				n++;
				serverResponse=sendAndReceive(dout, in, "OK\n");
				firstTime = false;
			}
	
			if(serverResponse2.contains("JOBN")){
				// Schedule the job to the server type with the maximum cores
				serverResponse=sendAndReceive(dout, in, "SCHD "+jobID+" "+maxType+" "+jobID%n+"\n");
				jobID++;
			}
			else serverResponse = serverResponse2;
		}
	
		send(dout, "QUIT\n");
		dout.close();
		s.close();
	}

	//A method to send a message and receive a response
	public static String sendAndReceive(DataOutputStream dout, BufferedReader in, String message) throws IOException {
		send(dout, message);
		return receive(in);
	}

	//A method to send a message
	public static void send(DataOutputStream dout, String message) throws IOException {
		dout.write(message.getBytes());
		dout.flush();
	}

	//A method to receive a response
	public static String receive(BufferedReader in) throws IOException {
		return in.readLine();
	}

    //A method to find the largest server type based on cores
    public static String findLargestType(BufferedReader in, int nRecs) throws IOException {
        String maxType = "";
        int maxCores = 0, n=0;
        for (int i = 0; i < nRecs; i++) {
            String[] array2 = null;
            String str = in.readLine();
            array2 = str.split(" ");
            int cores = Integer.parseInt(array2[4]);
            if (cores > maxCores) {
                maxType = array2[0];
                maxCores = cores;
            }
            if (maxType.equals(array2[0])) n = Integer.parseInt(array2[1]);
        }
        return maxType;
    }

}