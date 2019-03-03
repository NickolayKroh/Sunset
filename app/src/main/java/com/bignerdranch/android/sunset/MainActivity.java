package com.bignerdranch.android.sunset;

import android.app.*;
import android.os.*;
import android.view.*;
import android.util.*;
import android.content.*;
import android.animation.*;
import android.view.animation.*;
import android.content.res.*;
import android.graphics.drawable.*;
import android.graphics.*;

public class MainActivity extends Activity 
{
	private View mSceneView;
	private View mSunView;
	private View mSkyView;
	private View mSeaView;
	
	private int mBlueSkyColor;
	private int mSunsetSkyColor;
	private int mNightSkyColor;
			
	private boolean isLastTimeSunGoesUp = true;
	private ObjectAnimator mSunAnimator = new ObjectAnimator();
	private ObjectAnimator mDayAnimator = new ObjectAnimator();
	private ObjectAnimator mNightAnimator = new ObjectAnimator();
	
	private AnimatorSet mDawnAnimationSet = new AnimatorSet();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		
		Resources resources = getResources();
		mBlueSkyColor = resources.getColor( R.color.blue_sky );
		mSunsetSkyColor = resources.getColor( R.color.sunset_sky );
		mNightSkyColor = resources.getColor( R.color.night_sky );

		mSkyView = findViewById( R.id.sky );
		mSeaView = findViewById( R.id.sea );
		mSunView = findViewById( R.id.sun );

		mSceneView = findViewById( R.id.scene );
		mSceneView.setOnClickListener( new View.OnClickListener()
		{
			@Override
			public void onClick( View v )
			{
				startAnimation();
			}
		} );
    }
		
	private void startAnimation()
	{
		float sunYUp = mSeaView.getY()/2 - mSunView.getHeight()/2;
		float sunYDown = mSeaView.getY();

		mDawnAnimationSet.cancel();
		
		float fractDay = 1 - mSunAnimator.getAnimatedFraction();
		float fractNight = 1 - mNightAnimator.getAnimatedFraction();

		if(isLastTimeSunGoesUp){
			initAnimators( sunYUp, sunYDown, mBlueSkyColor, mSunsetSkyColor, mSunsetSkyColor, mNightSkyColor );

			mDawnAnimationSet = new AnimatorSet();
			mDawnAnimationSet
				.play(mSunAnimator)
				.with(mDayAnimator)
				.before(mNightAnimator);

			if( fractDay > 0 & fractDay < 1 ){
				mSunAnimator.setCurrentFraction(fractDay);
				mDayAnimator.setCurrentFraction(fractDay);
			}else if( fractNight > 0 & fractNight < 1 ){
				mSunAnimator.setCurrentFraction(1);
				mDayAnimator.setCurrentFraction(1);
				mNightAnimator.setCurrentFraction(fractNight);
			}
			
			isLastTimeSunGoesUp = false;
		}else{
			initAnimators( sunYDown, sunYUp, mSunsetSkyColor, mBlueSkyColor, mNightSkyColor, mSunsetSkyColor );

			mDawnAnimationSet = new AnimatorSet();
			mDawnAnimationSet
				.play(mSunAnimator)
				.with(mDayAnimator)
				.after(mNightAnimator);

			if( fractNight > 0 & fractNight < 1 ){
				mNightAnimator.setCurrentFraction(fractNight);
			}else if( fractDay > 0 & fractDay < 1 ){
				mSunAnimator.setCurrentFraction(fractDay);
				mDayAnimator.setCurrentFraction(fractDay);
				mNightAnimator.setCurrentFraction(1);
			}
			
			isLastTimeSunGoesUp = true;
		}

		mDawnAnimationSet.start();	
	}

	private void initAnimators(float sunStartY, float sunEndY, int daySkyColorStart, int daySkyColorEnd, int nightSkyColorStart, int nightSkyColorEnd )
	{
		mSunAnimator = ObjectAnimator.ofFloat(mSunView, "y", sunStartY, sunEndY)
			.setDuration(3000);
		mSunAnimator.setInterpolator( new AccelerateDecelerateInterpolator() );

		mDayAnimator = ObjectAnimator
			.ofInt(mSkyView, "backgroundColor", daySkyColorStart, daySkyColorEnd)
			.setDuration(3000);
		mDayAnimator.setEvaluator(new ArgbEvaluator());

		mNightAnimator = ObjectAnimator
			.ofInt(mSkyView, "backgroundColor", nightSkyColorStart, nightSkyColorEnd)
			.setDuration(1500);
		mNightAnimator.setEvaluator(new ArgbEvaluator());
	}
}

