package com.wormtrader.custom.worms;
/********************************************************************
* @(#)Oops.java 1.00 20130505
* Copyright © 2013 by Richard T. Salamone, Jr. All rights reserved.
*
* Oops: Based on the Larry Williams Oops Pattern:
*  1) Futures open above previous day's high, then trade below that high: Go short.
*  2) Futures open below previous day's low, then trade above that low: Go long.
*
* @author Rick Salamone
* @version 1.00
* 20130505 rts created
*******************************************************/
import static com.wormtrader.positions.signals.TradeSignal.ACT_LONG;
import static com.wormtrader.positions.signals.TradeSignal.ACT_SHORT;
import com.wormtrader.bars.Bar;
import com.wormtrader.bars.BarSize;
import com.wormtrader.positions.TapeWorm;
import com.wormtrader.positions.signals.TradeSignal;

public final class Oops
	extends TapeWorm
	{
	public static final String OPEN_TIME = "09:30";

	public String getShortName() { return "Oops"; }
	public BarSize getMaxBarSize() { return BarSize.THIRTY_MIN; }

	public void automata( int index, Bar bar )
		{
		if ( bar.hhmm().equals(OPEN_TIME))
			{
			removeSignals();
			try
				{
				String priorDate = bar(index-1).yyyymmdd();
				Bar prior = getLeg().getTape(BarSize.ONE_DAY)
				                           .getBars().find(priorDate);
				int open = bar.getOpen();
				if (open > prior.getHigh())
					signal(new TradeSignal(this, ACT_SHORT, prior.getHigh()-1, 0, "SOops"));
				else if (open < prior.getLow())
					signal(new TradeSignal(this, ACT_LONG, prior.getLow()+1, 0, "LOops"));
				}
			catch(Exception e) 	{ log(bar.yyyymmdd() + ": Error setting signal"); }
			}
		}
	}