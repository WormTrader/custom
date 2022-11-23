package com.wormtrader.custom.worms;
/********************************************************************
* @(#)BWChaos2.java 1.00 20120911
* Copyright © 2012 by Richard T. Salamone, Jr. All rights reserved.
*
* BWChaos2:
*
* @author Rick Salamone
* @version 1.00
* 20120911 rts created
*******************************************************/
import com.wormtrader.bars.Bar;
import com.wormtrader.history.Tape;
import com.wormtrader.history.indicators.BWChaos2nd;
import com.wormtrader.positions.TapeWorm;
import com.wormtrader.positions.*;
import com.shanebow.util.SBFormat;
import com.wormtrader.broker.OrderTracker;
import static com.wormtrader.broker.Broker.STP_ORDER;

public final class BWChaos2
	extends TapeWorm
	{
	private BWChaos2nd chaos2;
	private PositionLeg fLeg;

	public BWChaos2()
		{
		super();
		setWarmUpPeriods( 8 );
		}

	public void initTape(Tape tape)
		{
		chaos2 = new BWChaos2nd(tape);
		fLeg = getLeg();
		fLeg.addListener(new LegListener()
			{
			public void legDataChanged ( PositionLeg leg, int field )
				{
				if (field != LegEvent.QUANTITY)
					return;
//				if (leg.getQty() != 0 && bailOutPrice != 0)
//					{
//					leg.setGFO(bailOutPrice);
//					bailOutPrice = 0;
//					}
				}
			});
		}

	public String getShortName() { return "Chaos2"; }

	public String getMetrics ( int index, Bar bar )
		{
		return "bias: " + chaos2.bias(index)
				+ " pos: " + chaos2.barPosition(index);
		}

	private void order(Bar bar, int qty, int stopIn, String why)
		{
		if ( fLeg.getTrader().isSet( Trader.MODE_INHIBIT ))
			return;

int currentQty = fLeg.getQty();

if (qty * currentQty < 0)
	qty -= currentQty;

bailOutPrice = (qty < 0)?bar.getHigh()+1:bar.getLow()-1;

		fLeg.add(new OrderTracker(fLeg, STP_ORDER, qty, stopIn, 0, 0, why));
		}

	boolean isSquatOrGreen(Bar bar, Bar prevBar)
		{
		return bar.getVolume() > prevBar.getVolume();
		}

	private boolean isBullishReversal(int index, Bar bar)
		{
		int back1 = index-1;
		Bar prevBar = bar(back1);
		int thisLo = bar.getLow();
		int prevLo = prevBar.getLow();

		return bar.range() > 16  //&& prevBar.range() > 5
		    && bar.getClose() >= bar.midpoint()
		    && (thisLo <= prevLo)
		    && isSquatOrGreen(bar, prevBar);
		}

	private boolean isBearishReversal(int index, Bar bar)
		{
		int back1 = index-1;
		Bar prevBar = bar(back1);
		int thisHi = bar.getHigh();
		int prevHi = prevBar.getHigh();

		return bar.range() > 16  //&& prevBar.range() > 5
		    && bar.getClose() <= bar.midpoint()
		    && (thisHi >= prevHi)
		    && isSquatOrGreen(bar, prevBar);
		}

	private static final String BEAR_REV = "\u21E3RB";
	private static final String BULL_REV = "\u21E1RB";
	private static final String UP_FRACTAL_BO = "\u2227BO";
	private static final String DN_FRACTAL_BO = "\u2228BO";
int bailOutPrice;
	public void automata( int index, Bar bar )
		{
		if ( index < 8 ) return;
		int qty = getQty();

fLeg.clearSoftOrders(UP_FRACTAL_BO, DN_FRACTAL_BO);
int slope = chaos2.gator.slope100(index);
if (slope > -100 && slope < 100) return;
/*********

if (qty <= 0 && isBullishReversal(index, bar))
	{
order(bar,100,bar.getHigh()+1,BULL_REV);
//	goLong(BULL_REV); // if we're short or flat, check for a buy signal
	return;
	}
if (qty >= 0 && isBearishReversal(index, bar))
	{
order(bar, -100, bar.getLow()+1, BEAR_REV);
//	goShort(BEAR_REV); // if we're short or flat, check for a buy signal
	return;
	}
*********/

try
{
int goBackFrom = index; // index -1;
int xPriorFractal = chaos2.fractals.priorUp(goBackFrom);
int fractalPrice;
if ( xPriorFractal >= 0
&&   bar.getHigh() <= (fractalPrice = bar(xPriorFractal).getHigh())
&&   fractalPrice > chaos2.gator.red.getValue(xPriorFractal))
	order(bar, 100, fractalPrice+1, UP_FRACTAL_BO);

xPriorFractal = chaos2.fractals.priorDown(goBackFrom);
if ( xPriorFractal >= 0
&&   bar.getLow() >= (fractalPrice = bar(xPriorFractal).getLow())
&&   bar(xPriorFractal).getLow() < chaos2.gator.red.getValue(xPriorFractal))
	order(bar, -100, fractalPrice-1, DN_FRACTAL_BO);
} catch (Exception ex) { ex.printStackTrace(); System.exit(1); }

/***********
//		String hhmm = SBDate.toTime(bar.getTime());
		int prevMidpoint = getTape().get(index-1).midpoint();
		if (qty <= 0) // if we're short or flat, check for a buy signal
			{
			if ( bar.change() > 3
			&&   bar.midpoint() > prevMidpoint
//			&&   chaos2.upupAbove(index))
			&&   chaos2.above(index))
				{
				bailOutPrice = bar.getLow() - 2;
				goLong("above");
				return;
				}
			else if ((qty < 0) && (bar.getHigh() > bailOutPrice))
				{
				goFlat();
				return;
				}
			}

		if (qty >= 0)
			{
			if (bar.change() < -3
			&&   bar.midpoint() < prevMidpoint
			&&   chaos2.below(index))
//			&&   chaos2.dndnBelow(index))
				{
				bailOutPrice = bar.getHigh() + 1;
				goShort("below");
				return;
				}
			else if ((qty > 0) && (chaos2.divergentDown() || bar.getLow() < bailOutPrice))
//			if ( bar.getLow() < (getTape().get(index-1).getLow()-2))
				goFlat();
			}
***********/
		}
	}