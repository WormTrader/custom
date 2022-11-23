package com.wormtrader.custom.worms;
/********************************************************************
* @(#)RossOpposite.java	1.00 20120321
* Copyright 2010 by Richard T. Salamone, Jr. All rights reserved.
*
* RossOpposite: Goes the opposite direction from the signals given
* by Joe Ross TNT 1 page 106:
* 1) If the 2nd 5 minute bar is not an inside bar, FADE a
*    breakout of the extreme (high or low) of the 2nd 5 min bar.
* 2) If the 2nd 5 minute bar is an inside bar, FADE a
*    breakout of the extreme (high or low) of the 1st 5 min bar.
*
* @version 1.00 11/20/09
* @author Rick Salamone
* 20091120 RTS 0.00 demo for Paul
* 20120321 rts created for real
*******************************************************/
import com.wormtrader.bars.Bar;
import com.wormtrader.bars.BarSize;
import com.wormtrader.positions.TapeWorm;
import com.wormtrader.positions.*;

public final class RossOpposite
	extends TapeWorm
	implements LegListener
	{
	private Bar fBar0930;
	private Bar fBarActOn;
	private boolean fAlreadyIn;

	@Override public String getShortName() { return "RossOpposite"; }
	@Override public BarSize getMaxBarSize() { return BarSize.FIVE_MIN; }

	@Override public String getMetrics ( int index, Bar bar )	// called  before automata()
		{
		return bar.hhmm();
		}

	@Override public void freeResources()
		{
		fBar0930 = fBarActOn = null;
		getLeg().removeListener(this);
		super.freeResources();
		}

	@Override public void automata( int index, Bar bar )
		{
		String hhmm = bar.hhmm();

		if ( hhmm.equals( "09:30" ))
			{
			fAlreadyIn = false;
			fBarActOn = bar;
			fBar0930 = bar;
			getLeg().addListener(this);
			}
		else if ( hhmm.equals( "09:35" ))
			{
			if (!fAlreadyIn) fBarActOn = bar;
			}
		else if ( hhmm.equals( "10:00" ))
			getLeg().removeListener(this);
		}

	@Override public void legDataChanged ( PositionLeg leg, int field )
		{
		if ( field == LegEvent.QUANTITY )
			{
			fAlreadyIn = true;
			leg.removeListener(this);
			}
		else if ( field == LegEvent.PRICE_LAST )
			{
			int last = leg.getLast().cents();
			if ( last > fBarActOn.getHigh())
				{
				leg.removeListener(this);
goShort(); // Ross says goLong();
				fAlreadyIn = true;
				}
			else if ( last < fBarActOn.getLow())
				{
				leg.removeListener(this);
goLong(); // Ross says goShort();
				fAlreadyIn = true;
				}
			}
		}
	}