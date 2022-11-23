package com.wormtrader.custom.worms;
/********************************************************************
* @(#)BWFractals.java 1.00 20130421
* Copyright © 2013 by Richard T. Salamone, Jr. All rights reserved.
*
* BWFractals: Implements the Bill Williams "Trading the Fractal" as
* described in Trading Chaos 1st Edition Chapter 8 (pg 142).
*
* @author Rick Salamone
* @version 1.00
* 20130421 rts created
*******************************************************/
import com.wormtrader.bars.Bar;
import com.wormtrader.history.Tape;
import com.wormtrader.history.indicators.Fractals;
import com.wormtrader.positions.TapeWorm;
import com.wormtrader.positions.*;
import com.shanebow.util.SBFormat;
import com.wormtrader.positions.signals.TradeSignal;

public final class BWFractals
	extends TapeWorm
	{
	/** Reason strings for orders */
	private static final String BEAR_REV = "\u21E3RB";
	private static final String BULL_REV = "\u21E1RB";
	private static final String UP_FRACTAL_BUY = "\u2227BUY";
	private static final String DN_FRACTAL_SELL = "\u2228SELL";

	private Fractals fFractals;

	public BWFractals()
		{
		super();
		setWarmUpPeriods( 8 );
		}

	public void initTape(Tape tape)
		{
		fFractals = (Fractals)tape.addStudy(Fractals.STUDY_NAME, "");
		}

	public synchronized void historyDone() { backfilled(getTape()); }

	protected final void backfilled(Tape aTape)
		{
System.out.println(toString() + " backfill");
		removeSignals();
		int lastIndex = aTape.size() - 1;
		Bar bar = bar(lastIndex);
		int currLo = bar.getLow();
		int currHi = bar.getHigh();
		int lo = aTape.getHighestHi();
		int hi = aTape.getLowestLo();
		for (int x = lastIndex; x > 5; x--)
			{
			bar = bar(x);
			lo = Math.min(lo, bar.getLow());
			hi = Math.max(hi, bar.getHigh());
			int sigIndex = x -2;
			byte fract = fFractals.getValue(sigIndex);
			if (fract == 0) continue;
			Bar signalBar = bar(sigIndex);
			if ((fract & Fractals.IS_UP) != 0)
				{
				if (currHi > signalBar.getHigh()) // already triggered
					continue;
				int startIndex = fFractals.priorDown(sigIndex);
				if (startIndex >= 0)
					{
					int cancel = bar(startIndex).getLow();
					if (cancel < lo) // not canceled
						signal(new UpFractalSignal(this, signalBar, cancel), false);
					}
				}
			if ((fract & Fractals.IS_DOWN) != 0)
				{
				if (currLo < signalBar.getLow()) // already triggered
					continue;
				int startIndex = fFractals.priorUp(sigIndex);
				if (startIndex >= 0)
					{
					int cancel = bar(startIndex).getHigh();
					if (cancel > hi) // not canceled
						signal(new DownFractalSignal(this, signalBar, cancel), false);
					}
				}
			}
		}

	public String getShortName() { return "Fractals"; }

	public void automata( int x, Bar bar )
		{
		if (x < 8) return;

		int signalIndex = x - 2;
		byte fract = fFractals.getValue(signalIndex);
		if (fract == 0)
			return;
		Bar signalBar = bar(signalIndex);
		if ((fract & Fractals.IS_UP) != 0)
			{
			int startIndex = fFractals.priorDown(signalIndex);
			if (startIndex >= 0)
				{
				int cancel = bar(startIndex).getLow();
				signal( new UpFractalSignal(this, signalBar, cancel), false);
				}
			}
		if ((fract & Fractals.IS_DOWN) != 0)
			{
			int startIndex = fFractals.priorUp(signalIndex);
			if (startIndex >= 0)
				{
				int cancel = bar(startIndex).getHigh();
				signal( new DownFractalSignal(this, signalBar, cancel), false);
				}
			}
		}

	public String getMetrics ( int index, Bar bar ) { return ""; }

	private final class UpFractalSignal
		extends TradeSignal
		{
		private String fHHMM;

		UpFractalSignal(TapeWorm aWorm, Bar aSignalBar, int aCancel)
			{
			super(aWorm, ACT_LONG, aSignalBar.getHigh()+1, aCancel, UP_FRACTAL_BUY);
			fHHMM = aSignalBar.hhmm();
			}

		public String toString() { return fHHMM + " U"; }
		}

	private class DownFractalSignal
		extends TradeSignal
		{
		private String fHHMM;

		DownFractalSignal(TapeWorm aWorm, Bar aSignalBar, int aCancel)
			{
			super(aWorm, ACT_SHORT, aSignalBar.getLow() - 1, aCancel, DN_FRACTAL_SELL);
			fHHMM = aSignalBar.hhmm();
			}

		public String toString() { return fHHMM + " D"; }
		}
	}