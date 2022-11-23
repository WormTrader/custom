package com.wormtrader.custom.worms;
/*
 * POQ.java
 * 
 *
 */
import com.wormtrader.bars.Bar;
import com.wormtrader.history.Tape;
import com.wormtrader.history.indicators.*;
import com.wormtrader.positions.TapeWorm;
import com.shanebow.util.SBFormat;
import com.shanebow.util.SBLog;

public final class POQ extends TapeWorm
	{
	private REI m_rei;

	public POQ()
		{
		super();
		setWarmUpPeriods(8);
		}

	public void initTape(Tape tape)
		{
		m_rei = (REI)tape.addStudy(REI.STUDY_NAME, "" );
		}

	public String getSoundFile() { return "bleep_1"; }
	public String getMetrics(int index, Bar bar)
		{
		return String.format("%3d) REI=%5d duration: %2d",
						index, m_rei.reading(index), m_rei.duration(index));
		}

	public void automata( int index, Bar bar )
		{
		if ( index < 5 )
			return;

		int qty = getQty();
		if ( qty != 0 )
			{
			int rei1 = m_rei.reading(index-1);
			int rei =  m_rei.reading(index);
			if (( qty > 0 && rei < rei1 )  // long & rei going down
			||  ( qty < 0 && rei > rei1 )) // or short & rei going up
				goFlat();
			}

		int duration = m_rei.duration( index );
		if ( duration == 0 || duration > 5 || duration < -5 )
			return;

		Tape tape = getTape();
		int c2 = tape.get(index-2).getClose();
		Bar prev = tape.get(index-1);

		if ( duration < 0 ) // mild oversold
			{
if ( bar.getClose() > prev.getClose())
	alert("Long if nextbar OPENS < " + SBFormat.toDollarString(bar.getHigh())
	+ " THEN TRADES UP!");

	/***************
			if ( prev.getClose() <= c2 ) // DQ still going down
				return;
			int h1 = prev.getHigh();
			if (( bar.getOpen() < h1 ) && ( bar.getHigh() > h1 ))
				goLong();
	***************/
			return;
			}
		if ( duration > 0 ) // mild overbought
			{
if ( bar.getClose() < prev.getClose())
	alert("SHORT if next bar opens >" + SBFormat.toDollarString(bar.getLow())
	+ " THEN TRADES DOWN!");

	/***************
			if ( prev.getClose() >= c2 ) // DQ still going up
				return;
			int lo1 = prev.getHigh();
			if (( bar.getOpen() > lo1 ) && ( bar.getLow() < lo1 ))
				goShort();
	***************/
			return;
			}
		}
	}