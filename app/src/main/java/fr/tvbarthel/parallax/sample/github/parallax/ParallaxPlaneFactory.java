package fr.tvbarthel.parallax.sample.github.parallax;

/**
 * Used to create plane according to zOrder
 */
public final class ParallaxPlaneFactory {

    /**
     * most background plane parameters
     */
    private static final float MOTION_SPEED_MOST_BACKGROUND = 0.7f;
    private static final int MOTION_DIRECTION_MOST_BACKGROUND = 1;

    /**
     * background plane parameters
     */
    private static final float MOTION_SPEED_BACKGROUND = 1.0f;
    private static final int MOTION_DIRECTION_BACKGROUND = 1;

    /**
     * middle plane parameters
     */
    private static final float MOTION_SPEED_MIDDLE = 2.0f;
    private static final int MOTION_DIRECTION_MIDDLE = -1;

    /**
     * foreground plane parameters
     */
    private static final float MOTION_SPEED_FOREGROUND = 1.0f;
    private static final int MOTION_DIRECTION_FOREGROUND = -1;

    /**
     * most foreground plane parameters
     */
    private static final float MOTION_SPEED_MOST_FOREGROUND = 0.8f;
    private static final int MOTION_DIRECTION_MOST_FOREGROUND = -1;

    /**
     * static plane parameters
     */
    private static final float MOTION_SPEED_STATIC = 0.0f;
    private static final int MOTION_DIRECTION_STATIC = 1;

    /**
     * return plane matching the given zOrder. Support 5 zOrder (1-5)
     *
     * @param zOrder (1-5)
     * @return
     */
    public static ParallaxPlane createPlane(int zOrder) {
        ParallaxPlane plane;
        switch (zOrder) {
            case 1:
                plane = new ParallaxPlane(MOTION_SPEED_MOST_BACKGROUND, MOTION_DIRECTION_MOST_BACKGROUND);
                break;
            case 2:
                plane = new ParallaxPlane(MOTION_SPEED_BACKGROUND, MOTION_DIRECTION_BACKGROUND);
                break;
            case 3:
                plane = new ParallaxPlane(MOTION_SPEED_MIDDLE, MOTION_DIRECTION_MIDDLE);
                break;
            case 4:
                plane = new ParallaxPlane(MOTION_SPEED_FOREGROUND, MOTION_DIRECTION_FOREGROUND);
                break;
            case 5:
                plane = new ParallaxPlane(MOTION_SPEED_MOST_FOREGROUND, MOTION_DIRECTION_MOST_FOREGROUND);
                break;
            default:
                plane = new ParallaxPlane(MOTION_SPEED_STATIC, MOTION_DIRECTION_STATIC);
                break;
        }
        return plane;
    }

    //non instantiable class
    private ParallaxPlaneFactory() {

    }
}
