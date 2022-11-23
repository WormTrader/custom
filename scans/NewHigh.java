package com.wormtrader.custom.scans;
/********************************************************************
* @(#)NewHigh.java 1.00 20090719
* Copyright © 2007-2012 by Richard T. Salamone, Jr. All rights reserved.
*
* NewHigh: Scans the current universe for stocks that have made a new
* 21 day high.
*
* @author Rick Salamone
* @version 1.00
* 20090719 rts
*******************************************************/
import com.wormtrader.history.Scanner;
import com.wormtrader.bars.Bar;
import com.wormtrader.history.Tape;

public final class NewHigh extends Scanner
	{
	private static final int period = 20;
	public String toString() { return "New " + period + "-day high of closes"; }

	@Override public boolean isHit(Tape tape)
		{
		int index = tape.size() - 1;
		int todayClose = tape.get(index).getClose();
		for ( int i = 0; i < period; i++ )
			if ( tape.get(--index).getClose() >= todayClose )
				return false;
		return true;
		}
	}