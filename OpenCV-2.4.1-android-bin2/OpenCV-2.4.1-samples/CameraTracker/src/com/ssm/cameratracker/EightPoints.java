package com.ssm.cameratracker;

import java.util.Random;

import android.util.Log;

public class EightPoints {
	public Point top_left,top_right,bottom_left,bottom_right;
	private Random r;
	public EightPoints() {
		r = new Random();
		top_left = new Point();
		top_right = new Point();
		bottom_left = new Point();
		bottom_right = new Point();
	}
	class Point {
		public int x,y;
		
		public Point() {
			x = 0;
			y = 0;
		}
	}
	
	public void randPoint(){
		{
			top_left.x = r.nextInt(10);
			top_left.y = r.nextInt(10);
			top_right.x = r.nextInt(10);
			top_right.y = r.nextInt(10);
			bottom_left.x = r.nextInt(10);
			bottom_left.y = r.nextInt(10);
			bottom_right.x = r.nextInt(10);
			bottom_right.y = r.nextInt(10);
			
		}
	}
	
	public void setPoints(int points[]){
		points[0] = top_left.x;
		points[1] = top_left.y;
		points[2] = top_right.x;
		points[3] = top_right.y;

		points[4] = bottom_left.x;
		points[5] = bottom_left.y;
		points[6] = bottom_right.x;
		points[7] = bottom_right.y;
		
	}
	
	public void printPoints(){
		Log.i("sysout", "top_left"+top_left+"top_right"+top_right+"bottom_left"+bottom_left+"bottom_right"+bottom_right);
	}
}
