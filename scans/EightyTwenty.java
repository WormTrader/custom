package com.wormtrader.custom.scans;
/********************************************************************
* @(#)EightyTwenty.java 1.00 20121115
* Copyright © 2012 by Richard T. Salamone, Jr. All rights reserved.
*
* EightyTwenty: Scans the current universe for stocks that satisfy
* Linda Bradford Raschke's 80-20 setup from the Street Smarts book.
*
* @author Rick Salamone
* @version 1.00
* 20121115 rts created
*******************************************************/
import com.wormtrader.history.Scanner;
import com.wormtrader.bars.Bar;
import com.wormtrader.history.Tape;

public class EightyTwenty
	extends Scanner
	{
	@Override public String toString() { return "LBR 80-20"; }

	@Override public boolean isHit(Tape tape)
		{
		Bar bar = tape.lastBar();
		int range = bar.range(); // most recent range
		int twentyPercent = 20 * range / 100;
		if (twentyPercent <= 2)
			return false;
		return ((bar.getOpen() > bar.getHigh()-twentyPercent)
		         && (bar.getClose() < bar.getLow()+twentyPercent))
		    || ((bar.getOpen() < bar.getLow()+twentyPercent)
		         && (bar.getClose() > bar.getHigh()-twentyPercent));
		}
	}