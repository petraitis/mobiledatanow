package com.framedobjects.dashwell.tests;

public class PassO{ 
public static void main(String args[]){
	PassO p = new PassO();
	p.start();
	}
	void start() {
	Two t = new Two();
	System.out.print(t.x + " ");
	Two t2 = fix(t);
	System.out.println(t.x + " " + t2.x);
	}
	private Two fix(Two tt) {
	tt.x = 42;
	tt = new Two();
	return tt;
	}
	}
	class Two {
	int x;
	}
