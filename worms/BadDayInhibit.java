package com.wormtrader.custom.worms;
/********************************************************************
* @(#)BadDayInhibit.java 1.00 20130417
* Copyright © 2013 by Richard T. Salamone, Jr. All rights reserved.
*
* BadDayInhibit: On every bar, checks the symbol's profit (realized
* plus unrealized) for the current day and if it exceeds a specified
* amount, it closes the position and inhibits further trading.
* Requires the M5 tape.
*
* @author Rick Salamone
* @version 1.00
* 20130501 rts created
*******************************************************/
import com.wormtrader.bars.Bar;
import com.wormtrader.bars.BarSize;
import com.wormtrader.positions.TapeWorm;

public final class BadDayInhibit
	extends TapeWorm
	{
	public static final String CLOSE_TIME = "15:50";

	public String getShortName() { return "BadDayInhibit"; }
	public BarSize getMaxBarSize() { return BarSize.FIVE_MIN; }

	public String getMetrics ( int index, Bar bar )	// called  before automata()
		{
		return bar.hhmm();
		}

	int startReal;
	public void automata( int index, Bar bar )
		{
		String hhmm = bar.hhmm();

		if ( hhmm.equals( "09:30" ))
			{
			inhibit(false);
			startReal = getLeg().getRealized();
			}
		else if ( hhmm.compareTo(CLOSE_TIME) >=0)
			{
			inhibit(false);
			}
		else if (!inhibited()) // if ( hhmm.compareTo( CLOSE_TIME ) < 0)
			{
			int pnlCents = getLeg().getRealized() + getLeg().getUnreal() - startReal;
			if (pnlCents < -20000 ) // $200
				{
				getLeg().fireStop("Bad Day");
				log(bar.yyyymmdd() + " Bad Day: " + (pnlCents/100) + " inhibit @" + hhmm);
				inhibit(true);
				}
			}
		}
	}