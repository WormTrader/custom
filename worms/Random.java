package com.wormtrader.custom.worms;
/********************************************************************
* @(#)Random.java 1.00 20130507
* Copyright © 2013 by Richard T. Salamone, Jr. All rights reserved.
*
* Random: On every bar checks if there is an open position. If not,
* this worm randomly goes long or flat at the market. Useful for
* testing exit methods and sizing algorithms.
*
* @author Rick Salamone
* @version 1.00
* 20130507 rts created
*******************************************************/
import com.wormtrader.bars.Bar;
import com.wormtrader.positions.TapeWorm;

public final class Random
	extends TapeWorm
	{
	private final java.util.Random random = new java.util.Random();

	public String getShortName() { return "Random"; }

	public void automata( int index, Bar bar )
		{
		if ( getQty() == 0 )
			{
			boolean goLong = random.nextBoolean();
			if ( goLong )
				goLong();
			else
				goShort();
			}
		}
	}