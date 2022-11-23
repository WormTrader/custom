package com.wormtrader.custom.stops;
/********************************************************************
* @(#)RossExit.java 1.00 20120316
* Copyright © 2012 by Richard T. Salamone, Jr. All rights reserved.
*
* RossExit: Implements the Joe Ross Violation method of exiting
* a position as set forth in TNT I pgs 105ff.
* Params: R 3 M5,V 200
*  R - enables checking for reversal bars
*  3 - max # reversals, 0 disables Reversal checking - default is 2
*  V - enables checking for violation
*  200 - amount of room (in cents) beyond previous high/low - default is 0
*
* @author Rick Salamone
* @version 1.00
* 20120316 rts created as RossReversal
* 20120321 rts renamed, added the Violation code, and implemented params
* 20120327 rts added optional breathing room params
*******************************************************/
import com.wormtrader.bars.*;
import com.wormtrader.positions.*;
import com.wormtrader.history.event.*;

public final class RossExit
	extends ExitMethod
	implements TapeListener
	{
	private int fReversals; // counts bars that trend opposite to the position
	private BarSizeCount fMaxReversals; // # of reversals which fire the stop
	private int fMinChange = 2; // min change beween open/close to count as reversal
	int fViolationRoom = 0; // extra room for violations
	private Bar fEntryBar;
	private boolean fDoViolation = true;
	private String  fWhy; // reversal or violation

	protected void setDefaults()
		{
		fMaxReversals = BarSizeCount.parse("2 M5");
		}

	protected boolean parseParam(String aParam)
		{
		char first = aParam.toUpperCase().charAt(0);
		if (first == 'R')
			{
			BarSizeCount maxReversals = BarSizeCount.parse(aParam.substring(1).trim());
			if (maxReversals != null)
				{
				fMaxReversals = maxReversals;
				return true;
				}
			}
		else if (first == 'V')
			{
			try { fViolationRoom = Integer.parseInt(aParam.substring(1).trim()); }
			catch (Exception e) {}
			fDoViolation = true;
			return true;
			}
		return super.parseParam(aParam);
		}

	private void addTapeListener()
		{
		if (m_leg != null && fMaxReversals != null)
			{
			m_leg.getTape(fMaxReversals.barSize())
			     .addPriorityTapeListener(this);
			}
		}

	private void removeTapeListener()
		{
		if (m_leg != null && fMaxReversals != null)
			{
			m_leg.getTape(fMaxReversals.barSize())
			     .removeTapeListener(this);
			}
		fEntryBar = null;
		}

	@Override public void enable()
		{
		addTapeListener();
		super.enable();
		}

	@Override public void disable()
		{
		removeTapeListener();
		super.disable();
		}

	// implement TapeListener
	@Override public void tapeChanged(TapeEvent e)
		{
		int qty = getQty();
		if ( qty == 0
		|| e.getActionID() != TapeEvent.REALTIME_BAR )
			return;

		Bar bar = e.getBar();
		int change = bar.getClose() - bar.getOpen();
		if (Math.abs(change) > fMinChange // ignore "relatively unchanged bars"
		&& (change * qty) < 0)
			{
			++fReversals;
	trace("" + fReversals + " reversals, " + bar);
			if ( fReversals >= fMaxReversals.count() )
				fireStop(bar.getClose(), fWhy = "R" + fReversals);
			}
		}

	@Override protected void positionClosed()
		{
		fReversals = 0;
		fEntryBar = null;
		}

	@Override public void positionOpened( int qty, int basis )
		{
		super.positionOpened( qty, basis );
		fReversals = 0;
		System.out.println("RV RESET qty: " + qty + ", reversals: " + fReversals);
		fEntryBar = m_leg.getTape(fMaxReversals.barSize()).lastBar();
		trace("entry bar: " + fEntryBar
		    + "\n max reversals: " + fMaxReversals + " violation: " + fDoViolation);
		}

	@Override protected void lastPrice( int qty, int price )
		{
		if ( !fDoViolation)
			return;
		Bar bar = m_leg.getTape(fMaxReversals.barSize()).lastBar();
		if ( bar == fEntryBar )
			return;
		if ((( qty > 0 ) && price < (bar.getLow() - fViolationRoom))
		||  (( qty < 0 ) && price > (bar.getHigh() + fViolationRoom)))
			fireStop(bar.getClose(), fWhy = "V");
		}
	}
