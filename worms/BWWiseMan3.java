package com.wormtrader.custom.worms;
/********************************************************************
* @(#)BWWiseMan3.java 1.00 20130507
* Copyright © 2013 by Richard T. Salamone, Jr. All rights reserved.
*
* BWWiseMan3: Implements the Bill Williams "Third Wise Man" as
* described in Trading Chaos 2nd Edition Chapter 11 (pg 135).
*
* @author Rick Salamone
* @version 1.00
* 20130507 rts created by modifying BWFractal
*******************************************************/
import com.wormtrader.bars.Bar;
import com.wormtrader.history.Tape;
import com.wormtrader.history.indicators.Alligator;
import com.wormtrader.history.indicators.Fractals;
import com.wormtrader.positions.TapeWorm;
import com.wormtrader.positions.*;
import com.shanebow.util.SBFormat;
import com.wormtrader.positions.signals.TradeSignal;

public final class BWWiseMan3
	extends TapeWorm
	{
	/** Reason strings for orders */
	private static final String BEAR_REV = "\u21E3RB";
	private static final String BULL_REV = "\u21E1RB";
	private static final String UP_FRACTAL_BUY = "\u2227BUY";
	private static final String DN_FRACTAL_SELL = "\u2228SELL";

	private Alligator fGator;
	private Fractals fFractals;
	private TradeSignal fUSignal;
	private TradeSignal fDSignal;

	public String getShortName() { return "Wise3"; }

	public void initTape(Tape tape)
		{
		fGator = new Alligator(tape);
		fFractals = (Fractals)tape.addStudy(Fractals.STUDY_NAME, "");
		}

	protected final void backfilled(Tape aTape)
		{
		removeSignals();
		fUSignal = fDSignal = null;
		historyDone();
		}

	private boolean isWellFormedU(int x)
		{
		return fGator.range(x) > 10 &&
		       (bar(x).getHigh() > fGator.red.getValue(x));
		}

	private boolean isWellFormedD(int x)
		{
		return fGator.range(x) > 10 &&
		       (bar(x).getLow() < fGator.red.getValue(x));
		}

	public void automata( int x, Bar bar )
		{
		if (x < 8) return;

		int signalIndex = x - 2;
		byte fract = fFractals.getValue(signalIndex);
		if (fract == 0)
			return;
		Bar signalBar = bar(signalIndex);

		if ((fract & Fractals.IS_UP) != 0
		&& isWellFormedU(signalIndex))
			createSignalU(signalIndex);

		if ((fract & Fractals.IS_DOWN) != 0
		&& isWellFormedD(signalIndex))
			createSignalD(signalIndex);
		}

	private void createSignalU(int x)
		{
		if (fUSignal != null)
			remove(fUSignal);
		fUSignal = new UpFractalSignal(this, bar(x));
		signal(fUSignal);
		}

	private void createSignalD(int x)
		{
		if (fDSignal != null)
			remove(fDSignal);
		fDSignal = new DownFractalSignal(this, bar(x));
		signal(fDSignal);
		}

	private final class UpFractalSignal
		extends TradeSignal
		{
		private String fHHMM;

		UpFractalSignal(TapeWorm aWorm, Bar aSignalBar)
			{
			super(aWorm, ACT_LONG, aSignalBar.getHigh()+1, 0, UP_FRACTAL_BUY);
			fHHMM = aSignalBar.hhmm();
			}

		@Override public boolean triggered(int price)
			{
			boolean triggered = super.triggered(price)
			                 && (price > fGator.redAtLastBar()); // up bar
			if (triggered && (this == fUSignal))
				fUSignal = null;
			return triggered;
			}

		public String toString() { return fHHMM + " U"; }
		}

	private class DownFractalSignal
		extends TradeSignal
		{
		private String fHHMM;

		DownFractalSignal(TapeWorm aWorm, Bar aSignalBar)
			{
			super(aWorm, ACT_SHORT, aSignalBar.getLow() - 1, 0, DN_FRACTAL_SELL);
			fHHMM = aSignalBar.hhmm();
			}

		@Override public boolean triggered(int price)
			{
			boolean triggered = super.triggered(price)
			                 && (price < fGator.redAtLastBar()); // up bar
			if (triggered && (this == fDSignal))
				fDSignal = null;
			return triggered;
			}

		public String toString() { return fHHMM + " D"; }
		}
	}