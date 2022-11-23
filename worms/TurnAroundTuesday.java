package com.wormtrader.custom.worms;
/*
 * TurnAroundTuesday.java
 *
 * GAMBARU - Never Give Up (Japanese)
 *
 */
import com.wormtrader.bars.Bar;
import com.wormtrader.bars.BarSize;
import com.wormtrader.positions.TapeWorm;
import com.shanebow.util.SBDate;
import com.shanebow.util.SBFormat;

public final class TurnAroundTuesday extends TapeWorm
	{
	private final int THRESHOLD = 10;

	public String getShortName()   { return "TUE"; }
	public BarSize getMinBarSize() { return BarSize.ONE_DAY; }

	public String getMetrics ( int index, Bar bar )	// called  before automata()
		{																			// if debugging
		return SBDate.DDD(bar.getTime())
				+ " change: " + SBFormat.toDollarString(change(index,bar));
		}

	private final int change( int index, Bar bar )
		{
		int prevClose = ( index > 0 ) ? getTape().get(index-1).getClose()
								: bar.getOpen(); // 1st data point use the open as proxy for prev close
		return bar.getClose() - prevClose;
		}

	public void automata( int index, Bar bar )
		{
		int close = bar.getClose();
		String dayOfWeek = SBDate.DDD(bar.getTime());
		if ( dayOfWeek.equals("MON")) // see if monday was up or down
			{
			int change = change(index, bar);
			if ( change < -THRESHOLD )
				goLong();	 // monday is down, go long (reversal) tuesday
			else if ( change > THRESHOLD )
				goShort();
			}
		else if ( getQty() != 0 ) // just hold it for a day
			goFlat();
		}
	}