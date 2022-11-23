package com.wormtrader.custom.scans;
/********************************************************************
* @(#)UnfilledGap.java 1.00 20121120
* Copyright © 2009-2012 by Richard T. Salamone, Jr. All rights reserved.
*
* UnfilledGap:
*
* @author Rick Salamone
* @version 1.00
* 20121120 rts created
*******************************************************/
import com.wormtrader.history.Scanner;
import com.wormtrader.bars.Bar;
import com.wormtrader.history.Tape;

public class UnfilledGap extends Scanner
	{
	public String toString() { return "LBR Unfilled Gap"; }

	@Override public boolean isHit(Tape tape)
		{
		int nbars = tape.size();
		if ( nbars < 2 ) return false;
		int i = nbars - 1;

		Bar today = tape.get(i);
		Bar yest = tape.get(i-1);

		int todayOpen = today.getOpen();
		int yestClose = yest.getClose();
		if (todayOpen > yestClose // gapped up
		&& today.getLow() > yestClose ) // unfilled
			return true;
		if (todayOpen < yestClose // gapped down
		&& today.getHigh() < yestClose ) // unfilled
			return true;
		return false;
		}
	}