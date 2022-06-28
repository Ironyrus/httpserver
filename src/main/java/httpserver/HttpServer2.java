package httpserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class HttpServer2 implements Runnable{
    
    public int port;
    Socket soc;
    ServerSocket server;
    InputStream is;
    OutputStream os;
    FileInputStream image;
    String path;
    String[] paths;

    public HttpServer2(int port, ServerSocket server, String path){
        this.port = port;
        this.server = server;
        this.path = path;
    } 

    public HttpServer2(int port, ServerSocket server, String[] paths){
        this.port = port;
        this.server = server;
        this.paths = paths;
    } 

    @Override
    public void run() {
        try {
            while(!server.isClosed() && server.isBound()){
                System.out.println("Waiting for Client connection...");
                soc = server.accept(); //Listening for client connection. Basically, start your browser and type in http://localhost:3333/index.html            

                if(Main.morePaths){
                    boolean dirExists = false;
                    for(int i = 0; i < paths.length; i++) {
                        System.out.println("CHECK2 " + paths[i]);
                        File dir = new File(paths[i]);
                        System.out.println("CHECK3 " + paths[i] + dir.isDirectory());
                        if(!dir.isDirectory()){
                            System.out.println(paths[i] + " is not a directory.");
                        } else {
                            dirExists = true;
                        }
                    }
                    System.out.println("At least one dir exists: " + dirExists);
                    ArrayList<String> oldPaths = new ArrayList<String>();
                    for(int i = 0; i < paths.length; i++){
                        File dir = new File(paths[i]);
                        if (dir.isDirectory()){
                            oldPaths.add(paths[i]);
                        }    
                    }
                    
                    if(dirExists == false){
                        System.out.println("Directories do not exist");
                        System.exit(1);
                    }
                    
                    String[] newPaths = new String[oldPaths.size()];
                    for(int i = 0; i < oldPaths.size(); i++){
                        System.out.println(oldPaths.get(i));
                        newPaths[i] = oldPaths.get(i);
                    }
                    HttpClientConnection con = new HttpClientConnection(soc, newPaths);
                    con.respond2();
                } else {
                    HttpClientConnection con = new HttpClientConnection(soc, path);
                    con.respond2();
                }
                    
            }
            is.close();
            os.close();
            soc.close();
        } catch (Exception e) {
            e.printStackTrace();
        }    
    }
}