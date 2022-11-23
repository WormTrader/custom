package com.wormtrader.custom.scans;
/********************************************************************
* @(#)Squat_NR4.java 1.00 20121115
* Copyright © 2012 by Richard T. Salamone, Jr. All rights reserved.
*
* Squat_NR4: Scans for stocks that are squats and also have
* the narrowest trading range for the last four days.
*
* @author Rick Salamone
* @version 1.00
* 20130326 rts created
*******************************************************/
import static com.wormtrader.bars.Bar.SQUAT;
import com.wormtrader.bars.Bar;
import com.wormtrader.bars.BarList;
import com.wormtrader.history.Scanner;
import com.wormtrader.history.Tape;

public final class Squat_NR4
	extends NarrowRange
	{
	public Squat_NR4()
		{
		super();
		period = 4;
		}

	public String toString() { return "Squat/NR4"; }

	public void setParams(String params) {}

	@Override public boolean isHit(Tape tape)
		{
		BarList bars = tape.getBars();
		int index = bars.size() - 1;
		if (index < period-1) return false;
		Bar bar = bars.get(index);
		Bar prevBar = bars.get(index-1);
		return (bar.mfiType(prevBar) == SQUAT)
//		    && bar.isInside(prevBar)
		    && super.isHit(tape);
		}
	}