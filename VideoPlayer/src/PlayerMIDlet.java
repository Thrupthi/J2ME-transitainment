import javax.microedition.lcdui.*;
import javax.microedition.media.*;
import javax.microedition.media.control.*;
import javax.microedition.midlet.*;

public class PlayerMIDlet extends MIDlet implements
  CommandListener, PlayerListener, Runnable {
   private Display display;
   private Form form;
   private TextField url;
   private Command start = new Command("Play",
     Command.SCREEN, 1);
   private Command stop = new Command("Stop",
     Command.SCREEN, 2);
   private Player player;

   public PlayerMIDlet() {
      display = Display.getDisplay(this);
      form = new Form("Demo Player");
      url = new TextField("Enter URL:", "http://java.sun.com/products/java-media/mma/media/test-mpeg.mpg", 100,
	    TextField.URL);
      //http://java.sun.com/products/java-media/mma/media/test-wav.wav
      //http://java.sun.com/products/java-media/mma/media/test-mpeg.mpg
      form.append(url);
      form.addCommand(start);
      form.addCommand(stop);
      form.setCommandListener(this);
      display.setCurrent(form);
   }

   protected void startApp() {
      try {
         if(player != null && player.getState() ==
		   Player.PREFETCHED) {
            player.start();
         } else {
            defplayer();
            display.setCurrent(form);
         }
      }
      catch(MediaException me) {
         reset();
      }
   }

   protected void pauseApp() {
      try {
         if(player != null && player.getState() ==
		   Player.STARTED) {
            player.stop();
         } else {
            defplayer();
         }
      }
      catch(MediaException me) {
         reset();
      }
   }

   protected void destroyApp(
      boolean unconditional) {
      form = null;
      try {
         defplayer();
      }
      catch(MediaException me) {
      }
   }

   public void playerUpdate(Player player,
     String event, Object data) {
      if(event == PlayerListener.END_OF_MEDIA) {
         try {
            defplayer();
         }
         catch(MediaException me) {
         }
         reset();
      }
   }

   public void commandAction(Command c, Displayable d) {
      if(c == start) {
         start();
      } else if(c == stop) {
         stopPlayer();
      }
   }

   public void start() {
      Thread t = new Thread(this);
      t.start();
   }

   // to prevent blocking, all communication should
   // be in a thread
   // and not in commandAction
   public void run() {
      play(getURL());
   }

   String getURL() {
     return url.getString();
   }

   void play(String url) {
      try {
         VideoControl vc;
         defplayer();
         // create a player instance
         player = Manager.createPlayer(url);
         player.addPlayerListener(this);
         // realize the player
         player.realize();
         vc = (VideoControl)player.getControl(
		   "VideoControl");
         if(vc != null) {
            Item video = (Item)vc.initDisplayMode(
			  vc.USE_GUI_PRIMITIVE, null);
            Form v = new Form("Playing Video...");
            StringItem si = new StringItem("Status: ",
			  "Playing...");
            v.append(si);
            v.append(video);
            display.setCurrent(v);
         }
         player.prefetch();
         player.start();
      }
      catch(Throwable t) {
         reset();
      }
   }

   void defplayer() throws MediaException {
      if (player != null) {
         if(player.getState() == Player.STARTED) {
            player.stop();
         }
         if(player.getState() == Player.PREFETCHED) {
            player.deallocate();
         }
         if(player.getState() == Player.REALIZED ||
		    player.getState() == Player.UNREALIZED) {
            player.close();
         }
      }
      player = null;
   }

   void reset() {
      player = null;
   }

   void stopPlayer() {
      try {
         defplayer();
      }
      catch(MediaException me) {
      }
      reset();
   }
}