package com.wormtrader.custom.stops;
/*
 * @(#)TrailingStop.java	1.00 07/11/12
 *
 */

import com.wormtrader.positions.ExitMethod;

public final class TrailPrice
	extends ExitMethod
	{
	public String toString() { return "Trail " + fAmount; }

	public void positionOpened( int qty, int basis )
		{
		trace( String.format("positionOpened(%d,%d)", qty, basis ));
		super.positionOpened( qty, basis );
		lastPrice( qty, basis );
		}

	@Override protected void lastPrice( int qty, int price )
		{
		int newTrigger = (qty > 0)? fAmount.subtractFrom(price)
		                          : fAmount.addTo(price);
		trace( String.format( "calcTrail(%d) = %d", price, newTrigger ));
		adjustTrigger( newTrigger, "TP" );
		}
	}
