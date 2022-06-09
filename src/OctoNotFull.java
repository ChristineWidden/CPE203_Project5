import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class OctoNotFull extends Octo {

    private int resourceCount;

    public OctoNotFull(String id, int resourceLimit, Point position, int actionPeriod, int animationPeriod, List<PImage> images) {

        super(id, resourceLimit, position, actionPeriod, animationPeriod, images);


        this.resourceCount = 0;
    }


    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        //Optional<Entity> notFullTarget = world.findNearest(position, EntityKind.FISH);

        Optional<Entity> notFullTarget = world.findNearest(getPosition(), Fish.class);

        if (notFullTarget.isEmpty() ||
                !moveToNotFull(world, notFullTarget.get(), scheduler) ||
                !transformNotFull(world, scheduler, imageStore))
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
     * Entity
     * Movement for OCTO_NOT_FULL
     * Returns true if octo is adjacent to target, and removes target
     * otherwise moves octo and returns false
     * @param world WorldModel
     * @param target the target entity
     * @param scheduler EventScheduler
     * @return Where the octo should move to
     */
    private boolean moveToNotFull(WorldModel world,
                                  Entity target, EventScheduler scheduler)
    {
        if (getPosition().diagonallyAdjacent(target.getPosition()))
        {
            resourceCount += 1;
            world.removeEntity(target);
            scheduler.unscheduleAllEvents(target);

            return true;
        }
        else
        {
            moveOcto(world, target, scheduler);
            return false;
        }
    }

    /**
     * Replaces this OctoNotFull with a new OctoFull if resourceCount equals or exceeds resourceLimit
     * @param world WorldModel
     * @param scheduler EventScheduler
     * @param imageStore ImageStore
     * @return Did this octo transform?
     */
    private boolean transformNotFull(WorldModel world,
                                     EventScheduler scheduler, ImageStore imageStore)
    {
        if (resourceCount >= getResourceLimit())
        {
            Active octo = new OctoFull(getId(), getResourceLimit(),
                    getPosition(), getActionPeriod(), getAnimationPeriod(),
                    getImages());

            world.removeEntity(this);
            scheduler.unscheduleAllEvents(this);

            world.addEntity(octo);
            octo.scheduleActions(scheduler, world, imageStore);

            return true;
        }

        return false;
    }


}
