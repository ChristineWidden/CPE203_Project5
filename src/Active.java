import processing.core.PImage;

import java.util.List;

public abstract class Active extends Entity{

    private final int actionPeriod;

    public Active(String id, Point position, List<PImage> images, int imageIndex, int actionPeriod) {
        super(id, position, images, imageIndex);
        this.actionPeriod = actionPeriod;
    }

    /**
     * Execute the behavior for the given entity
     * @param world WorldModel
     * @param imageStore ImageStore
     * @param scheduler EventScheduler
     */
    abstract void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler);

    /**
     * Schedules events for the given entity
     * @param scheduler EventScheduler
     * @param world WorldModel
     * @param imageStore ImageStore
     */
    abstract void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore);

    /**
     * @return The action period for the given entity. 0 if entity has no actions.
     */
    public int getActionPeriod() {
        return actionPeriod;
    }
}
