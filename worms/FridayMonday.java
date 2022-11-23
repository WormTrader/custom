package com.wormtrader.custom.worms;
/********************************************************************
* @(#)FridayMonday.java	1.00 20120321
* Copyright 2010 by Richard T. Salamone, Jr. All rights reserved.
*
* FridayMonday: Goes the opposite direction from the signals given
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
import static com.shanebow.util.SBDate.MON;
import static com.shanebow.util.SBDate.FRI;
import com.shanebow.util.SBDate;
import com.wormtrader.bars.Bar;
import com.wormtrader.bars.BarSize;
import com.wormtrader.history.Tape;
import com.wormtrader.positions.TapeWorm;
import com.wormtrader.positions.*;

public final class FridayMonday
	extends TapeWorm
	implements LegListener
	{
	@Override public String getShortName() { return "FridayMonday"; }
//	@Override public BarSize getMaxBarSize() { return BarSize.FIVE_MIN; }

	@Override public String getMetrics ( int index, Bar bar )	// called  before automata()
		{
		return bar.hhmm();
		}

	@Override public void freeResources()
		{
//		fBar0930 = fBarActOn = null;
		getLeg().removeListener(this);
		super.freeResources();
		}

int fFridayOpen;
int fTrend;
	@Override public void automata( int index, Bar bar )
		{
		if ( index > 10)
			{
			int close = bar.getClose();
			fTrend = close - getTape().get(index - 10).getClose();
//			if ( Math.abs(fTrend * 100 / close) < 5 ) // % change
//				fTrend = 0;
			}
		}


	@Override public void initTape(Tape tape)
		{
		getLeg().addListener(this);
		}

	@Override public void legDataChanged ( PositionLeg leg, int field )
		{
		if ( field != LegEvent.PRICE_LAST
//		&&   field != LegEvent.PREOPEN
		&&   field != LegEvent.POSTCLOSE ) return;

		long time = leg.getTrader().getBroker().time();
		String hhmm = SBDate.hhmm(time);
		int qty = leg.getQty();
		int day = SBDate.dayOfWeek(time);
		if ( day == 6 ) // friday
			{
			if ( fTrend == 0 )
				{
	System.out.println(" It's friday, but no trend " + SBDate.DDD(time) + " time: " + hhmm);
				}
//			String hhmm = SBDate.hhmm(time);
			if ( hhmm.equals("09:30")) // friday open
				{
				fFridayOpen = leg.getLast().cents();
				}
			else if ( hhmm.equals("15:59")) // friday AT close
				{
				int close = leg.getLast().cents();
				int fridayChange =  close - fFridayOpen;
	System.out.println(" Fri change: " + fridayChange + ", trend " + fTrend + " " + SBDate.DDD(time) + " time: " + hhmm);
				if ( fridayChange * fTrend <= 0 )
					System.out.println("TREND & CHANGE IN OPPOSITE DIRECTIONS" );
				else if ( fridayChange > 0 ) goLong();
				else if ( fridayChange < 0 ) goShort();
				}
			else if ( hhmm.compareTo("16:00") > 0) // friday AFTER close
				{
	System.out.println(" Fri QTY: " + qty + ", " + SBDate.DDD(time) + " time: " + hhmm);
				}
			return;
			}
		if ( qty != 0 )
			{
	System.out.println(" QTY: " + qty + ", " + SBDate.DDD(time) + " time: " + hhmm);
			goFlat();
			}
//		if ( hhmm.compareTo("09:30") < 0)
//			{
//	System.out.println(" PREOPEN day: " + SBDate.DDD(time) + " time: " + hhmm);
//			}
/*********
if ( day == MON
||   day == FRI )
System.out.println("day: " + SBDate.DDD(time) + " time: " + hhmm);
*********/
		}
	}