package com.wormtrader.custom.scans;

import com.wormtrader.history.Scanner;
import com.wormtrader.bars.Bar;
import com.wormtrader.history.Tape;

public class NewLow extends Scanner
	{
	private static final int period = 20;
	public String toString() { return "New " + period + "-day low of closes"; }

	@Override public boolean isHit(Tape tape)
		{
		int index = tape.size() - 1;
		int todayClose = tape.get(index).getClose();
		for ( int i = 0; i < period; i++ )
			if ( tape.get(--index).getClose() <= todayClose )
				return false;
		return true;
		}
	}