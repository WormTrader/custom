package com.wormtrader.custom.worms;
/*
 * Random.java
 *
 * GAMBARU - Never Give Up (Japanese)
 *
 */
import com.wormtrader.bars.Bar;
import com.wormtrader.bars.BarSize;
import com.wormtrader.positions.TapeWorm;
/********************************************************************
* @(#)Reverse.java 1.00 2007
* Copyright © 2013 by Richard T. Salamone, Jr. All rights reserved.
*
* Reverse: On every bar checks if there is an open position. If not,
* this worm issues a market order in the opposite direction to the
* last time. Useful for testing exit methods and sizing algorithms.
*
* @author Rick Salamone
* @version 1.00
* 2007???? rts created
* 20130507 rts documented
*******************************************************/

public final class Reverse extends TapeWorm
	{
	private boolean m_long = true;

	public void automata( int index, Bar bar )
		{
		if ( getQty() == 0 )
			{
			if ( m_long )
				goLong();
			else
				goShort();
			m_long = !m_long; // so if we stop out we'll go other way
			}
		}
	}