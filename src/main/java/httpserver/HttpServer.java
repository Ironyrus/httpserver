package httpserver;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.util.ArrayList;


//NO LONGER USED - USE HttpServer2.java
public class HttpServer implements Runnable{

    public int port;
    Socket soc;
    ServerSocket server;
    InputStream is;
    OutputStream os;
    FileInputStream image;
    String path;

    public HttpServer(int port, ServerSocket server){
        this.port = port;
        //this.soc = soc;
        this.server = server;
    } 

    @Override
    public void run() {
        try {
            while(!server.isClosed() && server.isBound()){
                System.out.println("Waiting for Client connection...");
                soc = server.accept(); //Listening for client connection. Basically, start your browser and type in http://localhost:3333/index.html            
                
                is = soc.getInputStream(); //Get input from your browser.
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br2 = new BufferedReader(isr); //Need to convert InputStream to BufferedReader because we cannot read InputStream directly.
                ArrayList<String> browserResponse = new ArrayList<>();
                
                String line = ""; //https://www.baeldung.com/java-buffered-reader
                while(!(line = br2.readLine()).isBlank()){
                    browserResponse.add(line); //Adding browser responses to an array. We are looking for /index.html and /download.png
                }
                String[] list = browserResponse.get(0).split(" ");
                for (String item : list) {
                    System.out.println("ITEM " + item);
                }

                File file = null;
                if(list[1].equals("/index.html")){
                    file = new File("index.html"); //Keep in your project folder. If using maven, keep beside pom.xml
                } else if(list[1].equals("/alt.html")){
                    file = new File("alt.html"); //Keep in your project folder. If using maven, keep beside pom.xml
                } else if(list[1].equals("/demogorgon.png")){
                    image = new FileInputStream("demogorgon.png");
                } else if(list[1].equals("/download.png")){
                    image = new FileInputStream("download.png");
                } else {
                    image = new FileInputStream("download.png");
                    file = new File("index.html");
                }

                String html = ""; //This will be the one responsible for displaying your website
                
                try {
                    FileReader fr = new FileReader(file);
                    BufferedReader br = new BufferedReader(fr);

                    String content;
                    //Takes the content of your html file and store it in the string called "html"
                    while((content = br.readLine()) != null) { //CAN USE BUFFRDRDR READLINE TO READ FROM HTML
                        html += content;
                    }
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }  



                if (list[1].equals("/index.html")){ //In browser, type http://localhost:3333/index.html
                    System.out.println("index.html");
                    os = soc.getOutputStream(); //Server sending message to browser. This includes our html content and image content.
                    //IMPORTANT: Server to browser ALWAYS follow this structure! If not this will NOT work!
                    os.write(("HTTP/1.1 200 OK \nContent-Type: text/html \n \r\n").getBytes());
                    os.write(("\r\n").getBytes());
                    os.write((html).getBytes());
                    os.write("\r\n".getBytes());
                    os.write("\r\n".getBytes());
                    os.flush();
                } else if (list[1].equals("/alt.html")){ //In browser, type http://localhost:3333/index.html
                    System.out.println("alt.html");
                    os = soc.getOutputStream(); //Server sending message to browser. This includes our html content and image content.
                    //IMPORTANT: Server to browser ALWAYS follow this structure! If not this will NOT work!
                    os.write(("HTTP/1.1 200 OK \nContent-Type: text/html \n \r\n").getBytes());
                    os.write(("\r\n").getBytes());
                    os.write((html).getBytes());
                    os.write("\r\n".getBytes());
                    os.write("\r\n".getBytes());
                    os.flush();
                }
                else if(list[1].equals("/")){
                    System.out.println("/");
                    os = soc.getOutputStream(); //Server sending message to browser. This includes our html content and image content.
                    //IMPORTANT: Server to browser ALWAYS follow this structure! If not this will NOT work!
                    os.write(("HTTP/1.1 200 OK \nContent-Type: text/html \n \r\n").getBytes());
                    os.write(("\r\n").getBytes());
                    os.write((html).getBytes());
                    os.write("\r\n".getBytes());
                    os.write("\r\n".getBytes());
                    os.flush();
                }
                else if(list[1].equals("/download.png")){ 
                    //The browser after getting hold of html code, will circle back and ask for the image. This is when we serve the image to the browser.
                    System.out.println("download.png"); //Always print to know what's going on in your program.
                    os = soc.getOutputStream();
                    os.write(("HTTP/1.1 200 OK \nContent-Type: image/png \n \r\n").getBytes());
                    os.write(("\r\n").getBytes());
                    os.write(image.readAllBytes());
                    os.flush();
                } else if(list[1].equals("/demogorgon.png")){ 
                    //The browser after getting hold of html code, will circle back and ask for the image. This is when we serve the image to the browser.
                    System.out.println("demogorgon.png"); //Always print to know what's going on in your program.
                    os = soc.getOutputStream();
                    os.write(("HTTP/1.1 200 OK \nContent-Type: image/png \n \r\n").getBytes());
                    os.write(("\r\n").getBytes());
                    os.write(image.readAllBytes());
                    os.flush();
                } else {
                    System.out.println("else");
                    os = soc.getOutputStream(); //Server sending message to browser. This includes our html content and image content.
                    //IMPORTANT: Server to browser ALWAYS follow this structure! If not this will NOT work!
                    os.write(("HTTP/1.1 200 OK \nContent-Type: text/html \n \r\n").getBytes());
                    os.write(("\r\n").getBytes());
                    os.write(("ERROR 404 NOT FOUND").getBytes());
                    os.write("\r\n".getBytes());
                    os.write("\r\n".getBytes());
                    os.flush();
                }

                br2.close();
                if(image != null)
                    image.close();
            }
            is.close();
            os.close();
            soc.close();
        } catch (Exception e) {
            e.printStackTrace();
        }    
    }

}