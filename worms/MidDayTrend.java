package com.wormtrader.custom.worms;
/********************************************************************
* @(#)MidDayTrend.java 1.00 20100426
* Copyright © 2010 by Richard T. Salamone, Jr. All rights reserved.
*
* MidDayTrend:
*
* @author Rick Salamone
* @version 1.00
* 20100426 rts created
*******************************************************/
import com.wormtrader.bars.Bar;
import com.wormtrader.bars.BarSize;
import com.wormtrader.positions.TapeWorm;

public final class MidDayTrend
	extends TapeWorm
	{
	public static final String ENTRY_TIME = "13:00";

	public String getShortName() { return ENTRY_TIME; }
	public BarSize getMaxBarSize() { return BarSize.THIRTY_MIN; }

	public String getMetrics ( int index, Bar bar )	// called  before automata()
		{
		return bar.hhmm();
		}

	public void automata( int index, Bar bar )
		{
		String hhmm = bar.hhmm();

		if ( hhmm.equals( ENTRY_TIME ))
			{
			int last = bar.getClose();
			int open = getTape().get(0).getOpen();
			if ( last > open )
				goLong();
			else
				goShort();
			}
		}
	}