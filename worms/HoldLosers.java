package com.wormtrader.custom.worms;
/********************************************************************
* @(#)HoldLosers.java 1.00 20130417
* Copyright © 2013 by Richard T. Salamone, Jr. All rights reserved.
*
* HoldLosers: Implements the Bill Williams "First Wise Man" as
* described in Trading Chaos 2nd Edition Chapter 9.
*
* @author Rick Salamone
* @version 1.00
* 20130417 rts created
*******************************************************/
import com.wormtrader.bars.Bar;
import com.wormtrader.bars.BarSize;
import com.wormtrader.positions.TapeWorm;

public final class HoldLosers extends TapeWorm
	{
	public static final String CLOSE_TIME = "15:50";

	public String getShortName() { return "HoldLoss"; }
	public BarSize getMaxBarSize() { return BarSize.FIVE_MIN; }

	public String getMetrics ( int index, Bar bar )	// called  before automata()
		{
		return bar.hhmm();
		}

	public void automata( int index, Bar bar )
		{
		String hhmm = bar.hhmm();

		if ( hhmm.equals( CLOSE_TIME ))
			{
			if (getLeg().getUnreal() > 0)
				goFlat("-EOD");
			}
		}
	}