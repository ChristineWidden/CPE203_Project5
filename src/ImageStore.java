import java.util.*;

import processing.core.PApplet;
import processing.core.PImage;

//import javax.swing.text.html.parser.Entity;

/*
ImageStore: to ideally keep track of the images used in our virtual world
 */

final class ImageStore
{

   private static final int COLOR_MASK = 0xffffff;
   private static final int KEYED_IMAGE_MIN = 5;
   private static final int KEYED_RED_IDX = 2;
   private static final int KEYED_GREEN_IDX = 3;
   private static final int KEYED_BLUE_IDX = 4;


   private Map<String, List<PImage>> images;
   private List<PImage> defaultImages;

   public ImageStore(PImage defaultImage)
   {
      this.images = new HashMap<>();
      defaultImages = new LinkedList<>();
      defaultImages.add(defaultImage);
   }

   /**
    * ImagesStore // ambiguous. Shouldn't really belong to either Entity or Background. ImagesStore chosen due to relation to images.
    * Get the appropriate image for the given entity
    * ie
    * @param entity
    * @return
    */
   public static PImage getCurrentImage(Object entity)
   {
      if (entity instanceof Background)
      {
         return ((Background)entity).getImages()
                 .get(((Background)entity).getImageIndex());
      }
      else if (entity instanceof Entity)
      {
         return ((Entity)entity).getImages().get(((Entity)entity).getImageIndex());
      }
      else
      {
         throw new UnsupportedOperationException(
                 String.format("getCurrentImage not supported for %s",
                         entity));
      }
   }


   /**
    * ImageStore
    * Grabs the list of images for the given key
    * @param key
    * @return
    */
   public List<PImage> getImageList(String key)
   {
      return images.getOrDefault(key, defaultImages);
   }

   /**
    * ImageStore
    * Loads the images
    * @param in
    * @param screen
    */
   public void loadImages(Scanner in, PApplet screen)
   {
      int lineNumber = 0;
      while (in.hasNextLine())
      {
         try
         {
            processImageLine(images, in.nextLine(), screen);
         }
         catch (NumberFormatException e)
         {
            System.out.println(String.format("Image format error on line %d",
                    lineNumber));
         }
         lineNumber++;
      }
   }

   /**
    * ImageStore// no strong ties to anything,
    * but only gets accessed by getImageList(), which is tied to ImageStore
    * Processes image line?
    * @param images
    * @param line
    * @param screen
    */
   private static void processImageLine(Map<String, List<PImage>> images,
                                       String line, PApplet screen)
   {
      String[] attrs = line.split("\\s");
      if (attrs.length >= 2)
      {
         String key = attrs[0];
         PImage img = screen.loadImage(attrs[1]);
         if (img != null && img.width != -1)
         {
            List<PImage> imgs = getImages(images, key);
            imgs.add(img);

            if (attrs.length >= KEYED_IMAGE_MIN)
            {
               int r = Integer.parseInt(attrs[KEYED_RED_IDX]);
               int g = Integer.parseInt(attrs[KEYED_GREEN_IDX]);
               int b = Integer.parseInt(attrs[KEYED_BLUE_IDX]);
               setAlpha(img, screen.color(r, g, b), 0);
            }
         }
      }
   }

   /**
    * ImageStore // Same logic as processImageLine()
    * Gets the images for the given key
    * @param images
    * @param key
    * @return
    */
   private static List<PImage> getImages(Map<String, List<PImage>> images,
                                        String key)
   {
      List<PImage> imgs = images.get(key);
      if (imgs == null)
      {
         imgs = new LinkedList<>();
         images.put(key, imgs);
      }
      return imgs;
   }

   /*
     Called with color for which alpha should be set and alpha value.
     setAlpha(img, color(255, 255, 255), 0));
   */
   /**
    * ImageStore // same logic as processImageLine()
    * Sets the alpha for the given image
    * @param img
    * @param maskColor
    * @param alpha
    */
   private static void setAlpha(PImage img, int maskColor, int alpha)
   {
      int alphaValue = alpha << 24;
      int nonAlpha = maskColor & COLOR_MASK;
      img.format = PApplet.ARGB;
      img.loadPixels();
      for (int i = 0; i < img.pixels.length; i++)
      {
         if ((img.pixels[i] & COLOR_MASK) == nonAlpha)
         {
            img.pixels[i] = alphaValue | nonAlpha;
         }
      }
      img.updatePixels();
   }

}
