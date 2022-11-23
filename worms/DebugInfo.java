package com.wormtrader.custom.worms;
/********************************************************************
* @(#)DebugInfo.java 1.00 20130417
* Copyright © 2013 by Richard T. Salamone, Jr. All rights reserved.
*
* DebugInfo: Spits out various information about the data structures
* to assure correct operation of the simulator.
*
* @author Rick Salamone
* @version 1.00
* 20130501 rts created
*******************************************************/
import com.wormtrader.bars.Bar;
import com.wormtrader.bars.BarSize;
import com.wormtrader.history.*;
import com.wormtrader.positions.TapeWorm;
import com.wormtrader.positions.*;
import com.shanebow.util.SBDate;

public final class DebugInfo
	extends TapeWorm
	{
	public static final String CLOSE_TIME = "15:50";

	public String getShortName() { return "Info"; }
	public BarSize getMaxBarSize() { return BarSize.FIVE_MIN; }

	public String getMetrics ( int index, Bar bar )	// called  before automata()
		{
		return bar.hhmm();
		}

	int startReal;
	public void automata( int index, Bar bar )
		{
		String hhmm = bar.hhmm();

		if ( bar.dow() == 2 && hhmm.equals( "09:30" ))
//		if ( hhmm.equals( "09:30" ))
			{
			System.out.println("**** " + SBDate.DDD(bar.getTime()) + " " + bar.yyyymmdd());
//System.out.println("bar.dow: " + bar.dow());
			PositionLeg leg = getLeg();
			TripleTape t3 = leg.getTapeSet();
			for (Tape tape : t3.getTapes())
				System.out.println(tape.getBarSize().toString() + ": " + tape.size() + " bars");
			System.out.println("Soft Orders: " + leg.getSoftOrders().size());
			System.out.println("Realized: " + leg.getRealized());
			}
		}
	}