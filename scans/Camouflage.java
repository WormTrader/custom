package com.wormtrader.custom.scans;

import com.wormtrader.history.Scanner;
import com.wormtrader.bars.Bar;
import com.wormtrader.history.Tape;

public class Camouflage extends Scanner
	{
	public String toString() { return "TDCamouflage"; }

	public void initialize( Tape tape )
		{
		super.initialize( tape );
		}

	@Override public boolean isHit(Tape tape)
		{
		int nbars = tape.size();
		if ( nbars < 2 ) return false;
		Bar bar = tape.get( --nbars );
		int open = bar.getOpen();
		int close = bar.getClose();
		Bar prev = tape.get( --nbars );
		int prevClose = prev.getClose();
		
		return (((close < prevClose) && (bar.getLow() < prev.getLow()) && (close > open))
					// bullish at a market low
		    ||  ((close > prevClose) && (bar.getHigh() > prev.getHigh()) && (close < open)));
		}
	}