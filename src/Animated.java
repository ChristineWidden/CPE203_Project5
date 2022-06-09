import processing.core.PImage;

import java.util.List;

public abstract class Animated extends Active{

    private final int animationPeriod;

    public Animated(String id, Point position, List<PImage> images, int imageIndex, int actionPeriod, int animationPeriod) {
        super(id, position, images, imageIndex, actionPeriod);
        this.animationPeriod = animationPeriod;
    }

    /**
     * @return the animation period for the given entity
     */
    public int getAnimationPeriod() {
        return animationPeriod;
    }

    /**
     * Transition to the next image in the animation cycle
     */
    void nextImage() {
        setImageIndex((getImageIndex() + 1) % getImages().size());
    }

    /**
     * Entity
     * Creates a new animation action
     * @param repeatCount The amount of times the animation repeats
     * @return a new AnimationAction
     */
    abstract AnimationAction createAnimationAction(int repeatCount);
}
