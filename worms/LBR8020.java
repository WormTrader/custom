package com.wormtrader.custom.worms;
/********************************************************************
* @(#)LBR8020.java 1.00 20130511
* Copyright © 2013 by Richard T. Salamone, Jr. All rights reserved.
*
* LBR8020: implements the 80-20's strategy described in Chapter 6 of
* Street Smarts (p45ff).  Here are the rules:
*
* FOR BUYS (SELLS ARE REVERSED)
* 1. Yesterday the market opened in the top 20 percent of its daily
*    range and closed in the lower 20 percent of its daily range.
* 2. Today the market must trade at least 5-15 ticks below yesterday's
*    low This is a guideline. The exact amount is left to your discretion.
* 3. An entry buy stop is then placed at yesterday's low. Upon being filled,
*    place an initial protective stop near the low extreme of today.
*
* @author Rick Salamone
* @version 1.00
* 20130511 rts created
*******************************************************/
import static com.wormtrader.broker.OrderTracker.ACT_LONG;
import static com.wormtrader.broker.OrderTracker.ACT_SHORT;
import static com.wormtrader.broker.OrderTracker.ACT_STC;
import com.wormtrader.bars.Bar;
import com.wormtrader.bars.BarSize;
import com.wormtrader.history.Tape;
import com.wormtrader.positions.TapeWorm;
import com.wormtrader.positions.signals.TradeSignal;
import com.shanebow.util.SBFormat;

public final class LBR8020
	extends TapeWorm
	{
	public static final String OPEN_TIME = "09:30";

	private String fMetrics; // debug calculations
	private int fAct;
	private int fPercent = 20;
	private int fSetupPrice;
	private int fEntryPrice;
	private int fPenetration = 10;

	public String name() { return "" + (100-fPercent) + "-" + fPercent; }

	public BarSize getMaxBarSize() { return BarSize.THIRTY_MIN; }

	public void automata(int index, Bar bar)
		{
		if ( bar.hhmm().equals(OPEN_TIME))
			{
			removeSignals();
			fAct = 0;
			try
				{
				String priorDate = bar(index-1).yyyymmdd();
				Bar priorDay = getLeg().getTape(BarSize.ONE_DAY)
				                           .getBars().find(priorDate);
				initDay(priorDay);
				fMetrics = bar.yyyymmdd() + " is a " + TradeSignal.ACT_DESC[fAct] + " day."
				         + fMetrics;
				if (fAct != 0)
					fMetrics += "\n   Setup: " + SBFormat.toDollarString(fSetupPrice)
					         + "   Entry: " + SBFormat.toDollarString(fEntryPrice);
				log(fMetrics);
				}
			catch(Exception e) 	{ log(bar.yyyymmdd() + ": Error setting signal"); }
			}
		else switch (fAct)
			{
			default:
				return;

			case ACT_LONG:
				if (bar.getLow() > fSetupPrice) return;
				fAct = 0;
				signal(new TradeSignal(this, ACT_LONG, fEntryPrice, 0, "B" + name()));
/********
				signal(new TradeSignal(this, ACT_LONG, fEntryPrice, 0, "B" + name())
					{
					@Override public void setState(byte aState)
						{
						if (aState == FILLED)
							{
							int loToday = getLeg().getTape(BarSize.ONE_DAY).lastBar().getLow();
							signal(new TradeSignal(worm(), ACT_STC, loToday-1, 0, "Fail "+name()));
							}
						}
					});
********/
				return;

			case ACT_SHORT:
				if (bar.getHigh() < fSetupPrice) return;
				fAct = 0;
				signal(new TradeSignal(this, ACT_SHORT, fEntryPrice, 0, "S" + name()));
/********
				signal(new TradeSignal(this, ACT_SHORT, fEntryPrice, 0, "S" + name())
					{
					@Override public void setState(byte aState)
						{
						if (aState == FILLED)
							{
							int hiToday = getLeg().getTape(BarSize.ONE_DAY).lastBar().getHigh();
							signal(new TradeSignal(worm(), ACT_BTC, hiToday+1, 0, "Fail "+name()));
							}
						}
					});
********/
				return;
			}
		}

	private void initDay(Bar priorDay)
		{
		fMetrics = "" + priorDay.yyyymmdd();
		int range = priorDay.range();
		int twentyPercent = fPercent * range / 100;
		if (twentyPercent <= 2)
			{
			fMetrics += " is too narrow";
			return;
			}
		int priorDayO = priorDay.getOpen();
		int priorDayH = priorDay.getHigh();
		int priorDayL = priorDay.getLow();
		int priorDayC = priorDay.getClose();

		fMetrics = "\n  " + fPercent + "%% range = "
		 + SBFormat.toDollarString(priorDayL+twentyPercent)
		 + " - " + SBFormat.toDollarString(priorDayH-twentyPercent)
		 + "\n   O:" + SBFormat.toDollarString(priorDayO)
		 + "   C:" + SBFormat.toDollarString(priorDayC);

		if ((priorDayO > priorDayH-twentyPercent)
		&&  (priorDayC < priorDayL+twentyPercent))
			{
			fAct = ACT_LONG;
			fSetupPrice = priorDayL - fPenetration;
			fEntryPrice = priorDayL;
			}
		else if ((priorDayO < priorDayL+twentyPercent)
		&&  (priorDayC > priorDayH-twentyPercent))
			{
			fAct = ACT_SHORT;
			fSetupPrice = priorDayH + fPenetration;
			fEntryPrice = priorDayH;
			}
		}
	}
