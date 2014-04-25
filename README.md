ParallaxSampleGitHub
====================

![ParallaxSampleGitHub banner](http://tvbarthel.github.io/images/parallax.png)

Sandbox for parallax experiment

This project is a first attempt to perform parallax effect. Considered it as a proof concept since no effort has been made to support multiple screen sizes | orientations and different device sensors.

[Short video on YouTube](https://www.youtube.com/watch?v=hu4kZRP5mZA)

Motions
====================

Motions are based on Accelerometer as almost every Android-powered handset and table has an accelerometer. Moreover, according to the official documentation, ["it uses about 10 times less power than the other motion sensors"](http://developer.android.com/guide/topics/sensors/sensors_motion.html#sensors-motion-accel). For us, low power consumption is essential for animated layout.

In order to provide "smooth rendering", we have implemented low-pass filter to reduce noise and added a custom Animator to evaluate parallax translation between two sensor values.

Usage
=========

Replace your classic RelativeLayout by a ParallaxRelativeLayout and add tag (1-5) for animated items.

Don't forget parallax_background !

1 : most background plane(small house in sample)
2 : background plane (second house in sample)
3 : middle plane (spaceship in sample)
4 : foreground plane (luke gitwalker in sample)
5 : most foreground (404 in sample)

<pre>
  <fr.tvbarthel.parallax.sample.github.parallax.ParallaxRelativeLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:parallax="http://schemas.android.com/apk/res-auto"
    android:id="@+id/parallax"
    android:orientation="vertical"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    parallax:parallax_background="@drawable/parallax_background">
    
        <ImageView
            android:id="@+id/house1"
            ...
            android:tag="1"/>
            
        <ImageView
            android:id="@+id/house2"
            ...
            android:tag="2"/>
            
        <ImageView
            android:id="@+id/spaceship"
            ...
            android:tag="3"/>
            
        <ImageView
            android:id="@+id/luke_gitwalker"
            ...
            android:tag="4"/>
            
        <ImageView
            android:id="@+id/404"
            ...
            android:tag="5"/>
    
    </fr.tvbarthel.parallax.sample.github.parallax.ParallaxRelativeLayout>
</pre>

Then extend ParallaxFragment and use setParallaxRelativeLayout()

<pre>
public class GitHubFragment extends ParallaxFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.github, container, false);

        //set parallax layout
        setParallaxRelativeLayout((ParallaxRelativeLayout) v.findViewById(R.id.parallax));

        return v;
    }
}
</pre>

Credits and License
========

The images used for this demo are entirely based on the 404 GitHub page which has already been designed for a parallax experiment based on mouse motion.

Credits go to Thomas Barthélémy [https://github.com/tbarthel-fr](https://github.com/tbarthel-fr) and Vincent Barthélémy [https://github.com/vbarthel-fr](https://github.com/vbarthel-fr).

Licensed under the Beerware License:

<pre>
You can do whatever you want with this stuff.
If we meet some day, and you think this stuff is worth it, you can buy us a beer (or basically anything else) in return.
</pre>




