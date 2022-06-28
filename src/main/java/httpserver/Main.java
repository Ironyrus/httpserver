package httpserver;

/*
mvn archetype:generate -DgroupId=httpserver -DartifactId=sdfassessment -DarchetypeArtifactId=maven-archetype-quickstart -DarchetypeVersion=1.4 -DinteractiveMode=false
mvn archetype:generate -DgroupId=minesweeper -DartifactId=miniproj -DarchetypeArtifactId=maven-archetype-quickstart -DarchetypeVersion=1.4 -DinteractiveMode=false
git add . (add ALL content of cart to github)
git commit -m "While Loop"                  (add comment while committing)
git push origin main                        (push to main branch)

mvn compile exec:java -Dexec.mainClass="httpserver.Main"

javac src/main/java/minesweeper/minesweeper.java

vans_@LAPTOP-AS886SBL MINGW64 ~/VISA NUS-ISS VTTP/testing/miniproj/src/main/java (main)
$ javac minesweeper/minesweeper.java


----------------------------------- pom.xml ---------------------------------------------
        <plugin>
          <artifactId>maven-jar-plugin</artifactId>
          <version>3.0.2</version>
            <configuration>
              <archive>
                <manifest>
                  <mainClass>minesweeper.minesweeper</mainClass>
                </manifest>
              </archive>
            </configuration>
        </plugin>
----------------------------------------------------------------------------------------

 mvn compile exec:java -Dexec.mainClass="httpserver.Main" -Dexec.args="'args 1' 'args 2'"
 mvn compile exec:java -Dexec.mainClass="httpserver.Main" -Dexec.args="'--docRoot' './test/:./static/:./rando/' '--port' '3333'"
 */

import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main 
{
    public static int port;
    public static boolean morePaths = false;
    public static String path = ".\\static\\"; //Directory defaults to ./static/ if not specified in Command Line

    public static void main( String[] args ) { 
      port = 3000; //Port defaults to 3000 if not specified in Command Line
      String[] paths = null;
        if (args.length != 0){
          for(int i = 0; i < args.length; i++){
            System.out.println("args[i]" + args[i]);
            if(args[i].equals("--docRoot")){
              if(args[i+1].contains(":")){
                morePaths = true;
                paths = args[i+1].split(":");
              } else{
                path = args[i+1];
              }
            } 
            if(args[i].equals("--port")) {
              //mvn compile exec:java -Dexec.mainClass="httpserver.Main" -Dexec.args="'--docRoot' ' ./rand/:./test/:./stati/' '--port' '3333'"
              port = Integer.parseInt(args[i+1]);
              System.out.println("PORT: " + port);
              }
            }
          }
        ExecutorService threadPool = Executors.newFixedThreadPool(10);
        try {
          ServerSocket server = new ServerSocket(port);
          //while(true){
              HttpServer2 thr;
              System.out.println("Start Browser Up...");
              //Socket soc = server.accept();
              if(morePaths){
                thr = new HttpServer2(port, server, paths);
              }else{
                thr = new HttpServer2(port, server, path);
              }
              threadPool.submit(thr);
           // }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}