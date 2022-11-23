package com.wormtrader.custom.scans;
/********************************************************************
* @(#)EngulfingCandle.java 1.00 20090701
* Copyright © 2009-2012 by Richard T. Salamone, Jr. All rights reserved.
*
* EngulfingCandle:
*
* @author Rick Salamone
* @version 1.00
* 20090701 rts created
*******************************************************/
import com.wormtrader.history.Scanner;
import com.wormtrader.bars.Bar;
import com.wormtrader.history.Tape;

public class EngulfingCandle extends Scanner
	{
	public String toString() { return "Engulfing Candlesticks"; }

	@Override public boolean isHit(Tape tape)
		{
		int nbars = tape.size();
		if ( nbars < 2 ) return false;
		Bar bar = tape.get( --nbars );
		int open = bar.getOpen();
		int close = bar.getClose();
		bar = tape.get( --nbars );
		int yestOpen = bar.getOpen();
		int yestClose = bar.getClose();
		
		return ((yestClose < yestOpen) && (open < yestClose) && (close > yestOpen))
		    || ((yestClose > yestOpen) && (open > yestClose) && (close < yestOpen));
		}
	}