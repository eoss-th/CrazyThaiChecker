package com.example.thaicheckerdroid;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class SpriteCircle {
	private Paint fillPaint;
	private Paint borderPaint;
	private int x;
	private int y;
	private int size;

	public SpriteCircle(int x, int y, int size, int fillColor, int borderColor) {
		this.x = x;
		this.y = y;
		this.size = size;

		fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		fillPaint.setColor(fillColor);
		fillPaint.setStyle(Paint.Style.FILL_AND_STROKE);

		borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		borderPaint.setColor(borderColor);
		borderPaint.setStyle(Paint.Style.STROKE);
		borderPaint.setStrokeWidth(5);
	}

    public SpriteCircle(SpriteCircle parent) {
        this(parent.x, parent.y, parent.size, parent.fillPaint.getColor(), parent.borderPaint.getColor());
    }

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

    public void center(int x, int y) {
        this.x = x - this.size / 2;
        this.y = y - this.size / 2;
    }

    public void set(int x, int y) {
        this.x = x;
        this.y = y;
    }

	public void moveTo (int offsetX, int offsetY) {
		this.x += offsetX;
		this.y += offsetY;
	}

	public void draw(Canvas canvas) {
		canvas.drawOval(new RectF(x, y, x + size, y + size), fillPaint);
		canvas.drawOval(new RectF(x, y, x + size, y + size), borderPaint);
	}

	public void update() {}
	
	public boolean contains(int x, int y) {
		if (x < this.x - size / 2) return false;
		if (x > this.x + size / 2) return false;
		if (y < this.y - size / 2) return false;
		if (y > this.y + size / 2) return false;
		return true;
	}
}
