package com.wormtrader.custom.scans;

import com.wormtrader.history.Scanner;
import com.wormtrader.bars.Bar;
import com.wormtrader.history.Tape;
import com.wormtrader.history.indicators.Stochastic;

public class BullishEngulfing extends Scanner
	{
	private Stochastic m_stoch;

	public String toString() { return "Bullish Engulfing Candlesticks"; }

	public void initialize( Tape tape )
		{
		m_stoch = (Stochastic)tape.addStudy ( "Stochastics", "" );
		}

	@Override public boolean isHit(Tape tape)
		{
		int index = tape.size() - 1;
		if ((index < 1)
		|| !m_stoch.isOversold(index))
			return false;
		Bar bar = tape.get( index );
		int open = bar.getOpen();
		int close = bar.getClose();
		bar = tape.get( index - 1 );
		int yestOpen = bar.getOpen();
		int yestClose = bar.getClose();
		
		return ((yestClose < yestOpen) && (open < yestClose) && (close > yestOpen));
		}
	}