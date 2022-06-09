import java.util.List;
import java.util.Optional;
import java.util.Random;

import processing.core.PImage;

/*
Entity ideally would includes functions for how all the entities in our virtual world might act...
 */


final class Entity
{
   private static final String FISH_KEY = "fish";

   private static final int ATLANTIS_ANIMATION_REPEAT_COUNT = 7;

   private static final int QUAKE_ANIMATION_REPEAT_COUNT = 10;

   private static final String CRAB_KEY = "crab";
   private static final String CRAB_ID_SUFFIX = " -- crab";
   private static final int CRAB_PERIOD_SCALE = 4;
   private static final int CRAB_ANIMATION_MIN = 50;
   private static final int CRAB_ANIMATION_MAX = 150;

   private static final String QUAKE_KEY = "quake";

   private static final String FISH_ID_PREFIX = "fish -- ";
   private static final int FISH_CORRUPT_MIN = 20000;
   private static final int FISH_CORRUPT_MAX = 30000;

   private static final Random rand = new Random();

   private static final String QUAKE_ID = "quake";
   private static final int QUAKE_ACTION_PERIOD = 1100;
   private static final int QUAKE_ANIMATION_PERIOD = 100;



   private EntityKind kind;
   private String id;
   private Point position;
   private List<PImage> images;
   private int imageIndex;
   private int resourceLimit;
   private int resourceCount;
   private int actionPeriod;
   private int animationPeriod;

   private Entity(EntityKind kind, String id, Point position,
      List<PImage> images, int resourceLimit, int resourceCount,
      int actionPeriod, int animationPeriod)
   {
      this.kind = kind;
      this.id = id;
      this.position = position;
      this.images = images;
      this.imageIndex = 0;
      this.resourceLimit = resourceLimit;
      this.resourceCount = resourceCount;
      this.actionPeriod = actionPeriod;
      this.animationPeriod = animationPeriod;
   }


   /**
    * Entity
    * Returns the animation period for the given entity
    * @return
    */
   public int getAnimationPeriod()
   {
      switch (kind)
      {
         case OCTO_FULL:
         case OCTO_NOT_FULL:
         case CRAB:
         case QUAKE:
         case ATLANTIS:
            return animationPeriod;
         default:
            throw new UnsupportedOperationException(
                    String.format("getAnimationPeriod not supported for %s",
                            kind));
      }
   }


   /**
    * Entity
    * Returns the next image for the given entity
    *
    */
   public void nextImage()
   {
      imageIndex = (imageIndex + 1) % images.size();
   }


   /**
    * Entity
    * Executes the appropriate activity for a full Octo entity
    * @param world
    * @param imageStore
    * @param scheduler
    */
   public void executeOctoFullActivity(WorldModel world,
                                              ImageStore imageStore, EventScheduler scheduler)
   {
      Optional<Entity> fullTarget = world.findNearest(position,
              EntityKind.ATLANTIS);

      if (fullTarget.isPresent() &&
              moveToFull(world, fullTarget.get(), scheduler))
      {
         //at atlantis trigger animation
         fullTarget.get().scheduleActions(scheduler, world, imageStore);

         //transform to unfull
         transformFull(world, scheduler, imageStore);
      }
      else
      {
         scheduler.scheduleEvent(this,
                 createActivityAction(world, imageStore),
                 actionPeriod);
      }
   }

   /**
    * Entity
    * Executes the appropriate activity for a not full Octo entity
    * @param world
    * @param imageStore
    * @param scheduler
    */
   public void executeOctoNotFullActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler)
   {
      Optional<Entity> notFullTarget = world.findNearest(position,
              EntityKind.FISH);

      if (!notFullTarget.isPresent() ||
              !moveToNotFull(world, notFullTarget.get(), scheduler) ||
              !transformNotFull(world, scheduler, imageStore))
      {
         scheduler.scheduleEvent(this,
                 createActivityAction(world, imageStore),
                 actionPeriod);
      }
   }

   /**
    * Entity
    * Executes the appropriate activity for a Fish entity
    * (Turn fish into a crab???)
    * @param world
    * @param imageStore
    * @param scheduler
    */
   public void executeFishActivity(WorldModel world,
                                          ImageStore imageStore, EventScheduler scheduler)
   {
      Point pos = position;  // store current position before removing

      world.removeEntity(this);
      scheduler.unscheduleAllEvents(this);

      Entity crab = createCrab(id + CRAB_ID_SUFFIX,
              pos, actionPeriod / CRAB_PERIOD_SCALE,
              CRAB_ANIMATION_MIN +
                      rand.nextInt(CRAB_ANIMATION_MAX - CRAB_ANIMATION_MIN),
              imageStore.getImageList(CRAB_KEY));

      world.addEntity(crab);
      crab.scheduleActions(scheduler, world, imageStore);
   }

   /**
    * Entity
    * Executes the appropriate activity for a Crab entity
    * @param world
    * @param imageStore
    * @param scheduler
    */
   public void executeCrabActivity(WorldModel world,
                                          ImageStore imageStore, EventScheduler scheduler)
   {
      Optional<Entity> crabTarget = world.findNearest(position, EntityKind.SGRASS);
      long nextPeriod = actionPeriod;

      if (crabTarget.isPresent())
      {
         Point tgtPos = crabTarget.get().position;

         if (moveToCrab(world, crabTarget.get(), scheduler))
         {
            Entity quake = createQuake(tgtPos,
                    imageStore.getImageList(QUAKE_KEY));

            world.addEntity(quake);
            nextPeriod += actionPeriod;
            quake.scheduleActions(scheduler, world, imageStore);
         }
      }

      scheduler.scheduleEvent(this,
              createActivityAction(world, imageStore),
              nextPeriod);
   }

   /**
    * Entity
    * Executes the appropriate activity for a Quake entity
    * @param world
    * @param imageStore
    * @param scheduler
    */
   public void executeQuakeActivity(WorldModel world,
                                           ImageStore imageStore, EventScheduler scheduler)
   {
      scheduler.unscheduleAllEvents(this);
      world.removeEntity(this);
   }

   /**
    * Entity
    * Executes the appropriate activity for an Atlantis entity
    * @param world
    * @param imageStore
    * @param scheduler
    */
   public void executeAtlantisActivity(WorldModel world,
                                              ImageStore imageStore, EventScheduler scheduler)
   {
      scheduler.unscheduleAllEvents(this);
      world.removeEntity(this);
   }

   /**
    * Entity
    * Executes the appropriate activity for a Sgrass entity
    * @param world
    * @param imageStore
    * @param scheduler
    */
   public void executeSgrassActivity(WorldModel world,
                                            ImageStore imageStore, EventScheduler scheduler)
   {
      Optional<Point> openPt = world.findOpenAround(position);

      if (openPt.isPresent())
      {
         Entity fish = createFish(FISH_ID_PREFIX + id,
                 openPt.get(), FISH_CORRUPT_MIN +
                         rand.nextInt(FISH_CORRUPT_MAX - FISH_CORRUPT_MIN),
                 imageStore.getImageList(FISH_KEY));
         world.addEntity(fish);
         fish.scheduleActions(scheduler, world, imageStore);
      }

      scheduler.scheduleEvent(this,
              createActivityAction(world, imageStore),
              actionPeriod);
   }

   /**
    * Entity
    * Schedules events for the given entity
    * @param scheduler
    * @param world
    * @param imageStore
    */
   public void scheduleActions(EventScheduler scheduler,
                                      WorldModel world, ImageStore imageStore)
   {
      switch (kind)
      {
         case OCTO_FULL:
            scheduler.scheduleEvent(this,
                    createActivityAction( world, imageStore),
                    actionPeriod);
            scheduler.scheduleEvent(this,
                    createAnimationAction( 0), getAnimationPeriod());
            break;

         case OCTO_NOT_FULL:
            scheduler.scheduleEvent(this,
                    createActivityAction( world, imageStore),
                    actionPeriod);
            scheduler.scheduleEvent(this,
                    createAnimationAction( 0), getAnimationPeriod());
            break;

         case FISH:
            scheduler.scheduleEvent(this,
                    createActivityAction( world, imageStore),
                    actionPeriod);
            break;

         case CRAB:
            scheduler.scheduleEvent(this,
                    createActivityAction(world, imageStore),
                    actionPeriod);
            scheduler.scheduleEvent(this,
                    createAnimationAction(0), getAnimationPeriod());
            break;

         case QUAKE:
            scheduler.scheduleEvent(this,
                    createActivityAction(world, imageStore),
                    actionPeriod);
            scheduler.scheduleEvent(this,
                    createAnimationAction(QUAKE_ANIMATION_REPEAT_COUNT),
                    getAnimationPeriod());
            break;

         case SGRASS:
            scheduler.scheduleEvent(this,
                    createActivityAction(world, imageStore),
                    actionPeriod);
            break;
         case ATLANTIS:
            scheduler.scheduleEvent(this,
                    createAnimationAction(ATLANTIS_ANIMATION_REPEAT_COUNT),
                    getAnimationPeriod());
            break;

         default:
      }
   }



   /**
    * Entity
    * Transforms an OCTO_NOT_FULL to an OCTO_FULL if the entity has enough resources
    * @param world
    * @param scheduler
    * @param imageStore
    * @return
    */
   private boolean transformNotFull(WorldModel world,
                                          EventScheduler scheduler, ImageStore imageStore)
   {
      if (resourceCount >= resourceLimit)
      {
         Entity octo = createOctoFull(id, resourceLimit,
                 position, actionPeriod, animationPeriod,
                 images);

         world.removeEntity(this);
         scheduler.unscheduleAllEvents(this);

         world.addEntity(octo);
         octo.scheduleActions(scheduler, world, imageStore);

         return true;
      }

      return false;
   }

   /**
    * Entity
    * Transforms an OCTO_FULL to an OCTO_NOT_FULL
    * @param world
    * @param scheduler
    * @param imageStore
    */
   private void transformFull(WorldModel world,
                                    EventScheduler scheduler, ImageStore imageStore)
   {
      Entity octo = createOctoNotFull(id, resourceLimit,
              position, actionPeriod, animationPeriod,
              images);

      world.removeEntity(this);
      scheduler.unscheduleAllEvents(this);

      world.addEntity(octo);
      octo.scheduleActions(scheduler, world, imageStore);
   }


   /**
    * Entity
    * Movement for OCTO_NOT_FULL
    * Returns true if octo is adjacent to target, and removes target
    * otherwise moves octo and returns false
    * @param world
    * @param target the target entity
    * @param scheduler
    * @return
    */
   private boolean moveToNotFull(WorldModel world,
                                       Entity target, EventScheduler scheduler)
   {
      if (position.adjacent(target.position))
      {
         resourceCount += 1;
         world.removeEntity(target);
         scheduler.unscheduleAllEvents(target);

         return true;
      }
      else
      {
         Point nextPos = nextPositionOcto(world, target.position);

         if (!position.equals(nextPos))
         {
            Optional<Entity> occupant = world.getOccupant(nextPos);
            if (occupant.isPresent())
            {
               scheduler.unscheduleAllEvents(occupant.get());
            }

            world.moveEntity(this, nextPos);
         }
         return false;
      }
   }

   /**
    * Entity
    * Movement for OCTO_FULL
    * Returns true if octo is adjacent to target
    * otherwise moves octo and returns false
    * @param world
    * @param target the target entity
    * @param scheduler
    * @return
    */
   private boolean moveToFull(WorldModel world,
                                    Entity target, EventScheduler scheduler)
   {
      if (position.adjacent(target.position))
      {
         return true;
      }
      else
      {
         Point nextPos = nextPositionOcto(world, target.position);

         if (!position.equals(nextPos))
         {
            Optional<Entity> occupant = world.getOccupant(nextPos);
            if (occupant.isPresent())
            {
               scheduler.unscheduleAllEvents(occupant.get());
            }

            world.moveEntity(this, nextPos);
         }
         return false;
      }
   }


   /**
    * Entity
    * Movement for CRAB
    * Returns true if crab is adjacent to target, and removes target
    * @param world
    * @param target target entity
    * @param scheduler
    * @return
    */
   private boolean moveToCrab(WorldModel world,
                                    Entity target, EventScheduler scheduler)
   {
      if (position.adjacent(target.position))
      {
         world.removeEntity(target);
         scheduler.unscheduleAllEvents(target);
         return true;
      }
      else
      {
         Point nextPos = nextPositionCrab(world, target.position);

         if (!position.equals(nextPos))
         {
            Optional<Entity> occupant = world.getOccupant(nextPos);
            if (occupant.isPresent())
            {
               scheduler.unscheduleAllEvents(occupant.get());
            }

            world.moveEntity(this, nextPos);
         }
         return false;
      }
   }

   /**
    * Entity
    * The next position for the octo.
    *
    * 1 horizontal unit towards the destination,
    * unless directly above or below destination,
    * or there is an obstruction.
    * If so, 1 vertical unit towards the destination,
    * unless on same vertical level as destination.
    * If so, next position is the destination.
    * @param world
    * @param destPos the destination
    * @return
    */
   private Point nextPositionOcto(WorldModel world,
                                        Point destPos)
   {
      int horiz = Integer.signum(destPos.x - position.x);
      Point newPos = new Point(position.x + horiz,
              position.y);

      if (horiz == 0 || world.isOccupied(newPos))
      {
         int vert = Integer.signum(destPos.y - position.y);
         newPos = new Point(position.x,
                 position.y + vert);

         if (vert == 0 || world.isOccupied(newPos))
         {
            newPos = position;
         }
      }

      return newPos;
   }

   /**
    * Entity
    * The next position for the crab.
    *
    * @param world
    * @param destPos the destination
    * @return
    */
   private Point nextPositionCrab(WorldModel world,
                                        Point destPos)
   {
      int horiz = Integer.signum(destPos.x - position.x);
      Point newPos = new Point(position.x + horiz,
              position.y);

      Optional<Entity> occupant = world.getOccupant(newPos);

      if (horiz == 0 ||
              (occupant.isPresent() && !(occupant.get().kind == EntityKind.FISH)))
      {
         int vert = Integer.signum(destPos.y - position.y);
         newPos = new Point(position.x, position.y + vert);
         occupant = world.getOccupant(newPos);

         if (vert == 0 ||
                 (occupant.isPresent() && !(occupant.get().kind == EntityKind.FISH)))
         {
            newPos = position;
         }
      }

      return newPos;
   }


   /**
    * Entity
    * Creates a new animation action
    * @param repeatCount
    * @return
    */
   public AnimationAction createAnimationAction(int repeatCount)
   {
      return new AnimationAction(this, repeatCount);
   }

   /**
    * Entity
    * Creates a new activity action
    * @param world
    * @param imageStore
    * @return
    */
   private Action createActivityAction(WorldModel world,
                                             ImageStore imageStore)
   {
      return new ActivityAction(this, world, imageStore);
   }

   /**
    * Entity
    * Creates an ATLANTIS entity
    * @param id
    * @param position
    * @param images
    * @return
    */
   public static Entity createAtlantis(String id, Point position,
                                       List<PImage> images)
   {
      return new Entity(EntityKind.ATLANTIS, id, position, images,
              0, 0, 0, 0);
   }

   /**
    * Entity
    * Creates an OCTO_FULL entity
    * @param id
    * @param resourceLimit
    * @param position
    * @param actionPeriod
    * @param animationPeriod
    * @param images
    * @return
    */
   public static Entity createOctoFull(String id, int resourceLimit,
                                       Point position, int actionPeriod, int animationPeriod,
                                       List<PImage> images)
   {
      return new Entity(EntityKind.OCTO_FULL, id, position, images,
              resourceLimit, resourceLimit, actionPeriod, animationPeriod);
   }

   /**
    * Entity
    * Creates an OCTO_NOT_FULL entity
    * @param id
    * @param resourceLimit
    * @param position
    * @param actionPeriod
    * @param animationPeriod
    * @param images
    * @return
    */
   public static Entity createOctoNotFull(String id, int resourceLimit,
                                          Point position, int actionPeriod, int animationPeriod,
                                          List<PImage> images)
   {
      return new Entity(EntityKind.OCTO_NOT_FULL, id, position, images,
              resourceLimit, 0, actionPeriod, animationPeriod);
   }

   /**
    * Entity
    * Creates an OBSTACLE entity
    * @param id
    * @param position
    * @param images
    * @return
    */
   public static Entity createObstacle(String id, Point position,
                                       List<PImage> images)
   {
      return new Entity(EntityKind.OBSTACLE, id, position, images,
              0, 0, 0, 0);
   }

   /**
    * Entity
    * Creates a FISH entity
    * @param id
    * @param position
    * @param actionPeriod
    * @param images
    * @return
    */
   public static Entity createFish(String id, Point position, int actionPeriod,
                                   List<PImage> images)
   {
      return new Entity(EntityKind.FISH, id, position, images, 0, 0,
              actionPeriod, 0);
   }

   /**
    * Entity
    * Creates a CRAB entity
    * @param id
    * @param position
    * @param actionPeriod
    * @param animationPeriod
    * @param images
    * @return
    */
   public static Entity createCrab(String id, Point position,
                                   int actionPeriod, int animationPeriod, List<PImage> images)
   {
      return new Entity(EntityKind.CRAB, id, position, images,
              0, 0, actionPeriod, animationPeriod);
   }

   /**
    * Entity
    * Creates a QUAKE entity
    * @param position
    * @param images
    * @return
    */
   public static Entity createQuake(Point position, List<PImage> images)
   {
      return new Entity(EntityKind.QUAKE, QUAKE_ID, position, images,
              0, 0, QUAKE_ACTION_PERIOD, QUAKE_ANIMATION_PERIOD);
   }

   /**
    * Entity
    * Creates a SGRASS entity
    * @param id
    * @param position
    * @param actionPeriod
    * @param images
    * @return
    */
   public static Entity createSgrass(String id, Point position, int actionPeriod,
                                     List<PImage> images)
   {
      return new Entity(EntityKind.SGRASS, id, position, images, 0, 0,
              actionPeriod, 0);
   }


   public EntityKind getKind() {
      return kind;
   }

   public Point getPosition() {
      return position;
   }

   public List<PImage> getImages() {
      return images;
   }

   public int getImageIndex() {
      return imageIndex;
   }

   public int getActionPeriod() {
      return actionPeriod;
   }

   public void setPosition(Point position) {
      this.position = position;
   }
}
