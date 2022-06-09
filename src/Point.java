import java.util.ArrayList;
import java.util.List;

final class Point
{
   public final int x;
   public final int y;

   public Point(int x, int y)
   {
      this.x = x;
      this.y = y;
   }

   public String toString()
   {
      return "(" + x + "," + y + ")";
   }

   public boolean equals(Object other)
   {
      return other instanceof Point &&
         ((Point)other).x == this.x &&
         ((Point)other).y == this.y;
   }

   public int hashCode()
   {
      int result = 17;
      result = result * 31 + x;
      result = result * 31 + y;
      return result;
   }

   /**
    * Point
    * Determines if 2 points are adjacent
    *
    * @param other
    * @return
    */
   public boolean adjacent(Point other)
   {
      return (x == other.x && Math.abs(y - other.y) == 1) ||
              (y == other.y && Math.abs(x - other.x) == 1);
   }

   /**
    * Point
    * Determines if 2 points are adjacent
    *
    * @param other
    * @return
    */
   public boolean diagonallyAdjacent(Point other)
   {
      return (Math.abs(y - other.y) <= 1) &&
              (Math.abs(x - other.x) <= 1);
   }

   /**
    * Point
    * Converts the squared linear distance between two points
    * @param other
    * @return
    */
   public int distanceSquared(Point other)
   {
      int deltaX = x - other.x;
      int deltaY = y - other.y;

      return deltaX * deltaX + deltaY * deltaY;
   }



   public List<Point> getLocsAround(){
      List<Point> list = new ArrayList<>();
      list.add(new Point(x, y));

      list.add(new Point(x+1, y + 1));
      list.add(new Point(x + 1, y));
      list.add(new Point(x +1, y -1));
      list.add(new Point(x, y -1));
      list.add(new Point(x -1, y -1));
      list.add(new Point(x -1, y));
      list.add(new Point(x, y + 1));
      list.add(new Point(x -1, y +1 ));
      return list;
   }
}
