package org.hadi.simplewebserver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;


/**
 * @author Hadi Vahabpour Roudsari
 */
public class WebServer {

    public static void main(String[] args) {

        try {
            ServerSocket listener = new ServerSocket(8282);
           while(true) {
               Socket socket = listener.accept();
               InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
               BufferedReader reader = new BufferedReader(inputStreamReader);
               String line = reader.readLine();
               String strHttpRequest[];
               if (!line.isEmpty()) {
                   strHttpRequest = line.split("/");
                   if (strHttpRequest[0].startsWith("GET")) {
                       String contentType = "";
                       if (strHttpRequest[1].startsWith("index.html")) {
                           contentType = "index.html";
                       } else if (strHttpRequest[1].startsWith("image")) {
                           contentType = "image/jpeg";
                       }
                    Thread res=new Response(socket, contentType);
                       res.start();
                       res.join();

                   }
               }
           }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static class Response extends Thread {
        Socket socket;
        String contentType;
        public Response(Socket socket, String contentType) {
            this.socket = socket;
            this.contentType = contentType;
        }
        @Override
        public void run() {
            try {
                OutputStream os = socket.getOutputStream();
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());
                outputStreamWriter.write("HTTP/1.1 200 OK\n");
                outputStreamWriter.write("Content-Type: " + contentType + "\n");
                outputStreamWriter.write("Connection:close\n\n");
                outputStreamWriter.flush();
                if (contentType.equals("index.html")) {
                    File htmlFile = new File("index.html");
                    Files.copy(htmlFile.toPath(), os);
                }else if(contentType.equals("image/jpeg")){
                    File image = new File("testImage.jpg");
                    Files.copy(image.toPath(), os);
                }
                outputStreamWriter.close();
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}