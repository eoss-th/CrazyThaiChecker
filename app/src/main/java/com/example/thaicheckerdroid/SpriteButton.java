package com.example.thaicheckerdroid;

import android.graphics.Bitmap;

public class SpriteButton extends SpriteBitmap {
	
	private Bitmap normalBitmap;
	private Bitmap pressedBitmap;
	private ButtonClickListener listener;
	private boolean pressing;

	public SpriteButton(Bitmap bitmap, Bitmap pressedBitmap, int x, int y) {
		super(bitmap, x, y);
		
		this.normalBitmap = bitmap;
		this.pressedBitmap = pressedBitmap;
	}
	
	public void onPressed() {
		super.setBitmap(this.pressedBitmap);
		pressing = true;
	}
	
	public void onReleased() {
		super.setBitmap(this.normalBitmap);
		
		if (listener!=null)
			listener.onClicked(this);
		
		pressing = false;
	}
	
	public boolean isPressing() {
		return pressing;
	}
	
	public void setOnClickListener(ButtonClickListener listener) {
		this.listener = listener;
	}
	

}
