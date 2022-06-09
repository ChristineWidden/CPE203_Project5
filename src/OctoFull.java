import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class OctoFull extends Octo {


    public OctoFull(String id, int resourceLimit, Point position, int actionPeriod, int animationPeriod, List<PImage> images) {

        super(id, resourceLimit, position, actionPeriod, animationPeriod, images);


    }


    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> fullTarget = world.findNearest(getPosition(),
                Atlantis.class);

        if (fullTarget.isPresent() &&
                moveToFull(world, fullTarget.get(), scheduler))
        {
            //at atlantis trigger animation
            ((Active) fullTarget.get()).scheduleActions(scheduler, world, imageStore);

            //transform to unfull
            transformFull(world, scheduler, imageStore);
        }
        else
        {
            scheduler.scheduleEvent(this,
                    createActivityAction(world, imageStore),
                    getActionPeriod());
        }
    }

    @Override
    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent(this,
                createActivityAction( world, imageStore),
                getActionPeriod());
        scheduler.scheduleEvent(this,
                createAnimationAction( 0), getAnimationPeriod());
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
    private ActivityAction createActivityAction(WorldModel world,
                                        ImageStore imageStore)
    {
        return new ActivityAction(this, world, imageStore);
    }

    /**
     * Returns true if octo is adjacent to target
     * otherwise moves octo and returns false
     * @param world WorldModel
     * @param target the target entity
     * @param scheduler EventScheduler
     * @return is the octo adjacent to the target?
     */
    private boolean moveToFull(WorldModel world,
                               Entity target, EventScheduler scheduler)
    {
        if (getPosition().diagonallyAdjacent(target.getPosition()))
        {
            return true;
        }
        else
        {
            moveOcto(world, target, scheduler);
            return false;
        }
    }

    /**
     * Replaces this OctoFull with a new OctoNotFull
     * @param world WorldModel
     * @param scheduler EventScheduler
     * @param imageStore ImageStore
     */
    private void transformFull(WorldModel world,
                               EventScheduler scheduler, ImageStore imageStore)
    {
        Active octo = new OctoNotFull(getId(), getResourceLimit(),
                getPosition(), getActionPeriod(), getAnimationPeriod(),
                getImages());

        world.removeEntity(this);
        scheduler.unscheduleAllEvents(this);

        world.addEntity(octo);
        octo.scheduleActions(scheduler, world, imageStore);
    }

}
