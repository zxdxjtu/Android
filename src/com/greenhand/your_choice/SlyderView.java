package com.greenhand.your_choice;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.RotateAnimation;

public class SlyderView	extends View 
{
	public SlyderView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	public SlyderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public SlyderView(Context context) {
		super(context);
		init(context);
	}

	/**
	 * �ָ�Ķ���
	 */
	private int[] drgrees = { 60, 60, 60, 60, 60, 60 };
	/***
	 * �ָ������
	 */
	private String[] strs = { "", "", "", "", "",""};
	/**
	 * �ָ����ɫ
	 */
	private int[] colos = new int[] {0xfed9c960,0xfe57c8c8,0xfe9fe558,0xfef6b000,0xfef46212,0xfecf2911,0xfe9d3011 };
	/**
	 * ����
	 */
	private Paint paint;
	/**
	 * ���ֵĴ�С
	 */
	private float textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, getResources().getDisplayMetrics());
	/**
	 * ���ֵ���ɫ
	 */
	private int textcolor = Color.WHITE;
	/**
	 * ԰�İ뾶
	 */
	private float radius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 140, getResources().getDisplayMetrics());
	/**
	 * �����ֵľ���
	 */
	private float textdis = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 130, getResources().getDisplayMetrics());
	/**
	 * Բ��
	 */
	private float centerX;
	/**
	 * Բ��
	 */
	private float centerY;
	
	RotateAnimation rotateAnimation;

	private void init(Context context) 
	{
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Style.FILL);
		paint.setColor(Color.WHITE);
		centerX = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, getResources().getDisplayMetrics());
		centerY = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, getResources().getDisplayMetrics());
//		nowAngle = 0;
	}
	/**
	 * ���ƾ��ο�
	 */
	private RectF oval;

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		oval = new RectF(centerX - radius, centerY - radius, centerX + radius,centerY + radius);
		float start = 0;
		paint.setColor(Color.rgb(0xdd, 0xdd, 0xdd));
		paint.setAlpha(127);
		canvas.drawCircle(centerX, centerY, radius, paint);
		paint.setAlpha(255);
		// ������
		paint.setAntiAlias(true);
		for (int i = 0; i < drgrees.length; i++) {
			float sweepAngle = drgrees[i];
			float startAngle = start;
			paint.setColor(colos[i % colos.length]);
			canvas.drawArc(oval, startAngle, sweepAngle, true, paint);
			start += drgrees[i];
		}
		// ������
		paint.setColor(textcolor);
		paint.setAntiAlias(true);
		paint.setTextSize(textSize);
		paint.setTextAlign(Paint.Align.RIGHT);
		start = 0;
		for (int i = 0; i < drgrees.length; i++) {
			canvas.save();
			canvas.rotate(start + drgrees[i] / 2+7, centerX, centerY);
			canvas.drawText(strs[i], centerX + textdis, centerY, paint);
			canvas.restore();
			start += drgrees[i];
		}
		int saveCount = canvas.save();
	}
//	public void showAnimation(View mView) 
//	 {
//		float x = centerX;
//		float y = centerY;
//	  //�����������Ҫ��ת�ĽǶȣ������õ���1080��
//		int rand = (int) (Math.random()*360);
//		rand = 1080 + rand;
//		rotateAnimation = new RotateAnimation(nowAngle,rand,x,y);
//	  //���������ͨ��ʱ���
//		rotateAnimation.setDuration(1000*4);
//		rotateAnimation.setFillAfter(true);
//		mView.startAnimation(rotateAnimation);
//		nowAngle = rand % 360;
//		
//	 }
	
	//���÷������ε�����
	public void setdrgrees(ArrayList<String> list)
	{
		int num = list.size();
		drgrees = new int[num];
		strs = new String[num];
		for(int i=0;i<num;i++)
		{
			drgrees[i] = 360/num;
			strs[i] = list.get(i);
			if(strs[i].length() > 4)
			{
				strs[i] = strs[i].substring(0, 3) + "��";
			}
		}
		int last = 360-drgrees[0]*num;
		for(int i=0;i<last;i++)
		{
			drgrees[i]++;
		}
	}
}
