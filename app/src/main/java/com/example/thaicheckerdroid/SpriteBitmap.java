package com.example.thaicheckerdroid;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class SpriteBitmap {
	private Bitmap bitmap;
	private int x;
	private int y;
	private int width;
	private int height;

	public SpriteBitmap(Bitmap bitmap, int x, int y) {
		this.bitmap = bitmap;
		this.x = x;
		this.y = y;
		this.width = bitmap.getWidth();
		this.height = bitmap.getHeight();
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public void draw(Canvas canvas) {
		canvas.drawBitmap(bitmap, x - (width / 2),
				y - (height / 2), null);
	}

	public void update() {}
	
	public boolean contains(int x, int y) {
		if (x < this.x - width / 2) return false;
		if (x > this.x + width / 2) return false;
		if (y < this.y - height / 2) return false;
		if (y > this.y + height / 2) return false;
		return true;
	}
}
