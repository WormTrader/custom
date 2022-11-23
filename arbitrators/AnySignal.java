package com.wormtrader.custom.arbitrators;
/********************************************************************
* @(#)AllAgree.java 1.00 20121029
* Copyright © 2012 by Richard T. Salamone, Jr. All rights reserved.
*
* AllAgree: A SignalArbitrator that only approves the signal sent to a
* strategy if all worms agree on the bias.
*
* @version 1.00
* @author Rick Salamone
* 20121029 rts created
*******************************************************/
import com.wormtrader.positions.SignalArbitrator;
import com.wormtrader.positions.TapeWorm;

public final class AnySignal
	extends SignalArbitrator
	{
	public String toString() { return "Any Signal"; }
	@Override public boolean approveLong(TapeWorm aSignaler, Iterable<TapeWorm> aWorms)
		{
		return true;
		}

	@Override public boolean approveShort(TapeWorm aSignaler, Iterable<TapeWorm> aWorms)
		{
		return true;
		}
	}