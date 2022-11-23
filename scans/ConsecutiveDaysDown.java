package com.wormtrader.custom.scans;
/********************************************************************
* @(#)ConsecutiveDaysDown.java 1.00 20090719
* Copyright © 2007-2012 by Richard T. Salamone, Jr. All rights reserved.
*
* ConsecutiveDaysDown: Scans the current universe for stocks where each
* day has closed lower than the previous day for (at least) the specified
* number of consecutive days.
*
* @author Rick Salamone
* @version 1.00
* 20120710 rts created
*******************************************************/
import com.wormtrader.history.Scanner;
import com.wormtrader.bars.Bar;
import com.wormtrader.history.Tape;

public final class ConsecutiveDaysDown extends Scanner
	{
	private static final int period = 5;
	public String toString() { return "" + period + " Days Down"; }

	@Override public boolean isHit(Tape tape)
		{
		int index = tape.size() - 1;
		int close = tape.get(index).getClose();
		for ( int i = 0; i < period; i++ )
			{
			int prevClose = tape.get(--index).getClose();
			if ( prevClose < close )
				return false;
			close = prevClose;
			}
		return true;
		}
	}