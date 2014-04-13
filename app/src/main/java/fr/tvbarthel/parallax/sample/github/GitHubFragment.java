package fr.tvbarthel.parallax.sample.github;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fr.tvbarthel.parallax.sample.github.parallax.ParallaxFragment;
import fr.tvbarthel.parallax.sample.github.parallax.ParallaxRelativeLayout;

/**
 * Created by tbarthel on 12/04/2014.
 */
public class GitHubFragment extends ParallaxFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.github, container, false);

        //set parallax layout
        setParallaxRelativeLayout((ParallaxRelativeLayout) v.findViewById(R.id.parallax));

        return v;
    }
}
