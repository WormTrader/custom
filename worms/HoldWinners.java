package com.wormtrader.custom.worms;
/********************************************************************
* @(#)HoldWinners.java 1.00 20130501
* Copyright © 2013 by Richard T. Salamone, Jr. All rights reserved.
*
* HoldWinners: Exits losing positions at market close.
*
* @author Rick Salamone
* @version 1.00
* 20130501 rts created
*******************************************************/
import com.wormtrader.bars.Bar;
import com.wormtrader.bars.BarSize;
import com.wormtrader.positions.TapeWorm;

public final class HoldWinners extends TapeWorm
	{
	public static final String CLOSE_TIME = "15:55";

	public String getShortName() { return "HoldWin"; }
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
			if (getLeg().getUnreal() < 0)
				goFlat("-EOD");
			}
		}
	}