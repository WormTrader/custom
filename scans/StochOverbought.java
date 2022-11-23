package com.wormtrader.custom.scans;

import com.wormtrader.history.Scanner;
import com.wormtrader.bars.Bar;
import com.wormtrader.history.Tape;
import com.wormtrader.history.indicators.Stochastic;

public class StochOverbought extends Scanner
	{
	private Stochastic m_stoch;

	public String toString() { return "Overbought Stochastics"; }

	public void initialize( Tape tape )
		{
		m_stoch = (Stochastic)tape.addStudy ( "Stochastics", "" );
		}

	@Override public boolean isHit(Tape tape)
		{
		int index = tape.size() - 1;
		return (m_stoch.slowK(index) > 80)
		    && (m_stoch.slowD(index) > 80);
		}
	}