package com.wormtrader.custom.stops;
/********************************************************************
* @(#)TrailBars.java 1.00 20130416
* Copyright © 2013 by Richard T. Salamone, Jr. All rights reserved.
*
* TrailBars: For long entry trails the stop to the lowest low of
* the past N bars, where N is specified as a parameter and defaults
* to 3.
*
* @author Rick Salamone
* @version 1.00
* 20130416 rts created
* 20130505 rts added bar size parameter
*******************************************************/
import com.wormtrader.bars.*;
import com.wormtrader.positions.*;
import com.wormtrader.history.Tape;
import com.wormtrader.history.event.*;

public class TrailBars
	extends ExitMethod
	implements TapeListener
	{
	protected BarSizeCount fBarsBack;
	private static final String WHY = "TB";
	protected Bar fSignalBar;

	protected void setDefaults()
		{
		fBarsBack = BarSizeCount.parse("3 M5");
		}

	protected boolean parseParam(String aParam)
		{
System.out.println("TrailBars.parseParam(" + aParam + ")");
		BarSizeCount barsBack = BarSizeCount.parse(aParam);
		if (barsBack == null) // parse error
			return super.parseParam(aParam);
		fBarsBack = barsBack;
		return true;
		}

	private void addTapeListener()
		{
		if (m_leg != null)
			{
			Tape tape = m_leg.getTape(fBarsBack.barSize());
			tape.addPriorityTapeListener(this);
			}
		}

	private void removeTapeListener()
		{
		if (m_leg != null)
			{
			Tape tape = m_leg.getTape(fBarsBack.barSize());
			tape.removeTapeListener(this);
			}
		fSignalBar = null;
		}

	@Override public void enable()
		{
		addTapeListener();
		super.enable();
		}

	public void disable()
		{
		removeTapeListener();
		super.disable();
		}

	protected int lowestLo(BarList bars, int goBack)
		{
		int index = bars.size();
		int min = bars.get(--index).getLow();
		for (int i = 1; i < goBack; i++)
			{
			if (--index < 0) return min;
			int low = bars.get(index).getLow();
			if (low < min) min = low;
			}
		return min;
		}

	protected int highestHi(BarList bars, int goBack)
		{
		int index = bars.size();
		int max = bars.get(--index).getHigh();
		for (int i = 1; i < goBack; i++)
			{
			if (--index < 0) return max;
			int high = bars.get(index).getHigh();
			if (high > max) max = high;
			}
		return max;
		}

	// implement TapeListener
	@Override public void tapeChanged(TapeEvent e)
		{
		int qty = getQty();
		if ( qty == 0 || e.getActionID() != TapeEvent.REALTIME_BAR )
			return;

		computeTrigger(qty, e.getTape().getBars());
		}

	private void computeTrigger(int qty, BarList bars)
		{
		if (qty < 0)
			adjustTrigger(highestHi(bars, fBarsBack.count()), WHY);

		else // qty > 0
			adjustTrigger(lowestLo(bars, fBarsBack.count()), WHY);
		}

	@Override public void positionOpened( int qty, int basis )
		{
		super.positionOpened( qty, basis );
		BarList bars = m_leg.getTape(fBarsBack.barSize()).getBars();
		fSignalBar = bars.lastBar();
		}

	@Override protected void triggered(int qty, int price, String aWhy)
		{
		if (fSignalBar != m_leg.getTape(fBarsBack.barSize()).lastBar())
			super.triggered(qty, price, aWhy);
		}
	}
