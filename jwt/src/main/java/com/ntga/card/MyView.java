package com.ntga.card;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class MyView extends View {
	Paint mPaint = null;
	public PersonInfo mPerson = null;

	public MyView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}

	public MyView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public MyView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	void init() {
		mPerson = new PersonInfo();
		mPaint = new Paint();
	}

	void drawText2(Canvas canvas, String text, float x, float y, float width,
			float yStep, Paint paint) {
		int start = 0, end, count;

		end = text.length();
		if (end == 0)
			return;
		do {
			count = paint.breakText(text, start, end, true, width, null);
			canvas.drawText(text, start, start + count, x, y, paint);
			start += count;
			y += yStep;
		} while (start < end);
	}

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int density = canvas.getDensity();
		float dip = (float) density / (float) 160;
		mPaint.setTextSize(18 * dip);
		mPaint.setColor(Color.BLUE);
		canvas.drawText("姓名", 0 * dip, 20 * dip, mPaint);
		canvas.drawText("性别", 0 * dip, 40 * dip, mPaint);
		canvas.drawText("民族", 0 * dip, 60 * dip, mPaint);
		canvas.drawText("出生", 0 * dip, 80 * dip, mPaint);
		canvas.drawText("地址", 0 * dip, 200 * dip, mPaint);
		canvas.drawText("公民身份号码", 0 * dip, 240 * dip, mPaint);
		canvas.drawText("签发机关", 0 * dip, 260 * dip, mPaint);
		canvas.drawText("有效期限", 0 * dip, 280 * dip, mPaint);
		mPaint.setColor(Color.BLACK);
		canvas.drawText(mPerson.name, 40 * dip, 20 * dip, mPaint);
		canvas.drawText(mPerson.sex, 40 * dip, 40 * dip, mPaint);
		canvas.drawText(mPerson.nation, 40 * dip, 60 * dip, mPaint);
		canvas.drawText(mPerson.birthday2, 40 * dip, 80 * dip, mPaint);
		drawText2(canvas, mPerson.address, 40 * dip, 200 * dip, 280 * dip,
				18 * dip, mPaint);
		canvas.drawText(mPerson.idNum, 114 * dip, 240 * dip, mPaint);
		canvas.drawText(mPerson.authority, 78 * dip, 260 * dip, mPaint);
		canvas.drawText(mPerson.validDate2, 78 * dip, 280 * dip, mPaint);
		if (mPerson.photo != null) {
			RectF dstRect = new RectF();
			dstRect.left = 180 * dip;
			dstRect.top = 10 * dip;
			dstRect.right = dstRect.left + mPerson.photo.getWidth() * density
					/ 120;
			dstRect.bottom = dstRect.top + mPerson.photo.getHeight() * density
					/ 120;
			canvas.drawBitmap(mPerson.photo, null, dstRect, mPaint);
		}
	}
}
