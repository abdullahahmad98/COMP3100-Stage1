import java.net.*;
import java.io.*;

class MyClient {
    public static void main(String args[]) throws Exception {
        Socket socket = new Socket("localhost", 50000);
        DataOutputStream output = new DataOutputStream(socket.getOutputStream());
        BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        
        // Connect to the server and authenticate
        String serverResponse = sendAndReceive(output, input, "HELO\n");
        String user = System.getProperty("user.name");
        serverResponse = sendAndReceive(output, input, "AUTH " + user + "\n");
        
        // Initialize some variables for scheduling jobs
        int jobID = 0;
        int cores = 0;
        int maxCores = 0;
        int nRecs = 0;
        int n = 0;
        String maxType = "";
        String serverResponse2 = "";
        String[] jobFields = null;
        boolean firstTime = true;
        
        // Loop until there are no more jobs
        while (!serverResponse.equals("NONE")) {
            // Request a job from the server
            serverResponse2 = sendAndReceive(output, input, "REDY\n");
            
            // If this is the first time, get all the jobs and find the type with the most cores
            if (firstTime) {
                serverResponse = sendAndReceive(output, input, "GETS All\n");
                jobFields = serverResponse.split(" ");
                nRecs = Integer.parseInt(jobFields[1]);
                send(output, "OK\n");
                
                // Loop through all the jobs and compare their cores
                for (int i = 0; i < nRecs; i++) {
                    String[] jobFields2 = null;
                    serverResponse = receive(input);
                    jobFields2 = serverResponse.split(" ");
                    cores = Integer.parseInt(jobFields2[4]);
                    if (cores > maxCores) {
                        maxType = jobFields2[0];
                        maxCores = cores;
                    }
                    if (maxType.equals(jobFields2[0]))
                        n = Integer.parseInt(jobFields2[1]);
                }
                n++;
                serverResponse = sendAndReceive(output, input, "OK\n");
                firstTime = false;
            }
            
            // If the server sent a new job, schedule it to the type with the most cores
            if (serverResponse2.contains("JOBN")) {
                serverResponse = sendAndReceive(output, input,
                        "SCHD " + jobID + " " + maxType + " " + jobID % n + "\n");
                jobID++;
            } else
                serverResponse = serverResponse2;
        }
        
        // Quit the connection
        send(output, "QUIT\n");
        output.close();
        socket.close();
    }

    // Helper method to send a message to the server and receive a response
    public static String sendAndReceive(DataOutputStream output, BufferedReader input, String message)
            throws IOException {
        send(output, message);
        return receive(input);
    }

    // Helper method to send a message to the server
    public static void send(DataOutputStream output, String message) throws IOException {
        output.write(message.getBytes());
        output.flush();
    }

    // Helper method to receive a message from the server
    public static String receive(BufferedReader input) throws IOException {
        return input.readLine();
    }
}