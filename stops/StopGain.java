package com.wormtrader.custom.stops;
/*
 * @(#)Stop.java	1.00 07/11/12
 *
 */

import com.wormtrader.positions.ExitMethod;

public class StopGain
	extends ExitMethod
	{
	public static final String TAKE_GAIN = "TGT";
	private int fTarget;

	protected final void positionClosed()
		{
		super.positionClosed();
		fTarget = 0;
		}

	public void positionOpened( int qty, int basis )
		{
		super.positionOpened( qty, basis );
		fTarget = (fAmount == null)? 0
		         :(qty > 0)? fAmount.addTo(basis)
		         :           fAmount.subtractFrom(basis);
		trace("setTarget: " + dollarString(fTarget));
		}

	@Override protected void lastPrice( int qty, int price )
		{
//		trace( String.format("lastPrice(%d,%d)", qty, lastPrice ));
		if (( fTarget != 0 )
		&& ((qty > 0 && price >= fTarget) || (qty < 0 && price <= fTarget)))
			{
			fTarget = 0;
			fireStop(TAKE_GAIN);
			}
		}
	}
