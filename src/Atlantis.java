import processing.core.PImage;

import java.util.List;

public class Atlantis extends Animated  {

    private static final int ATLANTIS_ANIMATION_REPEAT_COUNT = 7;


    public Atlantis(String id, Point position, List<PImage> images) {
        super(id, position, images, 0, 0, 0);

    }


    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        scheduler.unscheduleAllEvents(this);
        world.removeEntity(this);
    }

    @Override
    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent(this,
                createAnimationAction(ATLANTIS_ANIMATION_REPEAT_COUNT),
                getAnimationPeriod());
    }




    @Override
    public AnimationAction createAnimationAction(int repeatCount)
    {
        return new AnimationAction(this, repeatCount);
    }

}
