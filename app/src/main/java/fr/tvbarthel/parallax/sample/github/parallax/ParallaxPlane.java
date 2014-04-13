package fr.tvbarthel.parallax.sample.github.parallax;

/**
 * Created by tbarthel on 12/04/2014.
 */
public class ParallaxPlane {

    private float mTranslationRatio;

    private int mTranslationDirection;

    /**
     * Plane used during translation motion
     *
     * @param radius
     * @param direction
     */
    public ParallaxPlane(float radius, int direction) {
        mTranslationRatio = radius;
        mTranslationDirection = direction;
    }

    public float getTranslationRatio() {
        return mTranslationRatio;
    }

    public int getTranslationDirection() {
        return mTranslationDirection;
    }
}
