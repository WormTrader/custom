package com.wormtrader.custom.worms;
/********************************************************************
* @(#)Donchian.java 1.00 2009????
* Copyright © 2010 by Richard T. Salamone, Jr. All rights reserved.
*
* Donchian: Buys/Sells breakouts of the Donchian Bands.
*
* @author Rick Salamone
* @version 1.00
* 2009???? rts created
*******************************************************/
import com.wormtrader.bars.Bar;
import com.wormtrader.history.Tape;
import com.wormtrader.history.indicators.DonchianBands;
import com.wormtrader.positions.TapeWorm;

public final class Donchian extends TapeWorm
	{
	private DonchianBands m_don;

	public Donchian()
		{
		super();
		setWarmUpPeriods( 15 );
		}

	public void initTape(Tape tape)
		{
		m_don = (DonchianBands)tape.addStudy ( DonchianBands.STUDY_NAME,  "" );
		}

	public String getShortName() { return "Donch"; }
	public String getMetrics ( int index, Bar bar )	// called  before automata()
		{
		int hiBand = m_don.getUpperBand(index-1);
		int loBand = m_don.getLowerBand(index-1);
		return "lo=" + loBand + " hi=" + hiBand;
		}

	public void automata( int index, Bar bar )
		{
		int hiBand = m_don.getUpperBand(index-1);
		int loBand = m_don.getLowerBand(index-1);
		int close = bar.getClose();
		if ( close > hiBand )
			{
			if ( getQty() <= 0 ) // reverse to long
				goLong();
			}
		else if ( close < loBand )
			{
			if ( getQty() >= 0 )
				goShort();
			}
		}
	}