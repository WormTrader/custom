package com.wormtrader.custom.scans;

import com.wormtrader.history.Scanner;
import com.wormtrader.history.Tape;
import com.wormtrader.history.indicators.TDSetup;

public class TDSetups extends Scanner
	{
	private TDSetup m_setup;

	public String toString() { return "TD Setups"; }

	public void initialize( Tape tape )
		{
		m_setup = (TDSetup)tape.addStudy ( TDSetup.STUDY_NAME, "" );
		}

	@Override public boolean isHit(Tape tape)
		{
		int index = tape.size() - 1;
		int setup = m_setup.getValue( index );
		return (( setup >= 9 ) || ( setup <= -9 ));
		}
	}