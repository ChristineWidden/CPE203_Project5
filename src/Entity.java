import processing.core.PImage;

import java.util.List;

public class Entity {


    private final List<PImage> images;
    private int imageIndex;
    private Point position;
    private final String id;

    public Entity(String id, Point position, List<PImage> images, int imageIndex) {
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = imageIndex;
    }


    /**
     * @return The entity's position
     */
    public Point getPosition() {
        return position;
    }

    /**
     * @return the images for the animation cycle
     */
    public List<PImage> getImages() {
        return images;
    }

    /**
     * @return The index of the current image
     */
    int getImageIndex() {
        return imageIndex;
    }


    /**
     * Set the position to the given Point
     * @param position Point
     */
    void setPosition(Point position) {
        this.position = position;
    }

    protected String getId() {
        return id;
    }

    void setImageIndex(int imageIndex) {
        this.imageIndex = imageIndex;
    }

}
