package hello;

import java.io.IOException;
import javax.microedition.lcdui.*;
import javax.microedition.media.*;
import javax.microedition.media.control.*;
import javax.microedition.midlet.MIDlet;
import javax.microedition.media.control.VideoControl;
public class HelloMIDlet extends MIDlet implements CommandListener, Runnable {

    private Display display;
    private Form form;
    private Command exit,back,capture,camera;
    private Player player;
    private VideoControl videoControl;
    private Video video;
    private Client server;
    private Bluetooth bt;
    private String msg;
    private String[] clients = new String[0];
    final String SERVICE_NAME = "rdootROV1";
final int STATE_NOT_CONNECTED = 0;
final int STATE_CONNECTING = 1;
final int STATE_CONNECTED = 2;
final int STATE_SENDING = 3;
final int STATE_SENT = 4;
final int STATE_PIC_TAKEN = 5;
private int state = STATE_NOT_CONNECTED;
    public HelloMIDlet() {

        exit = new Command("Exit", Command.EXIT, 0);
        camera = new Command("Camera", Command.SCREEN, 0);
        back = new Command("Back", Command.BACK, 0);
        capture = new Command("Capture", Command.SCREEN, 0);

        form = new Form("Capture Video");
        form.addCommand(camera);
        form.setCommandListener(this);
        bt = new Bluetooth(this); // RFCOMM
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

   

    void enqueueLibraryEvent(Bluetooth aThis, int event, Object data) {
        switch (event) {
    case Bluetooth.EVENT_DISCOVER_DEVICE:
      msg = "Found device at: " + ((Device) data).address + "...";
      break;
    case Bluetooth.EVENT_DISCOVER_DEVICE_COMPLETED:
      //msg = "Found " + length((Device[]) data) + " devices, looking for service " + SERVICE_NAME + "...";
      break;
    case Bluetooth.EVENT_DISCOVER_SERVICE:
      msg = "Found Service " + ((Service[]) data)[0].name + "...";
      break;
    case Bluetooth.EVENT_DISCOVER_SERVICE_COMPLETED:
      Service[] services = (Service[]) data;

      for (int i=0; i<services.length; i++) {
        if (services[i].name.equals(SERVICE_NAME)) {
          server = services[i].connect();
          msg = "Server connected";
          state = STATE_CONNECTED;
          return;
        }
      }

      msg = "Search complete, Server not found \nAny key to retry.";
      state = STATE_NOT_CONNECTED;
      break;
    }
    }

    void enqueueLibraryEvent(Bluetooth aThis, int EVENT_DISCOVER_DEVICE_COMPLETED, Device[] devices) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    void enqueueLibraryEvent(Bluetooth bt, int EVENT_DISCOVER_SERVICE_COMPLETED) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void run() {
        try {
                byte[] raw = videoControl.getSnapshot(null);


                //display.setCurrent(form);
                if (state == STATE_CONNECTED) {
                     state=STATE_NOT_CONNECTED; // setting it back if sending works
                     try{
                          server.writeInt(raw.length);
                          server.write(raw);
                          state = STATE_SENDING;
                        }
                    catch (Exception e) {
                    server.stop();
                    msg = "connection lost";
                }
                } }
                catch(MediaException e){

                }
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
               
                
                //display.setCurrent(form);
                if (state == STATE_CONNECTED) {
                     state=STATE_NOT_CONNECTED; // setting it back if sending works
                     try{
                          server.writeInt(raw.length);
                          server.write(raw);
                          state = STATE_SENDING;
                        }
                    catch (Exception e) {
                    server.stop();
                    msg = "connection lost";
                }
               // player.close();
               // player = null;
               // videoControl = null;
            
                }
            }
            catch(Exception e){
            }

        }
    };
}

