public class Parser {


    private static final String OCTO_KEY = "octo";
    private static final int OCTO_NUM_PROPERTIES = 7;
    private static final int OCTO_ID = 1;
    private static final int OCTO_COL = 2;
    private static final int OCTO_ROW = 3;
    private static final int OCTO_LIMIT = 4;
    private static final int OCTO_ACTION_PERIOD = 5;
    private static final int OCTO_ANIMATION_PERIOD = 6;

    private static final String OBSTACLE_KEY = "obstacle";
    private static final int OBSTACLE_NUM_PROPERTIES = 4;
    private static final int OBSTACLE_ID = 1;
    private static final int OBSTACLE_COL = 2;
    private static final int OBSTACLE_ROW = 3;

    private static final String FISH_KEY = "fish";
    private static final int FISH_NUM_PROPERTIES = 5;
    private static final int FISH_ID = 1;
    private static final int FISH_COL = 2;
    private static final int FISH_ROW = 3;
    private static final int FISH_ACTION_PERIOD = 4;

    private static final String ATLANTIS_KEY = "atlantis";
    private static final int ATLANTIS_NUM_PROPERTIES = 4;
    private static final int ATLANTIS_ID = 1;
    private static final int ATLANTIS_COL = 2;
    private static final int ATLANTIS_ROW = 3;
    //private static final int ATLANTIS_ANIMATION_PERIOD = 70;

    private static final String SGRASS_KEY = "seaGrass";
    private static final int SGRASS_NUM_PROPERTIES = 5;
    private static final int SGRASS_ID = 1;
    private static final int SGRASS_COL = 2;
    private static final int SGRASS_ROW = 3;
    private static final int SGRASS_ACTION_PERIOD = 4;

    private static final String BGND_KEY = "background";
    private static final int BGND_NUM_PROPERTIES = 4;
    private static final int BGND_ID = 1;
    private static final int BGND_COL = 2;
    private static final int BGND_ROW = 3;


    private static final String THING_KEY = "thing";
    private static final int THING_NUM_PROPERTIES = 6;
    private static final int THING_ID = 1;
    private static final int THING_COL = 2;
    private static final int THING_ROW = 3;
    private static final int THING_ACTION_PERIOD = 4;
    private static final int THING_ANIMATION_PERIOD = 5;



    private static final int PROPERTY_KEY = 0;

    /**
     * WorldModel
     * Processes a line from the saved file,
     * parsing it into the appropriate entity or background
     * @param line
     * @param imageStore
     * @return success
     */
    public static Object processLine(String line, ImageStore imageStore)
    {
        String[] properties = line.split("\\s");
        if (properties.length > 0)
        {
            switch (properties[PROPERTY_KEY])
            {
                case BGND_KEY:
                    return parseBackground(properties, imageStore);
                case OCTO_KEY:
                    return parseOcto(properties, imageStore);
                case OBSTACLE_KEY:
                    return parseObstacle(properties, imageStore);
                case FISH_KEY:
                    return parseFish(properties, imageStore);
                case ATLANTIS_KEY:
                    return parseAtlantis(properties, imageStore);
                case SGRASS_KEY:
                    return parseSgrass(properties, imageStore);
                case THING_KEY:
                    return parseTurtle(properties, imageStore);
            }
        }

        return false;
    }

    /**
     * WorldModel
     * Parses a background from saved data
     * @param properties
     * @param imageStore
     * @return success
     */
    private static Background parseBackground(String [] properties, ImageStore imageStore)
    {
        if (properties.length == BGND_NUM_PROPERTIES)
        {
            Point pt = new Point(Integer.parseInt(properties[BGND_COL]),
                    Integer.parseInt(properties[BGND_ROW]));
            String id = properties[BGND_ID];

            //setBackground(pt, new Background(id, imageStore.getImageList(id)));

            return new Background(id, imageStore.getImageList(id), pt);
        }

        return null;
        //return properties.length == BGND_NUM_PROPERTIES;
    }

    /**
     * WorldModel
     * Parses an OCTO_NOT_FULL from saved data
     * @param properties
     * @param imageStore
     * @return success
     */
    private static Entity parseOcto(String [] properties, ImageStore imageStore)
    {
        if (properties.length == OCTO_NUM_PROPERTIES)
        {
            Point pt = new Point(Integer.parseInt(properties[OCTO_COL]),
                    Integer.parseInt(properties[OCTO_ROW]));
            Entity entity = new OctoNotFull(properties[OCTO_ID],
                    Integer.parseInt(properties[OCTO_LIMIT]),
                    pt,
                    Integer.parseInt(properties[OCTO_ACTION_PERIOD]),
                    Integer.parseInt(properties[OCTO_ANIMATION_PERIOD]),
                    imageStore.getImageList(OCTO_KEY));
            return entity;
            //tryAddEntity(entity);
        }

        return null;
        //return properties.length == OCTO_NUM_PROPERTIES;
    }

    /**
     * WorldModel
     * Parses an OBSTACLE from saved data
     * @param properties
     * @param imageStore
     * @return success
     */
    private static Entity parseObstacle(String [] properties, ImageStore imageStore)
    {
        if (properties.length == OBSTACLE_NUM_PROPERTIES)
        {
            Point pt = new Point(
                    Integer.parseInt(properties[OBSTACLE_COL]),
                    Integer.parseInt(properties[OBSTACLE_ROW]));
            Entity entity = new Obstacle(properties[OBSTACLE_ID],
                    pt, imageStore.getImageList(OBSTACLE_KEY));

            return entity;
            //tryAddEntity(entity);
        }

        return null;
        //return properties.length == OBSTACLE_NUM_PROPERTIES;
    }

    /**
     * WorldModel
     * Parses a FISH from saved data
     * @param properties String[]
     * @param imageStore ImageStore
     * @return success
     */
    private static Entity parseFish(String[] properties, ImageStore imageStore)
    {
        if (properties.length == FISH_NUM_PROPERTIES)
        {
            Point pt = new Point(Integer.parseInt(properties[FISH_COL]),
                    Integer.parseInt(properties[FISH_ROW]));
            Entity entity = new Fish(properties[FISH_ID],
                    pt, Integer.parseInt(properties[FISH_ACTION_PERIOD]),
                    imageStore.getImageList(FISH_KEY));

            return entity;
            //tryAddEntity(entity);
        }

        return null;
        //return properties.length == FISH_NUM_PROPERTIES;
    }

    /**
     * WorldModel
     * Parses an ATLANTIS from saved data
     * @param properties
     * @param imageStore
     * @return success
     */
    private static Entity parseAtlantis(String [] properties, ImageStore imageStore)
    {
        if (properties.length == ATLANTIS_NUM_PROPERTIES)
        {
            Point pt = new Point(Integer.parseInt(properties[ATLANTIS_COL]),
                    Integer.parseInt(properties[ATLANTIS_ROW]));
            Entity entity = new Atlantis(properties[ATLANTIS_ID],
                    pt, imageStore.getImageList(ATLANTIS_KEY));

            return entity;
            //tryAddEntity(entity);
        }

        return null;
        //return properties.length == ATLANTIS_NUM_PROPERTIES;
    }

    /**
     * WorldModel
     * Parses a SGRASS from saved data
     * @param properties
     * @param imageStore
     * @return success
     */
    private static Entity parseSgrass(String [] properties, ImageStore imageStore)
    {
        if (properties.length == SGRASS_NUM_PROPERTIES)
        {
            Point pt = new Point(Integer.parseInt(properties[SGRASS_COL]),
                    Integer.parseInt(properties[SGRASS_ROW]));
            Entity entity = new SGrass(properties[SGRASS_ID],
                    pt,
                    Integer.parseInt(properties[SGRASS_ACTION_PERIOD]),
                    imageStore.getImageList(SGRASS_KEY));

            return entity;
            //tryAddEntity(entity);
        }

        return null;
        //return properties.length == SGRASS_NUM_PROPERTIES;
    }

    /**
     * WorldModel
     * Parses an OCTO_NOT_FULL from saved data
     * @param properties
     * @param imageStore
     * @return success
     */
    private static Entity parseTurtle(String [] properties, ImageStore imageStore)
    {
        if (properties.length == THING_NUM_PROPERTIES)
        {
            Point pt = new Point(Integer.parseInt(properties[THING_COL]),
                    Integer.parseInt(properties[THING_ROW]));
            return new Turtle(properties[THING_ID],
                    pt,
                    Integer.parseInt(properties[THING_ACTION_PERIOD]),
                    Integer.parseInt(properties[THING_ANIMATION_PERIOD]),
                    imageStore.getImageList(THING_KEY));
            //tryAddEntity(entity);
        }

        return null;
        //return properties.length == OCTO_NUM_PROPERTIES;
    }
}
