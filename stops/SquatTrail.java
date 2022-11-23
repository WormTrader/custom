package com.wormtrader.custom.stops;
/********************************************************************
* @(#)SquatTrail.java 1.00 20130416
* Copyright © 2013 by Richard T. Salamone, Jr. All rights reserved.
*
* SquatTrail: Extends TrailBars to tighten the stop when a squat bar
* occurs. The stop is placed one tick above/below the high/low of the
* the squat bar.
*
* @author Rick Salamone
* @version 1.00
* 20130416 rts created
*******************************************************/
import com.wormtrader.bars.*;
import com.wormtrader.positions.*;
import com.wormtrader.history.Tape;
import com.wormtrader.history.event.*;

public final class SquatTrail
	extends TrailBars
	implements TapeListener
	{
	// implement TapeListener
	@Override public void tapeChanged(TapeEvent e)
		{
		int qty = getQty();
		if ( qty == 0 || e.getActionID() != TapeEvent.REALTIME_BAR )
			return;

		BarList bars = e.getTape().getBars();
		Bar bar = e.getBar();
		boolean isSquat;
		try { isSquat = bar.mfiType(bars.get(e.getIndex()-1)) == Bar.SQUAT; }
		catch (Exception ex) { isSquat = false; }
		if (qty < 0)
			{
			int hh = isSquat? bar.getHigh()+1 : highestHi(bars, fBarsBack.count());
			adjustTrigger(hh, isSquat? "STH" : "TH");
			}
		else // qty > 0
			{
			int ll = isSquat? bar.getLow()-1 : lowestLo(bars, fBarsBack.count());
			adjustTrigger(ll, isSquat? "STL" : "TL");
			}
		}
	}
