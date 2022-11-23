package com.wormtrader.custom.worms;
/********************************************************************
* @(#)BWWiseMan1a.java 1.00 20130417
* Copyright © 2013 by Richard T. Salamone, Jr. All rights reserved.
*
* BWWiseMan1a: Implements the Bill Williams "First Wise Man" as
* described in Trading Chaos 2nd Edition Chapter 9. It is saved
* for posterity as the first profitable system I have written -
* It makes bank when run on USO or EEV M5 20080701 - 20100801.
*  But this version...
*
* 1) bypasses the signaling mechanism to directly place orders.
* 2) has a weak implementation of divergent bar
*
* @author Rick Salamone
* @version 1.00
* 20130417 rts created
*******************************************************/
import com.wormtrader.bars.Bar;
import com.wormtrader.history.Tape;
import com.wormtrader.history.indicators.Alligator;
import com.wormtrader.history.indicators.Fractals;
import com.wormtrader.positions.TapeWorm;
import com.wormtrader.positions.*;
import com.shanebow.util.SBFormat;
import com.wormtrader.broker.OrderTracker;
import static com.wormtrader.broker.Broker.STP_ORDER;

public final class BWWiseMan1a
	extends TapeWorm
	{
	private static final int FLAT=0;
	private static final int LONG=1;
	private static final int SHORT=2;
	private static final int CANCELED=3; // can order pending
	// following states return true for orderPending()
	private static final int LONG_SIG=4;
	private static final int SHORT_SIG=5;
	private static final int REV_LONG_SIG=6;
	private static final int REV_SHORT_SIG=7;

	private int fState;
	private OrderTracker fOrderTracker;
	private int fCancelPrice;
	private Alligator fGator;
	private Fractals fFractals;
	private PositionLeg fLeg;

	private boolean orderPending() { return fState >= LONG_SIG; }

	public BWWiseMan1a()
		{
		super();
		setWarmUpPeriods( 8 );
		}

	public void initTape(Tape tape)
		{
		fGator = new Alligator(tape);
		fFractals = (Fractals)tape.addStudy(Fractals.STUDY_NAME, "");
		fLeg = getLeg();
		fLeg.addListener(new LegListener()
			{
			public void legDataChanged ( PositionLeg leg, int field )
				{
				if (field == LegEvent.QUANTITY)
					{
					int qty = leg.getQty();
					if (qty == 0)
						{
						setState(FLAT, 65);
						}
					else if (orderPending())
						{
						if (qty > 0)
							order(-2 * qty, fCancelPrice, "Fail", LONG);
						else
							order(-2 * qty, fCancelPrice, "Fail", SHORT);
						}
					else
						{
				//		leg.setGFO(fCancelPrice);
						setState((qty > 0)? LONG : SHORT, 70);
						}
			// @TODO: where did qty come from? if not tracker, cancel it?
					}
				else if (orderPending() && field == LegEvent.PRICE_LAST)
					{
					int last = leg.getLast().cents();
					switch (fState)
						{
						case REV_LONG_SIG:
						case LONG_SIG:			if (last < fCancelPrice) canOrder();
														return;

						case REV_SHORT_SIG:
						case SHORT_SIG:		if (last > fCancelPrice) canOrder();
														return;
						}
					}
				else if (fState == CANCELED && field == LegEvent.ORDERS_CHANGED)
					{
					fOrderTracker = null;
					int qty = leg.getQty();
					setState((qty > 0)? LONG
					       : (qty < 0)? SHORT
					       :            FLAT, 94);
					}
				}
			});
		}

	public String getShortName() { return "Wise1"; }

	public String getMetrics ( int index, Bar bar )
		{
		return fGator.getToolTipText(index, bar);
		}

	private void order(int qty, int price, String why, int newState)
		{
		if ( fLeg.getTrader().isSet( Trader.MODE_INHIBIT ))
			{
			fState = FLAT;
			return;
			}
		fOrderTracker = new OrderTracker(fLeg, STP_ORDER, qty, price, 0, 0, why);
		fLeg.add(fOrderTracker);
		setState(newState, 119);
		}

	private void canOrder()
		{
		try
			{
			setState(CANCELED, 127);
			fLeg.remove(fOrderTracker);
			}
		catch (Exception e)
			{
			// @TODO: dialog here for failed order?
			log("Cancel order FAILED for order: " + fOrderTracker);
			fOrderTracker = null;
			int qty = fLeg.getQty();
			setState((qty > 0)? LONG
			       : (qty < 0)? SHORT
			       :            FLAT, 137);
			}
		}

	private void signalRevLong(Bar bar, String why)
		{
		int qty = fLeg.getDefaultSize() - fLeg.getQty();
		int entryPrice = bar.getHigh() + 1;
		fCancelPrice = bar.getLow() - 1;
		order(qty, entryPrice, why, REV_LONG_SIG);
		}

	private void signalRevShort(Bar bar, String why)
		{
		int qty = -fLeg.getDefaultSize() - fLeg.getQty();
		int entryPrice = bar.getLow() - 1;
		fCancelPrice = bar.getHigh() + 1;
		order(qty, entryPrice, why, REV_SHORT_SIG);
		}

	private void signalLong(Bar bar, String why)
		{
		int qty = fLeg.getDefaultSize();
		int entryPrice = bar.getHigh() + 1;
		fCancelPrice = bar.getLow() - 1;
		order(qty, entryPrice, why, LONG_SIG);
		}

	private void signalShort(Bar bar, String why)
		{
		int qty = -fLeg.getDefaultSize();
		int entryPrice = bar.getLow() - 1;
		fCancelPrice = bar.getHigh() + 1;
		order(qty, entryPrice, why, SHORT_SIG);
		}

	boolean isBullDiSignal(byte diType, int x)
		{
		return diType == Alligator.DI_BULL
		    && fGator.angulation100(x) > 200;
		}

	boolean isBearDiSignal(byte diType, int x)
		{
		return diType == Alligator.DI_BEAR
		    && fGator.angulation100(x) > 200;
		}

	private static final String BEAR_REV = "\u21E3RB";
	private static final String BULL_REV = "\u21E1RB";
	private static final String UP_FRACTAL_BO = "\u2227BO";
	private static final String DN_FRACTAL_BO = "\u2228BO";

	public void automata( int x, Bar bar )
		{
		if ( x < 8 || orderPending())
			return;

		byte diType = fGator.diType(x);
		if (diType == Alligator.DI_NONE)
			return;

		switch(fState)
			{
			case FLAT:	if (isBearDiSignal(diType, x))
									signalShort(bar, BEAR_REV);
								else if (isBullDiSignal(diType, x))
									signalLong(bar, BULL_REV);
								return;

			case LONG:	if (isBearDiSignal(diType, x))
									signalRevShort(bar, BEAR_REV);
								return;

			case SHORT:	if (isBullDiSignal(diType, x))
									signalRevLong(bar, BULL_REV); // was BEAR??
								return;
			}
		}

	private static String[] STATES=
		{	"FLAT","LONG","SHORT","CANCELED",
			"LONG_SIG","SHORT_SIG","REV_LONG_SIG","REV_SHORT_SIG" };
	private void setState(int newState, int line)
		{
		log("" + line + ": " + STATES[fState] + " -> " + STATES[newState]);
		fState = newState;
		}
	}