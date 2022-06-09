import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import processing.core.*;

/*
VirtualWorld is our main wrapper
It keeps track of data necessary to use Processing for drawing but also keeps track of the necessary
components to make our world run (eventScheduler), the data in our world (WorldModel) and our
current view (think virtual camera) into that world (WorldView)
 */

public final class VirtualWorld
   extends PApplet
{
   private static final int TIMER_ACTION_PERIOD = 100;

   private static final int VIEW_WIDTH = 640;
   private static final int VIEW_HEIGHT = 480;
   private static final int TILE_WIDTH = 32;
   private static final int TILE_HEIGHT = 32;
   private static final int WORLD_WIDTH_SCALE = 2;
   private static final int WORLD_HEIGHT_SCALE = 2;

   private static final int VIEW_COLS = VIEW_WIDTH / TILE_WIDTH;
   private static final int VIEW_ROWS = VIEW_HEIGHT / TILE_HEIGHT;
   private static final int WORLD_COLS = VIEW_COLS * WORLD_WIDTH_SCALE;
   private static final int WORLD_ROWS = VIEW_ROWS * WORLD_HEIGHT_SCALE;

   private static final String IMAGE_LIST_FILE_NAME = "imagelist";
   private static final String DEFAULT_IMAGE_NAME = "background_default";
   private static final int DEFAULT_IMAGE_COLOR = 0x808080;

   private static final String LOAD_FILE_NAME = "world.sav";

   private static final String FAST_FLAG = "-fast";
   private static final String FASTER_FLAG = "-faster";
   private static final String FASTEST_FLAG = "-fastest";
   private static final double FAST_SCALE = 0.5;
   private static final double FASTER_SCALE = 0.25;
   private static final double FASTEST_SCALE = 0.10;

   private static final String STRAW_HAT_KEY = "strawhat";
   private static final String CHOPPER_KEY = "chopper";


   private static final String TURTLE_KEY = "thing";
   private static final String TURTLE_ID_SUFFIX = " -- thing";
   public static final int TURTLE_ACTION_PERIOD_MIN = 1500;
   public static final int TURTLE_ACTION_PERIOD_MAX = 2000;
   public static final int TURTLE_ANIMATION_MIN = 50;
   public static final int TURTLE_ANIMATION_MAX = 100;



   private static double timeScale = 1.0;

   private ImageStore imageStore;
   private WorldModel world;
   private WorldView view;
   private EventScheduler scheduler;

   private long next_time;

   public void settings()
   {
      size(VIEW_WIDTH, VIEW_HEIGHT);
   }

   /*
      Processing entry point for "sketch" setup.
   */
   public void setup()
   {
      this.imageStore = new ImageStore(
         createImageColored(TILE_WIDTH, TILE_HEIGHT, DEFAULT_IMAGE_COLOR));
      this.world = new WorldModel(WORLD_ROWS, WORLD_COLS,
         createDefaultBackground(imageStore));
      this.view = new WorldView(VIEW_ROWS, VIEW_COLS, this, world,
         TILE_WIDTH, TILE_HEIGHT);
      this.scheduler = new EventScheduler(timeScale);

      loadImages(IMAGE_LIST_FILE_NAME, imageStore, this);
      loadWorld(world, LOAD_FILE_NAME, imageStore);

      scheduleActions(world, scheduler, imageStore);

      next_time = System.currentTimeMillis() + TIMER_ACTION_PERIOD;
   }

   public void draw()
   {
      long time = System.currentTimeMillis();
      if (time >= next_time)
      {
         this.scheduler.updateOnTime(time);
         next_time = time + TIMER_ACTION_PERIOD;
      }

      view.drawViewport();
   }



   public void keyPressed()
   {
      if (key == CODED)
      {
         int dx = 0;
         int dy = 0;

         switch (keyCode) {
            case UP:
               dy = -1;
               break;
            case DOWN:
               dy = 1;
               break;
            case LEFT:
               dx = -1;
               break;
            case RIGHT:
               dx = 1;
               break;
         }
         view.shiftView(dx, dy);
      }
   }

    public void mousePressed() {
        spawnStrawHatFlag();
    }

    public void spawnStrawHatFlag(){
        int xpos = view.getViewport().getCol();
        int ypos = view.getViewport().getRow();


        Point pressed = new Point(  xpos+ mouseX/TILE_WIDTH, ypos + mouseY/TILE_HEIGHT);
        List<Point> list= pressed.getLocsAround();

        list.forEach(p -> {
            world.setBackground(new Background(STRAW_HAT_KEY, imageStore.getImageList(STRAW_HAT_KEY), p));});

        Random rand = new Random();

        list.forEach(p -> {
            if(world.isOccupied(p) && world.getOccupant(p).get() instanceof Octo){
                Entity crab = new Crab(CHOPPER_KEY,    //Change from entity to crab
                        p,
                        8000 / Fish.CRAB_PERIOD_SCALE,
                        Fish.CRAB_ANIMATION_MIN + rand.nextInt(Fish.CRAB_ANIMATION_MAX - Fish.CRAB_ANIMATION_MIN + 20),
                        imageStore.getImageList(CHOPPER_KEY)
                );
                world.removeEntity(world.getOccupant(p).get());
                world.addEntity(crab);
                ((Crab) crab).scheduleActions(scheduler, world, imageStore);

            } else if(!world.isOccupied(p)) {
               Entity turtle = new Turtle(p.toString() + TURTLE_ID_SUFFIX,
                       p,
                       TURTLE_ACTION_PERIOD_MIN + rand.nextInt(TURTLE_ACTION_PERIOD_MAX-TURTLE_ACTION_PERIOD_MIN),
                       TURTLE_ANIMATION_MIN + rand.nextInt(TURTLE_ANIMATION_MAX-TURTLE_ANIMATION_MIN),
                       imageStore.getImageList(TURTLE_KEY));
               world.addEntity(turtle);
               ((Turtle) turtle).scheduleActions(scheduler, world, imageStore);
            }
        });

    }



   public static Background createDefaultBackground(ImageStore imageStore)
   {
      return new Background(DEFAULT_IMAGE_NAME,
              imageStore.getImageList(DEFAULT_IMAGE_NAME));
   }

   public static PImage createImageColored(int width, int height, int color)
   {
      PImage img = new PImage(width, height, RGB);
      img.loadPixels();
      for (int i = 0; i < img.pixels.length; i++)
      {
         img.pixels[i] = color;
      }
      img.updatePixels();
      return img;
   }

   private static void loadImages(String filename, ImageStore imageStore,
      PApplet screen)
   {
      try
      {
         Scanner in = new Scanner(new File(filename));
         imageStore.loadImages(in, screen);
      }
      catch (FileNotFoundException e)
      {
         System.err.println(e.getMessage());
      }
   }

   private static void loadWorld(WorldModel world, String filename,
      ImageStore imageStore)
   {
      try
      {
         Scanner in = new Scanner(new File(filename));
         world.load(in, imageStore);
      }
      catch (FileNotFoundException e)
      {
         System.err.println(e.getMessage());
      }
   }

   public static void scheduleActions(WorldModel world,
      EventScheduler scheduler, ImageStore imageStore)
   {
      for (Entity entity : world.getEntities())
      {
         //Only start actions for entities that include action (not those with just animations)
         if (entity instanceof Active && ! (entity instanceof Atlantis))
            ((Active) entity).scheduleActions(scheduler, world, imageStore);
      }
   }

   public static void parseCommandLine(String [] args)
   {
      for (String arg : args)
      {
         switch (arg)
         {
            case FAST_FLAG:
               timeScale = Math.min(FAST_SCALE, timeScale);
               break;
            case FASTER_FLAG:
               timeScale = Math.min(FASTER_SCALE, timeScale);
               break;
            case FASTEST_FLAG:
               timeScale = Math.min(FASTEST_SCALE, timeScale);
               break;
         }
      }
   }

   public static void main(String [] args)
   {
      parseCommandLine(args);
      PApplet.main(VirtualWorld.class);
   }
}
