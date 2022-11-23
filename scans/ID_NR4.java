package com.wormtrader.custom.scans;
/********************************************************************
* @(#)ID_NR4.java 1.00 20121115
* Copyright © 2012 by Richard T. Salamone, Jr. All rights reserved.
*
* ID_NR4: Scans for stocks that meet Linda Raschke's ID/NR4 criterion
* for a breakout day as described in the book StreetSmarts.
*
* @author Rick Salamone
* @version 1.00
* 20121115 rts created
*******************************************************/
import com.wormtrader.history.Scanner;
import com.wormtrader.bars.BarList;
import com.wormtrader.history.Tape;

public final class ID_NR4
	extends NarrowRange
	{
	public ID_NR4()
		{
		super();
		period = 4;
		}

	public String toString() { return "LBR ID/NR4"; }

	public void setParams(String params) {}

	@Override public boolean isHit(Tape tape)
		{
		BarList bars = tape.getBars();
		int index = bars.size() - 1;
		if (index < period-1) return false;
		return bars.get(index).isInside(bars.get(index-1))
		    && super.isHit(tape);
		}
	}