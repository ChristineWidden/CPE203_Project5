/*
Viewport ideally helps control what part of the world we are looking at for drawing only what we see
Includes helpful helper functions to map between the viewport and the real world
 */


final class Viewport
{
   private int row;
   private int col;
   private int numRows;
   private int numCols;

   public Viewport(int numRows, int numCols)
   {
      this.numRows = numRows;
      this.numCols = numCols;
   }

   /**
    * Viewport
    * Shifts the view
    * @param col
    * @param row
    */
   public void shift(int col, int row)
   {
      this.col = col;
      this.row = row;
   }

   /**
    * Viewport
    * Determines if the given point is within the current view
    * @param p
    * @return
    */
   public boolean contains(Point p)
   {
      return p.y >= row && p.y < row + numRows &&
              p.x >= col && p.x < col + numCols;
   }

   /**
    * Viewport
    * Returns the point for a position in the world from a position in the viewport
    * @param col
    * @param row
    * @return
    */
   public Point viewportToWorld(int col, int row)
   {
      return new Point(col + this.col, row + this.row);
   }

   /**
    * Viewport
    * Returns the point for a position in the viewport from a position in the world
    * @param col
    * @param row
    * @return
    */
   public Point worldToViewport(int col, int row)
   {
      return new Point(col - this.col, row - this.row);
   }


   public int getRow() {
      return row;
   }

   public int getCol() {
      return col;
   }

   public int getNumRows() {
      return numRows;
   }

   public int getNumCols() {
      return numCols;
   }
}
