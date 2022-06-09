import processing.core.PImage;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class SGrass extends Active {

    private static final String FISH_KEY = "fish";

    private static final String FISH_ID_PREFIX = "fish -- ";
    private static final int FISH_CORRUPT_MIN = 20000;
    private static final int FISH_CORRUPT_MAX = 30000;

    private static final Random rand = new Random();


    public SGrass(String id, Point position, int actionPeriod, List<PImage> images) {
        super(id, position, images, 0, actionPeriod);

    }

    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Point> openPt = world.findOpenAround(getPosition());

        if (openPt.isPresent())
        {
            Active fish = new Fish(FISH_ID_PREFIX + getId(),
                    openPt.get(), FISH_CORRUPT_MIN +
                            rand.nextInt(FISH_CORRUPT_MAX - FISH_CORRUPT_MIN),
                    imageStore.getImageList(FISH_KEY));
            world.addEntity(fish);
            fish.scheduleActions(scheduler, world, imageStore);
        }

        scheduler.scheduleEvent(this,
                createActivityAction(world, imageStore),
                getActionPeriod());
    }

    @Override
    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent(this,
                createActivityAction(world, imageStore),
                getActionPeriod());
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
