package com.wormtrader.custom.stops;
/********************************************************************
* @(#)TrailFractals.java 1.00 20130416
* Copyright © 2013 by Richard T. Salamone, Jr. All rights reserved.
*
* TrailFractals: For long entry trails the stop to the lowest low
* of the past N fractals, where N is specified as a parameter and
* defaults to 2.
*
* @author Rick Salamone
* @version 1.00
* 20130416 rts created
*******************************************************/
import com.wormtrader.bars.*;
import com.wormtrader.positions.*;
import com.wormtrader.history.Tape;
import com.wormtrader.history.event.*;
import com.wormtrader.history.indicators.Fractals;

public class TrailFractals
	extends ExitMethod
	implements TapeListener
	{
	protected Fractals fFractals;
	protected BarSizeCount fGoBack;
	protected String  WHY = "TF";
	protected Bar fSignalBar;

	protected void setDefaults()
		{
		fGoBack = BarSizeCount.parse("2 M5");
		}

	protected boolean parseParam(String aParam)
		{
		BarSizeCount goBack = BarSizeCount.parse(aParam);
		if (goBack == null) // parse error
			return super.parseParam(aParam);
		fGoBack = goBack;
		return true;
		}

	private void addTapeListener()
		{
		if (m_leg != null)
			{
			Tape tape = m_leg.getTape(fGoBack.barSize());
			tape.addPriorityTapeListener(this);
			fFractals = (Fractals)tape.addStudy(Fractals.STUDY_NAME, "");
			}
		}

	private void removeTapeListener()
		{
		if (m_leg != null)
			{
			Tape tape = m_leg.getTape(fGoBack.barSize());
			tape.removeTapeListener(this);
			}
		fFractals = null;
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

	// implement TapeListener
	@Override public void tapeChanged(TapeEvent e)
		{
		int qty = getQty();
		if ( qty == 0 || e.getActionID() != TapeEvent.REALTIME_BAR )
			return;

		int index = e.getIndex();
		if (index < 3) return;

		byte fract = fFractals.getValue(index - 2);
		if (fract == 0) return; // no new fractal formed

		if (qty < 0 && (fract & Fractals.IS_UP) != 0)
			adjustTrigger(maxUpFractal(index-1, fGoBack.count()), WHY);

		else if (qty > 0 && (fract & Fractals.IS_DOWN) != 0)
			adjustTrigger(minDnFractal(index-1, fGoBack.count()), WHY);

trace(e.getBar().hhmm() + " trigger: " + getTrigger());
		}

	private int minDnFractal(int fromIndex, int goBack)
		{
		BarList bars = fFractals.bars();
		int min = -1;
		int index = fromIndex;
		for (int i = 0; i < goBack; i++)
			{
			index = fFractals.priorDown(index);
trace("maxDn pass " + i + " index: " + index);
			if (index < 0) return -1;
			int low = bars.get(index).getLow();
			if (i == 0 || low < min) min = low;
			}
		return min;
		}

	private int maxUpFractal(int fromIndex, int goBack)
		{
		BarList bars = fFractals.bars();
		int max = -1;
		int index = fromIndex;
		for (int i = 0; i < goBack; i++)
			{
			index = fFractals.priorUp(index);
trace("maxUp pass " + i + " index: " + index);
			if (index < 0) return -1;
			int high = bars.get(index).getHigh();
			if (high > max) max = high;
			}
		return max;
		}

	@Override public void positionOpened( int qty, int basis )
		{
		super.positionOpened( qty, basis );
		BarList bars = fFractals.bars();
		int index = bars.size() - 2;
		if (index < 0) index = 0;
		int uf = -1;
		if (qty < 0)
			{
			uf = maxUpFractal(index, fGoBack.count());
			if (uf < basis)
{
				uf = maxUpFractal(bars.size(), 1);
trace("special case trigger: " + uf);
}
			}
		else
			{
			uf = minDnFractal(index, fGoBack.count());
			if (uf > basis)
{
				uf = minDnFractal(bars.size(), 1);
trace("special case trigger: " + uf);
}
			}
		if (uf == -1) uf = bars.get(index).midpoint();
		adjustTrigger(uf, WHY);
	trace("qty: " + qty + " trigger: " + getTrigger());
		}

	@Override protected void triggered(int qty, int price, String aWhy)
		{
		if (fSignalBar != m_leg.getTape(fGoBack.barSize()).lastBar())
			super.triggered(qty, price, aWhy);
		}
	}
