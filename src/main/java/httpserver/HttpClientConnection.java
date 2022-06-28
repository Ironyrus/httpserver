package httpserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class HttpClientConnection {
    
    String html = ""; //This will be the one responsible for displaying your website
    FileInputStream image;
    InputStream is;
    OutputStream os;
    Socket soc;
    String[] paths;
    String path;
    ArrayList<File> resources = new ArrayList<>();

    public HttpClientConnection(Socket s, String[] paths){
        this.paths = paths;

        try {
            this.soc = s;
            is = s.getInputStream();
            os = s.getOutputStream();    
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public HttpClientConnection(Socket s, String path){
        this.path = path;
        try {
            this.soc = s;
            is = s.getInputStream();
            os = s.getOutputStream(); 
            this.path = path;           
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void readFiles(){
        File file = new File(path + "index.html"); //Keep in your project folder. If using maven, keep beside pom.xml
        
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);

            String content;
            //Takes the content of your html file and store it in the string called "html"
            while((content = br.readLine()) != null) { //CAN USE BUFFRDRDR READLINE TO READ FROM HTML
                html += content;
            }
            fr.close();
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void respond2() throws IOException{
        is = soc.getInputStream(); //Get input from your browser.
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br2 = new BufferedReader(isr); //Need to convert InputStream to BufferedReader because we cannot read InputStream directly.
        ArrayList<String> browserResponse = new ArrayList<>();
        
        String line = ""; //https://www.baeldung.com/java-buffered-reader
        while(!(line = br2.readLine()).isBlank()){
            browserResponse.add(line); //Adding browser responses to an array. We are looking for /index.html and /download.png
        }
        String[] list = browserResponse.get(0).split(" ");

        File dirPath = null;
        File dir = null;
        String[] dirList = null;
        File file = null;
        String[] dirList2 = null;
        if(Main.morePaths){
            if(list[1].equals("/")){
                image = new FileInputStream(Main.path + "download.png");
                file = new File(Main.path + "index.html");
            }
            for (String item : paths) {
                dirPath = new File(item);
                dirList = dirPath.list();
                for (String eafile : dirList) {
                    if(list[1].equals("/" + eafile)){
                        if(list[1].contains("html")){
                            if(list[1].equals("/" + eafile)){
                                file = new File(item + eafile);
                                dirList2 = dirPath.list();
                                break;
                            }
                        } else if(list[1].contains("png")){
                            if(list[1].equals("/" + eafile)){
                                image = new FileInputStream(item + eafile);
                                file = new File(Main.path + "index.html"); //Ensures FileReader fr does not throw exception, since if browser fetches png this program will still ask for html file.
                                dirList2 = dirPath.list();
                                break;
                            }
                        } 
                    }
                }
            }
        } else{
            dir = new File(path);
            dirList = dir.list();
            for (String item : dirList) {
                if(list[1].contains("html")){
                    if(list[1].equals("/" + item)){
                        file = new File(path + item);
                        dirList2 = dir.list();
                    }
                } else if(list[1].contains("png")){
                    if(list[1].equals("/" + item)){
                        image = new FileInputStream(path + item);
                        file = new File(path + "index.html");
                        dirList2 = dir.list();
                    }
                } else {
                        image = new FileInputStream(path + "download.png");
                        file = new File(path + "index.html");
                }
            }
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

        if(!(list[0].equals("GET"))){
            os = soc.getOutputStream(); //Server sending message to browser. This includes our html content and image content.
            //IMPORTANT: Server to browser ALWAYS follow this structure! If not this will NOT work!
            os.write(("HTTP/1.1 405 Method Not Allowed\r\n").getBytes());
            os.write(("\r\n").getBytes());
            os.write((list[0] + " not supported").getBytes());
            os.write("\r\n".getBytes());
            os.write("\r\n".getBytes());
            os.flush();
            soc.close();
        }

        //NEW
        if(dirList2 != null){
            for(String res : dirList2) {
                if(list[1].equals("/")){
                    System.out.println("/");
                    os = soc.getOutputStream(); //Server sending message to browser. This includes our html content and image content.
                    //IMPORTANT: Server to browser ALWAYS follow this structure! If not this will NOT work!
                    os.write(("HTTP/1.1 200 OK \nContent-Type: text/html \n \r\n").getBytes());
                    os.write(("\r\n").getBytes());
                    os.write((html).getBytes());
                    os.write("\r\n".getBytes());
                    os.write("\r\n".getBytes());
                    os.flush();
                    break;
                }
                if(list[1].equals("/" + res)){
                    if(res.contains("html")){
                        System.out.println(res);
                        os = soc.getOutputStream(); //Server sending message to browser. This includes our html content and image content.
                        //IMPORTANT: Server to browser ALWAYS follow this structure! If not this will NOT work!
                        os.write(("HTTP/1.1 200 OK \nContent-Type: text/html \n \r\n").getBytes());
                        os.write(("\r\n").getBytes());
                        os.write((html).getBytes());
                        os.write("\r\n".getBytes());
                        os.write("\r\n".getBytes());
                        os.flush();
                        break;
                    } else if(res.contains("png")){
                        //The browser after getting hold of html code, will circle back and ask for the image. This is when we serve the image to the browser.
                        System.out.println(res); //Always print to know what's going on in your program.
                        os = soc.getOutputStream();
                        os.write(("HTTP/1.1 200 OK \nContent-Type: image/png \n \r\n").getBytes());
                        os.write(("\r\n").getBytes());
                        os.write(image.readAllBytes());
                        os.flush();
                        break;
                    }
                } else if(list[1].equals("/")){
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
            }
        } else if(list[1].equals("/")){
            System.out.println("/");
            os = soc.getOutputStream(); //Server sending message to browser. This includes our html content and image content.
            //IMPORTANT: Server to browser ALWAYS follow this structure! If not this will NOT work!
            os.write(("HTTP/1.1 200 OK \nContent-Type: text/html \n \r\n").getBytes());
            os.write(("\r\n").getBytes());
            os.write((html).getBytes());
            os.write("\r\n".getBytes());
            os.write("\r\n".getBytes());
            os.flush();
        } else {
            System.out.println("else");
            os = soc.getOutputStream(); //Server sending message to browser. This includes our html content and image content.
            //IMPORTANT: Server to browser ALWAYS follow this structure! If not this will NOT work!
            os.write(("HTTP/1.1 404 NOT FOUND\r\n").getBytes());
            os.write(("\r\n").getBytes());
            os.write(("<html><h1 style=\"text-align:center\">" + list[1] + " not found </h1><p style=\"text-align:center\">Please try again!</p></html>").getBytes());
            os.write("\r\n".getBytes());
            os.write("\r\n".getBytes());
            os.flush();
        }

        //Close all resources.
        soc.close();
        os.close();
        is.close();
        br2.close();

        if(image != null)
            image.close();
    }
}