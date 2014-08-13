ParallaxSampleGitHub
====================

![ParallaxSampleGitHub banner](http://tvbarthel.github.io/images/parallax.png)

Sandbox for parallax experiment

This project is a first attempt to perform parallax effect. Considered it as a proof concept since no effort has been made to support multiple screen sizes | orientations and different device sensors.

[ParallaxSampleGitHub on YouTube](https://www.youtube.com/watch?v=KyNBZLzWxYI)

Disclaimer
========

The images used for this demo are entirely based on the 404 GitHub page which has already been designed for a parallax experiment based on mouse motion.

Motions
====================

Motions are based on Accelerometer as almost every Android-powered handset and table has an accelerometer. Moreover, according to the official documentation, ["it uses about 10 times less power than the other motion sensors"](http://developer.android.com/guide/topics/sensors/sensors_motion.html#sensors-motion-accel). For us, low power consumption is essential for animated layout.

In order to provide "smooth rendering", we have implemented low-pass filter to reduce noise and added a custom Animator to evaluate parallax translation between two sensor values.

Usage
=========

Replace your classic RelativeLayout by a ParallaxRelativeLayout and add tag (1-5) for animated items.

Don't forget parallax_background !

```xml

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
    
```

Then extend ParallaxFragment and use setParallaxRelativeLayout()

```java
public class GitHubFragment extends ParallaxFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.github, container, false);

        //set parallax layout
        setParallaxRelativeLayout((ParallaxRelativeLayout) v.findViewById(R.id.parallax));

        return v;
    }
}
```

android:tag
=========

<pre>
1 : most background plane(small house in sample)
2 : background plane (second house in sample)
3 : middle plane (spaceship in sample)
4 : foreground plane (luke gitwalker in sample)
5 : most foreground (404 in sample)
</pre>

Credits and License
========

Credits go to Thomas Barthélémy [https://github.com/tbarthel-fr](https://github.com/tbarthel-fr) and Vincent Barthélémy [https://github.com/vbarthel-fr](https://github.com/vbarthel-fr).

Copyright (C) 2014 tvbarthel

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.



