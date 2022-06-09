import processing.core.PImage;

import java.util.List;

public class Quake extends Animated {

    private static final int QUAKE_ANIMATION_REPEAT_COUNT = 10;

    private static final String QUAKE_ID = "quake";
    private static final int QUAKE_ACTION_PERIOD = 1100;
    private static final int QUAKE_ANIMATION_PERIOD = 100;


    public Quake(Point position, List<PImage> images) {
        super(QUAKE_ID, position, images, 0, QUAKE_ACTION_PERIOD, QUAKE_ANIMATION_PERIOD);

    }


    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        scheduler.unscheduleAllEvents(this);
        world.removeEntity(this);
    }

    @Override
    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent(this,
                createActivityAction(world, imageStore),
                getActionPeriod());
        scheduler.scheduleEvent(this,
                createAnimationAction(QUAKE_ANIMATION_REPEAT_COUNT),
                getAnimationPeriod());
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
}
