import processing.core.PImage;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

abstract class Octo extends Animated{

    private final int resourceLimit;
    private int recalculatePathCounter;
    private LinkedList<Point> path;
    private AStarPathingStrategy aStarPathfinder = new AStarPathingStrategy();
    private SingleStepPathingStrategy singleStepPathfinder = new SingleStepPathingStrategy();

    Octo(String id, int resourceLimit, Point position, int actionPeriod, int animationPeriod, List<PImage> images) {
        super( id,  position,  images,  0,  actionPeriod,  animationPeriod);
        this.resourceLimit = resourceLimit;
        this.recalculatePathCounter = 5;
    }

    /**
     * 1 horizontal unit towards the destination,
     * unless directly above or below destination,
     * or there is an obstruction.
     * If so, 1 vertical unit towards the destination,
     * unless on same vertical level as destination.
     * If so, next position is the destination.
     * @param world WorldModel
     * @param destPos the destination
     * @return The next position for the octo.
     */
    private Point nextPositionOcto(WorldModel world,
                                   Point destPos)
    {

        if(recalculatePathCounter == 5) {
            //resetPathAStar(world, destPos);
            resetPathSingleStep(world, destPos);


        }
        recalculatePathCounter++;


        try {
            Point toReturn = path.getLast();
            path.removeLast();

            if(world.isOccupied(toReturn)){

                //resetPathAStar(world, destPos);
                resetPathSingleStep(world, destPos);

                throw new Exception("The chosen path was occupied");
            }

            return toReturn;
        }catch (Exception e) {
            return getPosition();
        }


        /*
        int horiz = Integer.signum(destPos.x - getPosition().x);
        Point newPos = new Point(getPosition().x + horiz,
                getPosition().y);

        if (horiz == 0 || world.isOccupied(newPos))
        {
            int vert = Integer.signum(destPos.y - getPosition().y);
            newPos = new Point(getPosition().x,
                    getPosition().y + vert);

            if (vert == 0 || world.isOccupied(newPos))
            {
                newPos = getPosition();
            }
        }

        return newPos;

         */


    }



    void moveOcto(WorldModel world,
                  Entity target, EventScheduler scheduler) {
        Point nextPos = nextPositionOcto(world, target.getPosition());

        if (!getPosition().equals(nextPos))
        {
            Optional<Entity> occupant = world.getOccupant(nextPos);
            occupant.ifPresent(scheduler::unscheduleAllEvents);

            world.moveEntity(this, nextPos);
        }

    }

    private void resetPathAStar(WorldModel world,
                                     Point destPos) {
        path = aStarPathfinder.computePath(getPosition(), destPos,
                //p ->  !world.isOccupied(p) && world.withinBounds(p),//not outside grid, not obstacle
                p ->  {Optional<Entity> occupant = world.getOccupant(p);
                    return (p.equals(destPos) || occupant.isEmpty()) && world.withinBounds(p);},//not outside grid, not obstacle
                (p1, p2) -> p1.diagonallyAdjacent(p2), //are these 2 points neighbors
                aStarPathfinder.DIAGONAL_CARDINAL_NEIGHBORS);

        recalculatePathCounter = -1;
        //LinkedList<Point> linkedPath = path.
    }

    private void resetPathSingleStep(WorldModel world,
                                     Point destPos) {
        List<Point> pathList = singleStepPathfinder.computePath(getPosition(), destPos,
                //p ->  !world.isOccupied(p) && world.withinBounds(p),//not outside grid, not obstacle
                p ->  {Optional<Entity> occupant = world.getOccupant(p);
                    return (p.equals(destPos) || occupant.isEmpty()) && world.withinBounds(p);},//not outside grid, not obstacle
                (p1, p2) -> p1.adjacent(p2), //are these 2 points neighbors
                aStarPathfinder.CARDINAL_NEIGHBORS);

        path = new LinkedList<>();
        for (Point p:
             pathList) {
            path.add(p);
        }

        recalculatePathCounter = -1;
    }

    int getResourceLimit() {
        return resourceLimit;
    }
}
