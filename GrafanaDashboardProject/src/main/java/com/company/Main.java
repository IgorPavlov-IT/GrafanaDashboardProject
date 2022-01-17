package com.company;
                                                                                                                        // Here I add temporary some rare icons for quick copying
import org.json.JSONArray;                                                                                              // \\
import org.json.JSONObject;                                                                                             //  < < > >

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class Main {

    private static HttpURLConnection connection;                  //Not required in Method 2                            // We define the connection here.
    public static void main(String[] args) {

        // Getting Unique ID. Method 1 : User input (via Command Line/Console)
        Scanner scanner = new Scanner(System.in);                                                                       // We utilise a Scanner class, to get input from user (for uid)
        System.out.println("Please enter the Dashboard uid:   then press ENTER");                                       // Console line to inform user to give input.
        String uid = scanner.nextLine();                                                                                // Takes user input (must not have any white spaces)
        System.out.println("Please wait a couple seconds");                                                             // Informing user that program is running. (Optional)

        // Getting Unique ID. Method 2 : (hard-coding when the uid is a constant or rarely changes)                     // more hard-coded approach of inputting an uid.
        // String uid2 = "unique";                                                                                      // replace the word unique with uid, leaving the " " icons.

        // Getting Unique ID  Method 3 : (reading from File, unpractical)                                               // I bring out this method, but it's unfinished, I don't think
                                                                                                                        // that it is viable for the current purposes.

        // Query Method 1: (older method, longer code, needs to be async-ed manually)
                                                                  //TODO Start of Method 1
        BufferedReader reader;                                                                                          // Used to read data from (in our case Grafana website)
        String lineTemporary;                                                                                           // Temporary variable to separate lines to preserve structure
        StringBuffer replyAnswer = new StringBuffer();                                                                  // This will contain a list of all data sources.
        StringBuffer datasource = new StringBuffer();
        int lineCounter = 0;


        try {                                                                                                           // surrounded our code with try/catch block to handle exceptions/errors.
            URL url = new URL("https://play.grafana.org/api/dashboards/uid/" + uid.trim());                        // Grafana dashboard URL with uid (removing whitespaces)
            //URL url = new URL("https://jsonplaceholder.typicode.com/albums");                                           // temporary URL for testing
            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);                                                                         // 5 s timeout on connecting (5000 milliseconds)
            connection.setReadTimeout(5000);                                                                            // 5 s timeout on reading (5000 milliseconds)
                                                                                                                        // we create timeouts to avoid permanent connection loop
            int status = connection.getResponseCode();
            if (status >= 200 && status < 300) {                                                                        // status 200 means everything is OK
                System.out.println("Connection successful");
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));                        // we assign a reader to read the data from connection (body)
                while ((lineTemporary = reader.readLine())!= null){                                                     // loop line-by-line for as long as there is a line.
                    replyAnswer.append(lineTemporary);                                                                  // we append (attach) String to our StringBuffer
                    replyAnswer.append(System.lineSeparator());                                                         // we append (attach) line spacing to our StringBuffer for aesthetics
                    lineCounter++;                                                                                      // we count the number of lines in our response (for later purposes)
                }
                reader.close();                                                                                         // we close/stop reader process.
            } else if (status >= 400 && status < 405 ) {                                                                // most common error section, we get error message in console and we can...
                System.out.println("Error with connection");                                                            // ... make custom actions upon receiving these errors.
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));                        // .getErrorStream -) getInputStream
                while ((lineTemporary = reader.readLine())!= null){
                    replyAnswer.append(lineTemporary);
                    replyAnswer.append(System.lineSeparator());                                                         // Gives error code in console

                }
                reader.close();
            } else {                                                                                                    // this is to cover all "other" status codes, which are less common.
                //System.out.println("Unknown error");
            }
            //System.out.println(replyAnswer.toString());                                                               // our Output by printing to system console.
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }
                                                                    //TODO End of Method 1

        // Query Method 2: (newer method, short, a-sync is done automatically, requires JDK 11+ )
        /*                                                          //TODO Start of Method 2
        HttpClient client = HttpClient.newHttpClient();
        //HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://play.grafana.org/api/dashboards/uid/" + uid.trim())).build();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://jsonplaceholder.typicode.com/albums")).build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(System.out::println)
                .join();
        */                                                          //TODO End of Method 2

        // Query Method 3: (REST Controller, simple to add to and expand, also requires more "recent" versions of JDK?)     // This would be required if we used POST, DELETE & other HTTP functions.

        // Output Method 1: (Writing to file, in the temporary file we write/save everything)                                // This I would "usually" put into "if connection successful"-loop
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("temp.txt"));                             // temp.txt file in our project folder will be where we write our temporary output.
            writer.write( replyAnswer + System.lineSeparator());                                                    // here we give the writer info that we write full output to the file.
            writer.close();                                                                                             // we close writer, otherwise our file will remain empty.
            System.out.println("Writing to file successful");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Sort out Output Data Method 1: (Searching through written file for the lines we need)                        // We wrote the whole Dashboard info into file so now we can sort out what we need.

        String title = null;                                                                                            // We declare the variables outside the conditions and loops,
        String url = null;                                                                                              // so we could use them later (in final output JSON creation).
        String folderName = null;

        try {
            BufferedReader reader2 = new BufferedReader(new FileReader("temp.txt"));                            // We read the temporary file temp.txt and filter out info
            String tempLine;                                                                                            // Temporary line variable for storing lines
            while ((tempLine = reader2.readLine()) != null) {                                                           // We keep doing the loop below until no more lines are left
                if (tempLine.contains("\"title\":")) {                                                                  // If the current line contains "title":, then we do the operation below.
                    int a = tempLine.indexOf(":");                                                                      // We find index of :
                    String tempLine2 = tempLine.substring(a + 3);                                                       // Then we take create another temporary string that starts only after : icon.
                    title = tempLine2.substring(0, (tempLine2.length()-2));                                             // We cut off the last characters " and , and we assign the string as a title.
                } else if (tempLine.contains("\"url\":")) {                                                             // --- Repeat ---
                    int a = tempLine.indexOf(":");                                                                      // Note: This is a very primitive, but hopefully fool-proof? way of finding the required value.
                    String tempLine2 = tempLine.substring(a + 3);
                    url = tempLine2.substring(0, (tempLine2.length()-2));                                               // We assign the URL.
                } else if (tempLine.contains("\"folderName\":")) {                                                      // --- Repeat ---
                    int a = tempLine.indexOf(":");
                    String tempLine2 = tempLine.substring(a + 3);
                    folderName = tempLine2.substring(0, (tempLine2.length() - 2));                                      // We assign the folder name.
                }
                if (tempLine.contains("\"datasource\":")) {                                                             // Complicated double-conditional double-nested-loop
                    boolean isTheEnd = false;                                                                           // Below part is RAW and is mostly guesswork since I don't have code to test on.
                    while (isTheEnd = false) {                                                                          // The code would need to be adjusted depending on the structure/layout of the JSON.
                        if (tempLine.trim().equals("]")) {
                            isTheEnd = true;                                                                            // Simply described, I attempt to find keyword "datasource" and pick out all of the names
                        } else {                                                                                        // and append (add) them to the datasource String. This is a very sub-optimal solution, but
                            for (int i = 0 ; i < 1 ; i++ ) {                                                            // it might work, considering I have currently no idea how the JSON structure looks like.
                                int a = tempLine.indexOf(":");
                                String tempLine2 = tempLine.substring(a + 3);
                                datasource.append(tempLine);
                            }
                            datasource.append(tempLine);                                                                // String that lists all datasources.
                        }
                    }

                }
            }
            reader2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        // Here we create the final JSON (output) Method 1: (using maven JSON jar library/method)

        JSONObject obj = new JSONObject();                                                                              // we create a JSON object called obj
        obj.put("uid" , uid);                                                                                         // we put in all the data under correct keywords
        obj.put("title", title );
        obj.put("url", url);
        obj.put("folderName", folderName);
        obj.put("dataSources", datasource );

        // Here we have to output/export/write the created JSON into a file.

        try (FileWriter file = new FileWriter ("myJSON.json")) {
            file.write(obj.toString());
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
/*        public static void parse (String responseBody) {
            JSONArray DashboardData = new JSONArray(responseBody);
                for (int i = 0; i < DashboardData.length(); i++ ) {
                    JSONObject Dashboard = DashboardData.getJSONObject(i);
                    String uid = Dashboard.getString("uid");
                    String title = Dashboard.getString("title");
                    String url = Dashboard.getString("url");
                    String folderName = Dashboard.getString("folderName");
                    JSONArray dataSources = Dashboard.getJSONArray("dataSources");

                }
                */
        }

    }

