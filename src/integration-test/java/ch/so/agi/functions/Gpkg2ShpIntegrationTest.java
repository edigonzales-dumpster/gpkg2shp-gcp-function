package ch.so.agi.functions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import io.restassured.RestAssured;

@RunWith(JUnit4.class)
public class Gpkg2ShpIntegrationTest {

    private static final String newline = System.getProperty("line.separator");
    private StringBuffer stdout = new StringBuffer();
    private StringBuffer stderr = new StringBuffer();
    
    private Process p;
    
    // TODO: Parametrisierbar und in Einklang bringen mit 'runFunction'
    // Task Properties?
    @BeforeClass
    public void setup() throws IOException, InterruptedException {
        RestAssured.port = Integer.valueOf(8080);
        RestAssured.baseURI = "http://localhost";

        String[] command = {"java", "-jar", "libs/java-function-invoker-1.0.0-beta1.jar", 
                "--classpath", "build/libs/gpkg2shp-gcp-function-all.jar",
                "--target", "ch.so.agi.functions.Gpkg2Shp"};
        
        p = Runtime.getRuntime().exec(command, null);
        appendProcessOutputToStdStreams(p, stdout, stderr);
        Thread.sleep(5000);
    }
    
    @AfterClass
    public void teardown() {
        p.destroy();
    }
    
    @Test
    public void fubar() {
        System.out.println("fubar");
        System.out.println(RestAssured.port);
        System.out.println(RestAssured.baseURI);
        
        
//        System.out.println(String.format("Here is the standard output of the command [%s]:\n", command));
        System.out.print(stdout);
//        System.out.println(String.format("Here is the standard error of the command [%s] (if any):\n", command));
        System.out.print(stderr);
        

    }
    
    private static void appendProcessOutputToStdStreams(Process p, StringBuffer stderr, StringBuffer stdout){
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
        BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

        // read the output from the command
        new Thread() {
            public void run() {
                try {
                    String s;
                    while ((s = stdInput.readLine()) != null) {
                        System.out.println(s);
                        if(stdout!=null) {
                            stdout.append(s);
                            stdout.append(newline);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

        // read any errors from the attempted command
        new Thread() {
            public void run() {
                try {
                    String s;
                    while ((s = stdError.readLine()) != null) {
                        stderr.append(s);
                        stderr.append(newline);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }    
}
