/*
 *   Copyright 2012 Shell M. Shrader
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package com.shellware.adaptronic.adaptive.tuner.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.shellware.adaptronic.adaptive.tuner.logging.AdaptiveLogger;

public class GaugeNeedle extends ImageView {

	private static AdaptiveLogger logger = new AdaptiveLogger(AdaptiveLogger.DEFAULT_LEVEL, AdaptiveLogger.DEFAULT_TAG);

	private final int NOT_SET_YET = Integer.MAX_VALUE;
	
	private int offsetCenterInDegrees = NOT_SET_YET;
//	private float lastValue; 
	
	private int minValue = 0;
	private int maxValue = 360;
	private int minDegrees = -180;
	private int maxDegrees = 180;
	private float pivotPoint = .5f;

	public GaugeNeedle(Context context) {
		super(context);
	}
	public GaugeNeedle(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	public GaugeNeedle(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setValue(final float value) {
		
		// calculate our center point if it has not yet been calculated
		if (offsetCenterInDegrees == NOT_SET_YET) {
			offsetCenterInDegrees = maxDegrees - (maxDegrees - minDegrees) / 2 ;
		}
		
		final float scale = (float) (maxDegrees - minDegrees) / (maxValue - minValue) ;
		float newValue = (value - minValue) * scale - ((maxDegrees - minDegrees) / 2) + offsetCenterInDegrees;
		
		// peg the needle if our values are significantly beyond our borders (20+ degrees)
		if (newValue > maxDegrees + 20) newValue = maxDegrees + 20;
		if (newValue < minDegrees - 20) newValue = minDegrees - 20;

//		final RotateAnimation rotateAnimation = new RotateAnimation(lastValue, newValue, 
//																	  Animation.RELATIVE_TO_SELF, 0.5f, 
//																	  Animation.RELATIVE_TO_SELF, pivotPoint);
//
//		rotateAnimation.setInterpolator(new LinearInterpolator());
//		rotateAnimation.setDuration(100);
//		
//		rotateAnimation.setFillEnabled(true);	
//		rotateAnimation.setFillAfter(true);
//		
//		startAnimation(rotateAnimation);
		
		setPivotY(getHeight() * pivotPoint);
		setPivotX(getWidth() / 2);
		
		setRotation(newValue);
				
//		lastValue = newValue;
		logger.log(String.format("GaugeNeedle %s - %s - %s", String.valueOf(value), String.valueOf(newValue), String.valueOf(scale)));
	}
	
	public int getMinValue() {
		return minValue;
	}
	public void setMinValue(int minValue) {
		this.minValue = minValue;
	}
	public int getMaxValue() {
		return maxValue;
	}
	public void setMaxValue(int maxValue) {
		this.maxValue = maxValue;
	}
	public int getMinDegrees() {
		return minDegrees;
	}
	public void setMinDegrees(int minDegrees) {
		this.minDegrees = minDegrees;
	}
	public int getMaxDegrees() {
		return maxDegrees;
	}
	public void setMaxDegrees(int maxDegrees) {
		this.maxDegrees = maxDegrees;
	}
	public float getPivotPoint() {
		return pivotPoint;
	}
	public void setPivotPoint(float pivotPoint) {
		this.pivotPoint = pivotPoint;
	}
}
