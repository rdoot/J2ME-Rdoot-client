package hello;

import java.io.IOException;
import javax.microedition.lcdui.*;
import javax.microedition.media.*;
import javax.microedition.media.control.*;
import javax.microedition.midlet.MIDlet;
import javax.microedition.media.control.VideoControl;
public class HelloMIDlet extends MIDlet implements CommandListener {

    private Display display;
    private Form form;
    private Command exit,back,capture,camera;
    private Player player;
    private VideoControl videoControl;
    private Video video;
    private Device dev;
    private Client cl;
    public HelloMIDlet() {

        exit = new Command("Exit", Command.EXIT, 0);
        camera = new Command("Camera", Command.SCREEN, 0);
        back = new Command("Back", Command.BACK, 0);
        capture = new Command("Capture", Command.SCREEN, 0);

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

    void enqueueLibraryEvent(Bluetooth aThis, int EVENT_DISCOVER_DEVICE, Device device) {
         Device d = device;
        //Record r = null;
        cl.device = d;
       
    }

    void enqueueLibraryEvent(Bluetooth aThis, int EVENT_DISCOVER_SERVICE, Service[] data) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    void enqueueLibraryEvent(Bluetooth aThis, int EVENT_DISCOVER_DEVICE_COMPLETED, Device[] devices) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    void enqueueLibraryEvent(Bluetooth bt, int EVENT_DISCOVER_SERVICE_COMPLETED) {
        throw new UnsupportedOperationException("Not yet implemented");
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
                Image image = Image.createImage(raw, 0, raw.length);
                form.append(image);
                display.setCurrent(form);

                player.close();
                player = null;
                videoControl = null;
            } catch (MediaException me) { }
        }
    };
}

