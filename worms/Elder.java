package com.wormtrader.custom.worms;
/********************************************************************
* @(#)Elder.java 1.00 20110601
* Copyright (c) 2011 by Richard T. Salamone, Jr. All rights reserved.
*
* Elder: Emulates Alexander Elder's trading style as put forth in
* his books, using his favorite indicators.
*
* @author Rick Salamone
* 20110601 rts first iteration
*******************************************************/
import com.wormtrader.bars.Bar;
import com.wormtrader.history.Tape;
import com.wormtrader.history.indicators.*;
import com.wormtrader.positions.TapeWorm;
import com.shanebow.util.SBFormat;

public final class Elder
	extends TapeWorm
	{
	ElderImpulse fImpulse;

	public Elder()
		{
		super();
		setWarmUpPeriods(3);
		}

	public void initTape(Tape tape)
		{
		fImpulse = (ElderImpulse)tape.addStudy(ElderImpulse.STUDY_NAME, "" );
		}

	public String getShortName() { return "Elder"; }

	public String getMetrics(int index, Bar bar)
		{
		return String.format("%3d) impuse=%2d ",
				index, fImpulse.getValue(index));
		}

	public void automata( int index, Bar bar )
		{
		if ( index == 0 )
			return;
		int impulse = fImpulse.getValue(index);
		if ( getQty() == 0 )
			{
			// @TODO: Only trade in direction of long term trend
			if ( impulse > 0 )
				goLong();
			else if ( impulse < 0 )
				goShort();
			}
		else if (( getQty() > 0 ) && (impulse < 0))
			goFlat();
		else if (( getQty() < 0 ) && (impulse > 0))
			goFlat();
		}
	}