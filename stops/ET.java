package com.wormtrader.custom.stops;
/*
 * @(#)TrailingStop.java 1.00 07/11/12
 *
 */
import com.wormtrader.positions.ExitMethod;

public final class ET
	extends ExitMethod
	{
	public void positionOpened( int qty, int basis )
		{
		super.positionOpened( qty, basis );
		}

	@Override protected void lastPrice( int qty, int price )
		{
		int basis = getLeg().getBasis().cents();

		if (( qty > 0 ) && (price > basis))
			adjustTrigger(basis + (price - basis)/2, "ET");

		else if (( qty < 0 ) && (price < basis))
			adjustTrigger(basis + (price - basis)/2, "ET");
		}
	}
