package com.wormtrader.custom.stops;
/*
 * @(#)Stop.java	1.00 07/11/12
 *
 */
import com.wormtrader.positions.ExitMethod;

public class StopLoss
	extends ExitMethod
	{
	public static final String WHY="STOP";

	public void positionOpened( int qty, int basis )
		{
		super.positionOpened( qty, basis );
		if ( fAmount != null )
			adjustTrigger((qty > 0)? fAmount.subtractFrom(basis)
			                       : fAmount.addTo(basis), WHY);
		}
	}
