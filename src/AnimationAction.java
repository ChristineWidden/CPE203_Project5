public class AnimationAction implements Action {

    //private final Entity entity;
    private final Animated animated;
    private final int repeatCount;

    public AnimationAction(Animated animated, int repeatCount)
    {
        this.animated = animated;
        this.repeatCount = repeatCount;
    }

    @Override
    public void executeAction(EventScheduler scheduler) {
        animated.nextImage();

        if (repeatCount != 1)
        {
            scheduler.scheduleEvent(animated,
                    animated.createAnimationAction(Math.max(repeatCount - 1, 0)),
                    animated.getAnimationPeriod());
        }
    }
}
