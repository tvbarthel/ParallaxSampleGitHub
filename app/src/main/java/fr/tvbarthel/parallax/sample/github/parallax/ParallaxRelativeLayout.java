package fr.tvbarthel.parallax.sample.github.parallax;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import java.util.HashMap;

import fr.tvbarthel.parallax.sample.github.R;


/**
 * Relative layout which use tag to render a parallax experience
 * Don't forget to register this layout as sensor rotation listener
 */
public class ParallaxRelativeLayout extends RelativeLayout implements SensorEventListener {

    /**
     * constant use to convert nano second into second
     */
    private static final float NS2S = 1.0f / 1000000000.0f;

    /**
     * boundary minimum to avoid noise
     */
    private static final float TRANSLATION_NOISE = 0.15f;

    /**
     * boundary maximum, over it phone rotates
     */
    private static final float MAXIMUM_ACCELERATION = 3.00f;

    /**
     * duration for translation animation
     */
    private static final int ANIMATION_DURATION_IN_MILLI = 200;

    /**
     * size of buffer used to determined a wished motion
     */
    private static final int DIRECTION_BUFFER_SIZE = 5;

    /**
     * ratio used to determine radius according to ZOrder
     */
    private static final int DEFAULT_RADIUS_RATIO = 12;

    /**
     * remapped axis X according to current device orientation
     */
    private int mRemappedViewAxisX;

    /**
     * remapped axis Y according to current device orientation
     */
    private int mRemappedViewAxisY;

    /**
     * remapped orientation X according to current device orientation
     */
    private int mRemappedViewOrientationX;

    /**
     * remapped orientation Y according to current device orientations
     */
    private int mRemappedViewOrientationY;

    /**
     * Children view to animate
     */
    private HashMap<View, Integer> mChildrenToAnimate;

    /**
     * store last acceleration values
     */
    private float[] mLastAcceleration;

    /**
     * use to calculate dT
     */
    private long mTimeStamp;

    /**
     * parallax Background
     */
    private ParallaxBackground mParallaxBackground;

    /**
     * Animator for smooth face motion
     */
    private ObjectAnimator mParallaxAnimator;

    /**
     * last known translation
     */
    private float[] mLastTranslation;

    /**
     * buffer used to know if the new x acceleration is noise or wished motion
     */
    private float mDirectionBufferX;

    /**
     * buffer used to know if the new y acceleration is noise or wished motion
     */
    private float mDirectionBufferY;


    /**
     * Constructor
     *
     * @param context
     * @param attrs
     */
    public ParallaxRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ParallaxRelativeLayout,
                0, 0);

        //retrieve custom attribute
        try {
            final Drawable background = a.getDrawable(R.styleable.ParallaxRelativeLayout_parallax_background);
            mParallaxBackground = new ParallaxBackground(context, drawableToBitmap(background));
        } finally {
            a.recycle();
        }

        //allow onDraw for layout
        this.setWillNotDraw(false);


        /**
         * Remap axis and axis' orientation according to the current device rotation
         */
        final int rotation = ((WindowManager) context.getSystemService(context.WINDOW_SERVICE))
                .getDefaultDisplay().getRotation();
        remapAxis(rotation);

        mChildrenToAnimate = new HashMap<View, Integer>();

        mLastAcceleration = new float[]{0.0f, 0.0f};

        mLastTranslation = new float[]{0.0f, 0.0f};

        mTimeStamp = 0;

        mDirectionBufferX = 1.0f;

        mDirectionBufferY = 1.0f;

        mParallaxAnimator = ObjectAnimator.ofObject(this, "CurrentTranslationValues",
                new FloatArrayEvaluator(2), 0);

        mParallaxAnimator.setDuration(ANIMATION_DURATION_IN_MILLI);
    }

    public Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }


    /**
     * Add all children with a tag in order to apply parallax motion
     *
     * @param viewGroup
     */
    private void addParallaxChildrenRecursively(ViewGroup viewGroup) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View childView = viewGroup.getChildAt(i);
            if (childView instanceof ViewGroup) {
                addParallaxChildrenRecursively((ViewGroup) childView);
            }

            if (childView.getTag() != null) {
                Integer zOrder = Integer.valueOf(childView.getTag().toString());
                mChildrenToAnimate.put(childView, zOrder);
            }
        }

    }

    /**
     * Used to remap axis and axis' orientation according to the current device rotation.
     * Orientation inverted = -1 otherwise +1
     *
     * @param rotation current device rotation
     */
    private void remapAxis(int rotation) {
        switch (rotation) {
            case Surface.ROTATION_0:
                mRemappedViewAxisX = 0;
                mRemappedViewAxisY = 1;
                mRemappedViewOrientationX = +1;
                mRemappedViewOrientationY = -1;
                break;

            case Surface.ROTATION_90:
                mRemappedViewAxisX = 1;
                mRemappedViewAxisY = 0;
                mRemappedViewOrientationX = -1;
                mRemappedViewOrientationY = -1;
                break;

            case Surface.ROTATION_270:
                mRemappedViewAxisX = 1;
                mRemappedViewAxisY = 0;
                mRemappedViewOrientationX = +1;
                mRemappedViewOrientationY = +1;
                break;
        }
    }

    /**
     * used by object animator to update current orientation values
     *
     * @param evaluatedValues
     */
    public void setCurrentTranslationValues(float[] evaluatedValues) {

        final float translateX = mRemappedViewOrientationX * this.getWidth() / DEFAULT_RADIUS_RATIO * evaluatedValues[mRemappedViewAxisX];
        final float translateY = mRemappedViewOrientationY * this.getHeight() / DEFAULT_RADIUS_RATIO * evaluatedValues[mRemappedViewAxisY];

        //animate background
        mParallaxBackground.setTranslationX(translateX);
        mParallaxBackground.setTranslationY(translateY);


        //  animate each child provided with an integer tag used as zOrder for parallax
        for (View parallaxItem : mChildrenToAnimate.keySet()) {
            ParallaxPlane plane =
                    ParallaxPlaneFactory.createPlane(mChildrenToAnimate.get(parallaxItem));

            parallaxItem.setTranslationX(translateX * plane.getTranslationDirection() / plane.getTranslationRatio());
            parallaxItem.setTranslationY(translateY * plane.getTranslationDirection() / plane.getTranslationRatio());
        }
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        //get all children which will be animated
        if (mChildrenToAnimate.isEmpty()) {
            addParallaxChildrenRecursively(this);
        }

        if (mParallaxBackground.getParent() == null) {
            LayoutParams params = new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            params.setMargins((int) (-this.getWidth() / 2.0f), (int) (-this.getHeight() / 2.0f), 0, 0);
            this.addView(mParallaxBackground, 0, params);
        }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        final float accelerationX = event.values[mRemappedViewAxisX];
        final float accelerationY = event.values[mRemappedViewAxisY];
        float[] translation = new float[]{0.0f, 0.0f};


        /**
         * Process acceleration to determine motion and let ParallaxRelativeLayout animate
         * it's children and it's background
         */
        if (mTimeStamp != 0) {
            final float dT = (event.timestamp - mTimeStamp) * NS2S;

            /**
             * Use basic integration to retrieve position from acceleration.
             * p = 1/2 * a * dT^2
             */
            if (Math.abs(accelerationX) > MAXIMUM_ACCELERATION) {
                translation[mRemappedViewAxisX] = mLastAcceleration[mRemappedViewAxisX] + 0.5f * MAXIMUM_ACCELERATION * dT * dT;
            } else {
                translation[mRemappedViewAxisX] = mLastAcceleration[mRemappedViewAxisX] + 0.5f * accelerationX * dT * dT;
                mLastAcceleration[mRemappedViewAxisX] = accelerationX;
            }

            if (Math.abs(accelerationY) > MAXIMUM_ACCELERATION) {
                translation[mRemappedViewAxisY] = mLastAcceleration[mRemappedViewAxisY] + 0.5f * MAXIMUM_ACCELERATION * dT * dT;
            } else {
                translation[mRemappedViewAxisY] = mLastAcceleration[mRemappedViewAxisY] + 0.5f * accelerationY * dT * dT;
                mLastAcceleration[mRemappedViewAxisY] = accelerationY;
            }

            /**
             * In order to keep small variations, the noise is dynamic.
             * We normalized translation and noise it by the means of last and new value.
             */
            final float normalizerX = (Math.abs(mLastTranslation[mRemappedViewAxisX]) + Math.abs(translation[mRemappedViewAxisX])) / 2;
            final float normalizerY = (Math.abs(mLastTranslation[mRemappedViewAxisY]) + Math.abs(translation[mRemappedViewAxisY])) / 2;

            final float translationDifX = Math.abs(mLastTranslation[mRemappedViewAxisX] - translation[mRemappedViewAxisX]) / normalizerX;
            final float translationDifY = Math.abs(mLastTranslation[mRemappedViewAxisY] - translation[mRemappedViewAxisY]) / normalizerY;

            final float dynamicNoiseX = TRANSLATION_NOISE / normalizerX;
            final float dynamicNoiseY = TRANSLATION_NOISE / normalizerY;

            float[] newTranslation = null;

            if (translationDifX > dynamicNoiseX && translationDifY > dynamicNoiseY) {
                newTranslation = translation.clone();
            } else if (translationDifX > dynamicNoiseX) {
                newTranslation = new float[2];
                newTranslation[mRemappedViewAxisX] = translation[mRemappedViewAxisX];
                newTranslation[mRemappedViewAxisY] = mLastTranslation[mRemappedViewAxisY];
            } else if (translationDifY > dynamicNoiseY) {
                newTranslation = new float[2];
                newTranslation[mRemappedViewAxisX] = mLastTranslation[mRemappedViewAxisX];
                newTranslation[mRemappedViewAxisY] = translation[mRemappedViewAxisY];
            }


            /**
             * if new translation aren't noise as values, check if directions are noise as direction
             */
            if (newTranslation != null) {

                boolean wishedMotion = true;

                //TODO replace by a low pass filter ?!!

                /**
                 * Check direction only for translation values close from noise
                 */
                if ((translationDifX < 3.0 * dynamicNoiseX) || (translationDifY < 3.0 * dynamicNoiseY)) {

                    final float currentBufferDirectionX = Math.signum(mDirectionBufferX);
                    final float currentBufferDirectionY = Math.signum(mDirectionBufferY);

                    /**
                     * A motion is considered as wished if value is higher in case of increasing
                     * buffer or smaller in case of decreasing buffer
                     */
                    final boolean wishedMotionX = (currentBufferDirectionX >= 0.0f && newTranslation[mRemappedViewAxisX] >= mLastTranslation[mRemappedViewAxisX])
                            || (currentBufferDirectionX < 0.0f && newTranslation[mRemappedViewAxisX] < mLastTranslation[mRemappedViewAxisX]);
                    final boolean wishedMotionY = (currentBufferDirectionY >= 0.0f && newTranslation[mRemappedViewAxisY] >= mLastTranslation[mRemappedViewAxisY])
                            || (currentBufferDirectionY < 0.0f && newTranslation[mRemappedViewAxisY] < mLastTranslation[mRemappedViewAxisY]);

                    /**
                     * reset newTranslation values for axis where motion is not wished
                     */
                    if (wishedMotionX && wishedMotionY) {
                    } else if (wishedMotionX) {
                        newTranslation[mRemappedViewAxisY] = mLastTranslation[mRemappedViewAxisY];
                    } else if (wishedMotionY) {
                        newTranslation[mRemappedViewAxisX] = mLastTranslation[mRemappedViewAxisX];
                    } else {
                        wishedMotion = false;
                    }

                    /**
                     * Update buffer values
                     */
                    mDirectionBufferX += newTranslation[mRemappedViewAxisX] >= mLastTranslation[mRemappedViewAxisX] ? 1.0f : -1.0f;
                    mDirectionBufferY += newTranslation[mRemappedViewAxisY] >= mLastTranslation[mRemappedViewAxisY] ? 1.0f : -1.0f;

                    final float updatedDirectionX = Math.signum(mDirectionBufferX);
                    final float updatedDirectionY = Math.signum(mDirectionBufferX);

                    if (Math.abs(mDirectionBufferX) > DIRECTION_BUFFER_SIZE) {
                        mDirectionBufferX = DIRECTION_BUFFER_SIZE * updatedDirectionX;
                    }

                    if (Math.abs(mDirectionBufferY) > DIRECTION_BUFFER_SIZE) {
                        mDirectionBufferY = DIRECTION_BUFFER_SIZE * updatedDirectionY;
                    }
                }

                /**
                 * Animate parallax item if both translation values are higher than noise
                 * and direction are the result of real wished motion.
                 */
                if (wishedMotion) {
                    if (mParallaxAnimator.isRunning()) {
                        mParallaxAnimator.cancel();
                    }
                    mParallaxAnimator.setObjectValues(mLastTranslation.clone(), translation.clone());
                    mParallaxAnimator.start();
                    mLastTranslation[mRemappedViewAxisX] = translation[mRemappedViewAxisX];
                    mLastTranslation[mRemappedViewAxisY] = translation[mRemappedViewAxisY];
                }

            }

        }
        mTimeStamp = event.timestamp;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
