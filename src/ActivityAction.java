public class ActivityAction implements Action {

    private final Active active;
    private final WorldModel world;
    private final ImageStore imageStore;

    public ActivityAction(Active active, WorldModel world,
                          ImageStore imageStore)
    {
        this.active = active;
        this.world = world;
        this.imageStore = imageStore;
    }

    @Override
    public void executeAction(EventScheduler scheduler) {
        active.executeActivity(world, imageStore, scheduler);
    }
}
