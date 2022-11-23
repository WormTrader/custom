package com.wormtrader.custom.worms;
/*
 * Seykota.java
 *
 * GAMBARU - Never Give Up (Japanese)
 *
 */
import com.wormtrader.bars.Bar;
import com.wormtrader.history.Tape;
import com.wormtrader.history.indicators.ATR;
import com.wormtrader.history.indicators.MovingAverage;
import com.wormtrader.positions.TapeWorm;

public final class Seykota extends TapeWorm
	{
	private ATR m_atr;
	private MovingAverage m_maFast;
	private MovingAverage m_maSlow;

	public Seykota()
		{
		super();
		setWarmUpPeriods( 22 ); // 25
		}

	public void initTape(Tape tape)
		{
		m_maFast = (MovingAverage)tape.addStudy ( "EMA",  "15,C" );
		m_maSlow = (MovingAverage)tape.addStudy ( "EMA", "150,C" );
		m_atr = (ATR)tape.addStudy ( "ATR", "" );
		}

	public String getShortName() { return "SEY150_15"; }
	public String getMetrics ( int index, Bar bar )	// called  before automata()
		{																			// if debugging
		int slow = m_maSlow.getValue(index);
		int fast = m_maFast.getValue(index);
		int atr = m_atr.getValue(index);
		return String.format("slow=%5d fast=%5d atr=%5d%s", slow, fast, atr,
					((fast > slow) ? " +" : " -"));
		}

	public void automata( int index, Bar bar )
		{
		int slow = m_maSlow.getValue(index);
		int fast = m_maFast.getValue(index);
		int atr = m_atr.getValue(index);

		if (( fast > slow ) && ( getQty() <= 0 ))
			goLong();
		else if (( fast < slow ) && ( getQty() >= 0 ))
			goShort();
		}
	}