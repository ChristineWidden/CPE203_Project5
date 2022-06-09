import processing.core.PImage;

import java.util.*;

/*
WorldModel ideally keeps track of the actual size of our grid world and what is in that world
in terms of entities and background elements
 */

final class WorldModel
{

   private static final int FISH_REACH = 1;



   private int numRows;
   private int numCols;
   private Background[][] background;
   private Entity[][] occupancy;
   private Set<Entity> entities;

   public WorldModel(int numRows, int numCols, Background defaultBackground)
   {
      this.numRows = numRows;
      this.numCols = numCols;
      this.background = new Background[numRows][numCols];
      this.occupancy = new Entity[numRows][numCols];
      this.entities = new HashSet<>();

      for (int row = 0; row < numRows; row++)
      {
         Arrays.fill(this.background[row], defaultBackground);
      }
   }


   /**
    * WorldModel // nothing gets accessed from WorldModel,
    * but this function seems fit for the class dedicated to the locations of objects.
    * Finds an open space around a point within a fish's reach
    * @param pos point to check around
    * @return Point an open space
    */
   Optional<Point> findOpenAround(Point pos)
   {
      for (int dy = -FISH_REACH; dy <= FISH_REACH; dy++)
      {
         for (int dx = -FISH_REACH; dx <= FISH_REACH; dx++)
         {
            Point newPt = new Point(pos.x + dx, pos.y + dy);
            if (withinBounds(newPt) &&
                    !isOccupied(newPt))
            {
               return Optional.of(newPt);
            }
         }
      }

      return Optional.empty();
   }


   /**
    * WorldModel
    * Loads world from saved file
    * @param in
    * @param imageStore
    */
   void load(Scanner in, ImageStore imageStore)
   {
      int lineNumber = 0;
      while (in.hasNextLine())
      {
         try
         {
            //if (!processLine(in.nextLine(), imageStore))
            Object parseResult = Parser.processLine(in.nextLine(), imageStore);
            if (parseResult == null)
            {
               System.err.println(String.format("invalid entry on line %d",
                       lineNumber));
            }else{
               if(parseResult instanceof Entity) {
                  tryAddEntity((Entity) parseResult);
               } else if(parseResult instanceof Background) {
                  setBackground((Background) parseResult);
               }
            }
         }
         catch (NumberFormatException e)
         {
            System.err.println(String.format("invalid entry on line %d",
                    lineNumber));
         }
         catch (IllegalArgumentException e)
         {
            System.err.println(String.format("issue on line %d: %s",
                    lineNumber, e.getMessage()));
         }
         lineNumber++;
      }
   }



   /**
    * WorldModel
    * Tries to add entity to the entity's position
    * throws exception if position is occupied
    * @param entity
    */
   private void tryAddEntity(Entity entity)
   {
      if (isOccupied(entity.getPosition()))
      {
         // arguably the wrong type of exception, but we are not
         // defining our own exceptions yet
         throw new IllegalArgumentException("position occupied");
      }

      addEntity(entity);
   }

   /**
    * WorldModel
    * Returns if a point is within the bounds of the world
    * @param pos
    * @return
    */
   boolean withinBounds(Point pos)
   {
      return pos.y >= 0 && pos.y < numRows &&
              pos.x >= 0 && pos.x < numCols;
   }

   /**
    * WorldModel
    * Returns if a point in the world is occupied
    * @param pos
    * @return
    */
   boolean isOccupied(Point pos)
   {
      return withinBounds(pos) &&
              getOccupancyCell(pos) != null;
   }

   /**
    * WorldModel
    * Returns the entity nearest to the given point
    * @param entities the list of entities to check
    * @param pos
    * @return
    */
   private Optional<Entity> nearestEntity(List<Entity> entities,
                                                 Point pos)
   {
      if (entities.isEmpty())
      {
         return Optional.empty();
      }
      else
      {
         Entity nearest = entities.get(0);
         int nearestDistance = nearest.getPosition().distanceSquared(pos);

         for (Entity other : entities)
         {
            int otherDistance = other.getPosition().distanceSquared(pos);

            if (otherDistance < nearestDistance)
            {
               nearest = other;
               nearestDistance = otherDistance;
            }
         }

         return Optional.of(nearest);
      }
   }


   /**
    * WorldModel
    * Returns the nearest entity of the given kind to the given point
    * @param pos
    * @param kind The kind of entity to look for
    * @return
    */
   Optional<Entity> findNearest(Point pos,
                                       Class kind)
   {
      List<Entity> ofType = new LinkedList<>();
      for (Entity entity : entities)
      {
         if (entity.getClass() == kind)
         {
            ofType.add(entity);
         }
      }

      return nearestEntity(ofType, pos);
   }

   /*
      Assumes that there is no entity currently occupying the
      intended destination cell.
   */
   /**
    * WorldModel
    * Adds an entity to the entity's location
    * @param entity
    */
   void addEntity(Entity entity)
   {
      if (withinBounds(entity.getPosition()))
      {
         setOccupancyCell(entity.getPosition(), entity);
         entities.add(entity);
      }
   }

   /**
    * Removes an entity from its old position (entity.position)
    * and adds it to its new position (pos)
    * @param entity Entity
    * @param pos Point
    */
   void moveEntity(Entity entity, Point pos)
   {
      Point oldPos = entity.getPosition();
      if (withinBounds(pos) && !pos.equals(oldPos))
      {
         setOccupancyCell(oldPos, null);
         removeEntityAt(pos);
         setOccupancyCell(pos, entity);
         entity.setPosition(pos);
      }
   }

   /**
    * Removes an entity from its current position in the world
    * @param entity Entity
    */
   void removeEntity(Entity entity)
   {
      removeEntityAt(entity.getPosition());
   }

   /**
    * Removes the entity at the given position in the world
    * @param pos Point
    */
   private void removeEntityAt(Point pos)
   {
      if (withinBounds(pos)
              && getOccupancyCell(pos) != null)
      {
         Entity entity = getOccupancyCell(pos);

         /* this moves the entity just outside of the grid for
            debugging purposes */
         entity.setPosition(new Point(-1, -1));
         entities.remove(entity);
         setOccupancyCell(pos, null);
      }
   }

   /**
    * @param pos Point
    * @return the background image for the given point
    */
   Optional<PImage> getBackgroundImage(Point pos)
   {
      if (withinBounds(pos))
      {
         return Optional.of(ImageStore.getCurrentImage(getBackgroundCell(pos)));
      }
      else
      {
         return Optional.empty();
      }
   }

   /**
    * Sets the given position as a background
    * @param background Background
    */
   public void setBackground(Background background)
   {
      if (withinBounds(background.getPosition()))
      {
         setBackgroundCell(background.getPosition(), background);
      }
   }

   /**
    * @param pos Point
    * @return the entity at the given point in the world
    */
   Optional<Entity> getOccupant(Point pos)
   {
      if (isOccupied(pos))
      {
         return Optional.of(getOccupancyCell(pos));
      }
      else
      {
         return Optional.empty();
      }
   }

   /**
    * @param pos Point
    * @return the Entity at the given position in the world
    */
   private Entity getOccupancyCell(Point pos)
   {
      return occupancy[pos.y][pos.x];
   }

   /**
    * Sets an entity as an occupant of a given position in the world
    * @param pos Point
    * @param entity Entity
    */
   private void setOccupancyCell(Point pos,
                                       Entity entity)
   {
      occupancy[pos.y][pos.x] = entity;
   }

   /**
    * @param pos Point
    * @return the background for a given position in the world.
    */
   private Background getBackgroundCell(Point pos)
   {
      return background[pos.y][pos.x];
   }

   /**
    * Sets a background for the given position in the world
    * @param pos Point
    * @param background Background
    */
   private void setBackgroundCell(Point pos,
                                        Background background)
   {
      this.background[pos.y][pos.x] = background;
   }



   int getNumRows() {
      return numRows;
   }

   int getNumCols() {
      return numCols;
   }

   Set<Entity> getEntities() {
      return entities;
   }
}
