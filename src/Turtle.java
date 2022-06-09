import processing.core.PImage;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class Turtle extends Animated {

    private int recalculatePathCounter;
    private LinkedList<Point> path;
    private AStarPathingStrategy pathfinder = new AStarPathingStrategy();

    private final int RECALCULATE_PATH_COUNTER = 3;


    Turtle(String id, Point position, int actionPeriod, int animationPeriod, List<PImage> images) {
        super(id, position, images, 0, actionPeriod, animationPeriod);
        recalculatePathCounter = RECALCULATE_PATH_COUNTER;

    }



    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> turtleTarget = world.findNearest(getPosition(), OctoNotFull.class);
        long nextPeriod = getActionPeriod();

        if (turtleTarget.isPresent())
        {
            moveToTurtle(world, turtleTarget.get(), scheduler);

        }

        scheduler.scheduleEvent(this,
                createActivityAction(world, imageStore),
                nextPeriod);
    }

    @Override
    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent(this,
                createActivityAction(world, imageStore),
                getActionPeriod());
        scheduler.scheduleEvent(this,
                createAnimationAction(0), getAnimationPeriod());
    }


    @Override
    public AnimationAction createAnimationAction(int repeatCount)
    {
        return new AnimationAction(this, repeatCount);
    }

    /**
     * @param world WorldModel
     * @param imageStore ImageStore
     * @return a new activity action
     */
    private Action createActivityAction(WorldModel world,
                                        ImageStore imageStore)
    {
        return new ActivityAction(this, world, imageStore);
    }

    /**
     * Entity
     * Movement for CRAB
     * Returns true if crab is adjacent to target, and removes target
     * @param world WorldModel
     * @param target target entity
     * @param scheduler EventScheduler
     * @return is the crab adjacent to the target?
     */
    private boolean moveToTurtle(WorldModel world,
                                 Entity target, EventScheduler scheduler)
    {
        if (getPosition().adjacent(target.getPosition()))
        // if we are adjacent to the target, remove it, and return true
        {
            world.removeEntity(target);
            scheduler.unscheduleAllEvents(target);
            recalculatePathCounter = RECALCULATE_PATH_COUNTER;
            return true;
        }
        else // we were not adjacent to the target
        {
            Point nextPos = nextPositionTurtle(world, target.getPosition());

            if (!getPosition().equals(nextPos)) //if the next position is not the current position
            {


                Optional<Entity> occupant = world.getOccupant(nextPos);

                // if there is an entity in the next space the crab is supposed to go to,
                // stop it from doing stuff?
                occupant.ifPresent(scheduler::unscheduleAllEvents);

                world.moveEntity(this, nextPos); // move to the next position
            }
            return false;
        }
    }

    private void resetPath(WorldModel world,
                           Point destPos) {
        path = pathfinder.computePath(getPosition(), destPos,
                //p ->  !world.isOccupied(p) && world.withinBounds(p),//not outside grid, not obstacle
                p ->  {Optional<Entity> occupant = world.getOccupant(p);
                    return (p.equals(destPos) || !(occupant.isPresent() && !(occupant.get().getClass() == Fish.class))) && world.withinBounds(p);},//not outside grid, not obstacle
                (p1, p2) -> p1.adjacent(p2), //are these 2 points neighbors
                pathfinder.CARDINAL_NEIGHBORS);

        recalculatePathCounter = -1;
        //LinkedList<Point> linkedPath = path.
    }

    /**
     * @param world WorldModel
     * @param destPos the destination
     * @return The next position for the crab.
     */
    private Point nextPositionTurtle(WorldModel world,
                                     Point destPos)
    {
        if(recalculatePathCounter == RECALCULATE_PATH_COUNTER) {
            resetPath(world, destPos);


        }
        recalculatePathCounter++;


        try {
            Point toReturn = path.getLast();
            path.removeLast();

            if(world.isOccupied(toReturn)){
                resetPath(world, destPos);
                throw new Exception("The chosen path was occupied");
            }

            return toReturn;
        }catch (Exception e) {
            return getPosition();
        }

    }

    /*
    private static boolean neighbors(Point p1, Point p2)
    {
        return p1.x+1 == p2.x && p1.y == p2.y ||
                p1.x-1 == p2.x && p1.y == p2.y ||
                p1.x == p2.x && p1.y+1 == p2.y ||
                p1.x == p2.x && p1.y-1 == p2.y;
    }

     */

}
