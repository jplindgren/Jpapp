package com.jplindgren.jpapp.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;

import com.jplindgren.jpapp.R;

public class CompassView extends View{
	private Paint markerPaint;
	private Paint textPaint;
	private Paint circlePaint;
	private int textHeight;
	int[] borderGradientColors;
	float[] borderGradientPositions;
	int[] glassGradientColors;
	float[] glassGradientPositions;
	int skyHorizonColorFrom;
	int skyHorizonColorTo;
	int groundHorizonColorFrom;
	int groundHorizonColorTo;
	
	private enum CompassDirection { N, NNE, NE, ENE,
		E, ESE, SE, SSE,
		S, SSW, SW, WSW,
		W, WNW, NW, NNW }
	
	public CompassView(Context context) {
		super(context);
		initCompassView();
	}
	public CompassView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initCompassView();
	}
	public CompassView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initCompassView();
	}
	
	protected void initCompassView() {
		setFocusable(true);
		// Get external resources
		Resources r = this.getResources();
		circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		circlePaint.setColor(r.getColor(R.color.background_color));
		circlePaint.setStrokeWidth(1);
		circlePaint.setStyle(Paint.Style.STROKE);
		textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		textPaint.setColor(r.getColor(R.color.text_color));
		textPaint.setFakeBoldText(true);
		textPaint.setSubpixelText(true);
		textPaint.setTextSize(25);
		textPaint.setTextAlign(Align.LEFT);
		textHeight = (int)textPaint.measureText("yY");
		markerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		markerPaint.setColor(r.getColor(R.color.marker_color));
		markerPaint.setAlpha(200);
		markerPaint.setStrokeWidth(1);
		markerPaint.setStyle(Paint.Style.STROKE);
		markerPaint.setShadowLayer(2, 1, 1, r.getColor(R.color.shadow_color));
		
		glassGradientColors = new int[5];
		glassGradientPositions = new float[5];
		int glassColor = 245;
		glassGradientColors[4] = Color.argb(65, glassColor,glassColor, glassColor);
		glassGradientColors[3] = Color.argb(100, glassColor,glassColor, glassColor);
		glassGradientColors[2] = Color.argb(50, glassColor,glassColor, glassColor);
		glassGradientColors[1] = Color.argb(0, glassColor,glassColor, glassColor);
		glassGradientColors[0] = Color.argb(0, glassColor,glassColor, glassColor);
		glassGradientPositions[4] = 1-0.0f;
		glassGradientPositions[3] = 1-0.06f;
		glassGradientPositions[2] = 1-0.10f;
		glassGradientPositions[1] = 1-0.20f;
		glassGradientPositions[0] = 1-1.0f;
		
		skyHorizonColorFrom = r.getColor(R.color.horizon_sky_from);
		skyHorizonColorTo = r.getColor(R.color.horizon_sky_to);
		groundHorizonColorFrom = r.getColor(R.color.horizon_ground_from);
		groundHorizonColorTo = r.getColor(R.color.horizon_ground_to);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		float ringWidth = textHeight + 15;
		int height = getMeasuredHeight();
		int width =getMeasuredWidth();
		int px = width/2;
		int py = height/2;
		Point center = new Point(px, py);
		int radius = Math.min(px, py)-2;
		RectF boundingBox = new RectF(center.x - radius,
									center.y - radius,
									center.x + radius,
									center.y + radius);
		RectF innerBoundingBox = new RectF(center.x - radius + ringWidth,
											center.y - radius + ringWidth,
											center.x + radius - ringWidth,
											center.y + radius - ringWidth);
		float innerRadius = innerBoundingBox.height()/2;
		
		Paint pgb = new Paint();
		Path outerRingPath = new Path();
		outerRingPath.addOval(boundingBox, Direction.CW);
		canvas.drawPath(outerRingPath, pgb);		
		
		float tiltDegree = pitch;
		while (tiltDegree > 90 || tiltDegree < -90){
			if (tiltDegree > 90) tiltDegree = -90 + (tiltDegree - 90);
			if (tiltDegree < -90) tiltDegree = 90 - (tiltDegree + 90);
		}
		float rollDegree = roll;
		while (rollDegree > 180 || rollDegree < -180){
			if (rollDegree > 180) rollDegree = -180 + (rollDegree - 180);
			if (rollDegree < -180) rollDegree = 180 - (rollDegree + 180);
		}
		
		canvas.save();
		canvas.rotate(-1*(bearing), px, py);
		
		/*
		 * teste para setar longitude latitude
		 */
		heading = bearing - (bearing + heading);
		int testeNewHeading = Math.round(-heading / 360 + 180);
		canvas.rotate(testeNewHeading, px, py);
		/*
		canvas.drawLine(px, py,
				   (float)(px + px * Math.sin((double)(-bearing) * 3.14/180)),
				   (float)(px - radius * Math.cos((double)(-bearing) * 3.14/180)),
				   markerPaint);
		*/
		int markWidth = radius / 3;
		int startX = center.x - markWidth;
		int endX = center.x + markWidth;
		
		double h = innerRadius*Math.cos(Math.toRadians(90-tiltDegree));
		double justTiltY = center.y - h;
		
		float pxPerDegree = (innerBoundingBox.height()/2)/45f;
		
		for (int i = 90; i >= -90; i -= 10) {
			double ypos = justTiltY + i*pxPerDegree;
			// Only display the scale within the inner face.
			if ((ypos < (innerBoundingBox.top + textHeight)) ||(ypos > innerBoundingBox.bottom - textHeight))
				continue;
			// Draw a line and the tilt angle for each scale increment.
			canvas.drawLine(startX, (float)ypos, endX, (float)ypos,markerPaint);
			int displayPos = (int)(tiltDegree - i);
			String displayString = String.valueOf(displayPos);
			float stringSizeWidth = textPaint.measureText(displayString);
			canvas.drawText(displayString, (int)(center.x-stringSizeWidth/2),(int)(ypos)+1,textPaint);
		}
				
		markerPaint.setStrokeWidth(2);
		canvas.drawLine(center.x - radius / 2,(float)justTiltY,center.x + radius / 2,(float)justTiltY,markerPaint);
		markerPaint.setStrokeWidth(1);
		
		// Draw the arrow
		Path rollArrow = new Path();
		rollArrow.moveTo(center.x - 3, (int)innerBoundingBox.top + 14);
		rollArrow.lineTo(center.x, (int)innerBoundingBox.top + 10);
		rollArrow.moveTo(center.x + 3, innerBoundingBox.top + 14);
		rollArrow.lineTo(center.x, innerBoundingBox.top + 10);
		canvas.drawPath(rollArrow, markerPaint);
		// Draw the string
		String rollText = String.valueOf(rollDegree);
		double rollTextWidth = textPaint.measureText(rollText);
		canvas.drawText(rollText,(float)(center.x - rollTextWidth / 2),innerBoundingBox.top + textHeight + 2,textPaint);
		canvas.restore();
		
		canvas.save();
		canvas.rotate(180, center.x, center.y);
		for (int i = -180; i < 180; i += 10){
			// Show a numeric value every 30 degrees
			if (i % 30 == 0) {
				String rollString = String.valueOf(i*-1);
				float rollStringWidth = textPaint.measureText(rollString);
				PointF rollStringCenter = new PointF(center.x-rollStringWidth/2, innerBoundingBox.top+1+textHeight);
				canvas.drawText(rollString, rollStringCenter.x, rollStringCenter.y,textPaint);
			}else { 		// Otherwise draw a marker line
				canvas.drawLine(center.x, (int)innerBoundingBox.top,
						center.x, (int)innerBoundingBox.top + 5, markerPaint);
			}
			canvas.rotate(10, center.x, center.y);
		}
		canvas.restore();
		
		canvas.save();
		//canvas.rotate(-1*(bearing), px, py);
		canvas.rotate(heading, px, py);
		// Should this be a double?
		double increment = 22.5;
		for (double i = 0; i < 360; i += increment) {
			CompassDirection cd = CompassDirection.values()[(int)(i / 22.5)];
			String headString = cd.toString();
			float headStringWidth = textPaint.measureText(headString);
			PointF headStringCenter = new PointF(center.x - headStringWidth / 2,boundingBox.top + 1 + textHeight);
			if (i % increment == 0)
				canvas.drawText(headString,headStringCenter.x, headStringCenter.y,textPaint);
			else
				canvas.drawLine(center.x, (int)boundingBox.top,center.x, (int)boundingBox.top + 3,markerPaint);
			canvas.rotate((int)increment, center.x, center.y);
		}
		canvas.restore();
		
		RadialGradient glassShader = new RadialGradient(px, py, (int)innerRadius,glassGradientColors,glassGradientPositions,TileMode.CLAMP);
		Paint glassPaint = new Paint();
		glassPaint.setShader(glassShader);
		canvas.drawOval(innerBoundingBox, glassPaint);
		// Draw the outer ring
		canvas.drawOval(boundingBox, circlePaint);
		// Draw the inner ring
		circlePaint.setStrokeWidth(2);
		canvas.drawOval(innerBoundingBox, circlePaint);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// The compass is a circle that fills as much space as possible.
		// Set the measured dimensions by figuring out the shortest boundary,
		// height or width.
		int measuredWidth = measure(widthMeasureSpec);
		int measuredHeight = measure(heightMeasureSpec);
		int d = Math.min(measuredWidth, measuredHeight);
		setMeasuredDimension(d, d);
	}
	
	private int measure(int measureSpec) {
		int result = 0;
		// Decode the measurement specifications.
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);
		if (specMode == MeasureSpec.UNSPECIFIED) {
			// Return a default size of 200 if no bounds are specified.
			result = 200;
		} else {
			// As you want to fill the available space
			// always return the full available bounds.
			result = specSize;
		}
		return result;
	}
	
	
	private float heading;
	///Responsavel pela movimentacao da bússola
	public void setHeading(float _heading) {
		bearing = _heading;
	}
	public float getHeading() {
		return bearing;
	}
	private float bearing;
	///Responsavel pela movimentacao da bússola
	public void setBearing(float _bearing) {
		bearing = _bearing;
		sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED);
	}
	public float getBearing() {
		return bearing;
	}
	private float pitch;
	public void setPitch(float _pitch) {
		pitch = _pitch;
		sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED);
	}
	public float getPitch() {
		return pitch;
	}
	private float roll;
	///Responsavel pela movimentacao das marcas centrais
	public void setRoll(float _roll) {
		roll = _roll;
		sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED);
	}
	public float getRoll() {
		return roll;
	}
	
	@Override
	public boolean dispatchPopulateAccessibilityEvent(final AccessibilityEvent event) {
		super.dispatchPopulateAccessibilityEvent(event);
		if (isShown()) {
			String bearingStr = String.valueOf(bearing);
			if (bearingStr.length() > AccessibilityEvent.MAX_TEXT_LENGTH)
				bearingStr = bearingStr.substring(0, AccessibilityEvent.MAX_TEXT_LENGTH);
				event.getText().add(bearingStr);
				return true;
		}else{
			return false;
		}
	}
}// class
