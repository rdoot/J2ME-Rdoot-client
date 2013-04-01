package hello;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;
import javax.bluetooth.*;
import javax.microedition.lcdui.*;
import javax.microedition.media.*;
import javax.microedition.media.control.*;
import javax.microedition.midlet.MIDlet;
import javax.microedition.media.control.VideoControl;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
public class HelloMIDlet extends MIDlet implements CommandListener, Runnable {

    private Display display;
    private Form form;
    private Command exit,back,capture,camera;
    private Player player;
    private VideoControl videoControl;
    private Video video;
    private UUID HELLOSERVICE_ID;
    private DiscoveryAgent mDiscoveryAgent;
    private String url = null;
    private int state = 0;
    private DataInputStream in;
    private DataOutputStream out;
    private StreamConnection conn;

    public HelloMIDlet() {

        exit = new Command("Exit", Command.EXIT, 0);
        camera = new Command("Camera", Command.SCREEN, 0);
        back = new Command("Back", Command.BACK, 0);
        capture = new Command("Capture", Command.SCREEN, 0);
        HELLOSERVICE_ID = new UUID("0000110100001000800000805F9B34FB",false);
        form = new Form("Capture Video");
        form.addCommand(camera);
        form.setCommandListener(this);
      
    }

    public void startApp() {
        display = Display.getDisplay(this);
        display.setCurrent(form);
    }

    public void pauseApp() {}

    public void destroyApp(boolean unconditional) {}

    public void commandAction(Command c, Displayable s) {
        if (c == exit) {
            destroyApp(true);
            notifyDestroyed();
        } else if (c == camera) {
            showCamera();
        } else if (c == back)
            display.setCurrent(form);
        else if (c == capture) {
            video = new Video(this);
            video.start();
        }
    }

    public void showCamera() {
        try {
            player = Manager.createPlayer("capture://video"); // "capture://video" is used for S60 devices
            //player = Manager.createPlayer("capture://image"); // "capture://image" is used for Series 40 devices
            player.realize();

            videoControl = (VideoControl)player.getControl("VideoControl");
            Canvas canvas = new VideoCanvas(this, videoControl);
        
            canvas.addCommand(back);
            canvas.addCommand(capture);
            canvas.setCommandListener(this);
            display.setCurrent(canvas);
            player.start();
         
        } catch (IOException ioe) {} catch (MediaException me) {}
    }

   

 

    public void run() {
       try {
               byte[] raw = videoControl.getSnapshot(null);
               
                   try {
                    mDiscoveryAgent = LocalDevice.getLocalDevice().getDiscoveryAgent();
                    url = mDiscoveryAgent.selectService(HELLOSERVICE_ID, ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
                    conn = (StreamConnection) Connector.open(url);
                   // in = new DataInputStream(conn.openInputStream());
                    out = new DataOutputStream(conn.openOutputStream());
                    
                  /* }catch (Exception e){ state = 0;}
               }
               else if (state == 1){
                   try {*/
                       out.write(raw.length>> 8);
                       out.write(raw.length& 0xff);
                       out.write(raw);
                       out.flush ();
                       //To be enabled later
                       /*String received = in.readUTF();
                       if(received == "Ready"){
                            out.write(raw);
                            received = in.readUTF();
                            if(received == "left"){
                                //Turn Left
                            }
                            if(received == "right"){
                                //Turn Right
                            }
                            if(received == "go"){
                                //Forward
                            }
                            if(received == "quiting"){
                                conn.close();
                                state = 0;
                            }
                       }*/
                   }catch(Exception e){

                   
               }


       }
       catch (Exception e){}//display.setCurrent(form);
                
    }

    class Video extends Thread {
        HelloMIDlet midlet;
        public Video(HelloMIDlet midlet) {
            this.midlet = midlet;
        }

        public void run() {
            captureVideo();

        }

        public void captureVideo() {
            try {
                byte[] raw = videoControl.getSnapshot(null);
               
                

            
                
            }
            catch(Exception e){
            }

        }
    };

}

