package com.wormtrader.custom.scans;
/********************************************************************
* @(#)TurtleSoup.java 1.00 20121115
* Copyright © 2012 by Richard T. Salamone, Jr. All rights reserved.
*
* TurtleSoup: Scans for stocks that meet Linda Raschke's Turtle Soup
* Plus One criterion: A new 20-day high or low at least 3 days after
* the prior high or low.
*
* @author Rick Salamone
* @version 1.00
* 20121115 rts created
*******************************************************/
import com.wormtrader.history.Scanner;
import com.wormtrader.bars.Bar;
import com.wormtrader.bars.BarList;
import com.wormtrader.history.Tape;

public final class TurtleSoup
	extends Scanner
	{
	private static final int period = 20;
	private static final int priorBackMin = 3;
	@Override public String toString() { return "LBR Turtle Soup Plus One"; }

	@Override public boolean isHit(Tape tape)
		{
		BarList bars = tape.getBars();
		int index = bars.size() - 1;
		return (index >= period-1)		
		    && (isLow(bars,index) || isHigh(bars, index));
		}

	private boolean isLow(BarList bars, int index)
		{
		Bar today = bars.get(index);
		int todayLow = today.getLow();

		Bar bar = bars.get(index-1);
		int priorLow = bar.getLow();
		int priorLowBack = 1; // number of bars back to prior low

		for ( int i = 1; i < period; i++ )
			{
			bar = bars.get(--index);
			int low = bar.getLow();
			if ( low < todayLow ) // today is not a new low
				return false;
			if ( low < priorLow )
				{
				priorLow = low;
				priorLowBack = i;
				}
			}
// System.out.println(symbol + " prior low " + priorLow + " " + priorLowBack + " bars back");
		return priorLowBack >= priorBackMin;
		}

	private boolean isHigh(BarList bars, int index)
		{
		Bar today = bars.get(index);
		int todayHigh = today.getHigh();

		Bar bar = bars.get(index-1);
		int priorHigh = bar.getHigh();
		int priorBack = 1; // number of bars back to prior low

		for ( int i = 1; i < period; i++ )
			{
			bar = bars.get(--index);
			int high = bar.getHigh();
			if ( high > todayHigh ) // today is not a new high
				return false;
			if ( high > priorHigh )
				{
				priorHigh = high;
				priorBack = i;
				}
			}
//System.out.println(symbol + " prior high " + priorHigh + " " + priorBack + " bars back");
		return priorBack >= priorBackMin;
		}
	}