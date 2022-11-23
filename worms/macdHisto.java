package com.wormtrader.custom.worms;
/*
 * ElderImpulse.java
 * 
 * Presented by Dr. Alexander Elder in "Come into My Trading Room".
 * Two indicators are used, one to measure inertia and the other momentum:
 * 	a) for momentum use the slope of the MACD(12,26,9) histogram,
 * 	b) for inertia use the slope of the EMA(13).
 * Elder says "when both point in the same direction, it's an impulse
 * worth following."
 * Rules:
 *    1) When the slope of both indicators is up, it's a strong buy
 *       signal. Sell when the buy signal disappears.
 *    2) Reverse rule one for short positions.
 *    3) In either case, only trade in the direction of the long term
 *       trend (e.g. slope of the 26 week EMA).
 *
 */
import com.wormtrader.bars.Bar;
import com.wormtrader.history.Tape;
import com.wormtrader.history.indicators.*;
import com.wormtrader.positions.TapeWorm;
import com.shanebow.util.SBFormat;

public final class macdHisto extends TapeWorm
	{
	MACD  m_macd;

	public macdHisto()
		{
		super();
		setWarmUpPeriods(3);
		}

	public void initTape(Tape tape)
		{
		m_macd = (MACD)tape.addStudy(MACD.STUDY_NAME, "" );
		}

	public String getShortName() { return "Impulse"; }

	public String getMetrics(int index, Bar bar)
		{
		return String.format("MACD histo: %d", m_macd.getHistogram(index));
		}

	public void automata( int index, Bar bar )
		{
		if ( index < 5 )
			return;
		int momentum = m_macd.getHistogram(index);
		if (( getBias() <= NEUTRAL_BIAS ) && ( momentum > 0 ))
			goLong();
		else if (( getBias() >= NEUTRAL_BIAS ) && ( momentum < 0 ))
			goShort();
		}
	}