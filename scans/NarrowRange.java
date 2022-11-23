package com.wormtrader.custom.scans;
/********************************************************************
* @(#)NarrowRange.java 1.00 20121115
* Copyright © 2012 by Richard T. Salamone, Jr. All rights reserved.
*
* NarrowRange: Scans the current universe for stocks where the range of
* the most recent day is less than the range of the prior N-1 days where N
* is the specified period.
*
* @author Rick Salamone
* @version 1.00
* 20121115 rts created
*******************************************************/
import com.wormtrader.history.Scanner;
import com.wormtrader.bars.BarList;
import com.wormtrader.history.Tape;

public class NarrowRange
	extends Scanner
	{
	protected int period = 7;
	public String toString() { return "LBR NR" + period; }

	public void setParams(String params)
		{
		if (params == null || params.isEmpty())
			return;
		try { period = Integer.parseInt(params); }
		catch (Exception e) { System.err.println("NarrowRange bad params: " + params); }
		}

	@Override public boolean isHit(Tape tape)
		{
		BarList bars = tape.getBars();
		int index = bars.size() - 1;
		if (index < period-1) return false;
		int mrRange = bars.get(index).range(); // most recent range
		for ( int i = 0; i < period-1; i++ )
			if( bars.get(--index).range() < mrRange)
				return false;
		return true;
		}
	}