import java.util.List;
import processing.core.PImage;

final class Background
{
   private String id;
   private List<PImage> images;
   private Point position;
   private int imageIndex;

   public Background(String id, List<PImage> images, Point position)
   {
      this.id = id;
      this.images = images;
      this.position = position;
   }

   public Background(String id, List<PImage> images)
   {
      this.id = id;
      this.images = images;
   }

   public List<PImage> getImages() {
      return images;
   }

   public int getImageIndex() {
      return imageIndex;
   }

   public Point getPosition() {
      return position;
   }
}
