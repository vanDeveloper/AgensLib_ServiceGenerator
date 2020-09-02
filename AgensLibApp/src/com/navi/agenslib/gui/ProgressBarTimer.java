/************************************************************************************************************************************
 * LICENSE
 ************************************************************************************************************************************
 * 	This file is part of AgensLib.
 * 
 *  AgensLib is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  AgensLib is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.

 *  You should have received a copy of the GNU General Public License
 *  along with AgensLib.  If not, see <https://www.gnu.org/licenses/>.
*/
package com.navi.agenslib.gui;

import java.awt.event.*;
import javax.swing.*;

import com.navi.agenslib.gui.callbacks.AgensLibCallback;
import com.navi.agenslib.mas.MasSetup;
import com.navi.agenslib.mas.utils.AgentUtils;

/**
 * Created by van on 10/06/20.
 */
class ProgressBarTimer {

	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- PROPERTIES
	//--------------------------------------------------------------------------------------------------------------------------------
	private Timer timerProgress;
	private Timer timerLog;
    
    //--------------------------------------------------------------------------------------------------------------------------------
  	//MARK:- CONSTRUCTOR
  	//--------------------------------------------------------------------------------------------------------------------------------
    public ProgressBarTimer(JLabel lblLog, JProgressBar progress, AgensLibCallback lib) {
    	
        ActionListener listener = new ActionListener() {
            int counter = 0;
            
            public void actionPerformed(ActionEvent ae) {
                counter++;
                progress.setValue(counter);
                
                if (counter > 60) {
                	timerProgress.stop();
                	lib.clean();
                } else if(MasSetup.finished) {
                	timerProgress.stop();
                	lib.clean();
                }
            }
        };
        timerProgress = new Timer(1000, listener);
        timerProgress.start();
        
        ActionListener logListener = new ActionListener() {
            int counter = 0;
            
            public void actionPerformed(ActionEvent ae) {
                counter++;
                lblLog.setText(AgentUtils.logMessage);
                
                if (counter > 1000 * 60) {
                	timerLog.stop();
                } else if(MasSetup.finished) {
                	timerLog.stop();
                }
            }
        };
        timerLog = new Timer(100, logListener);
        timerLog.start();
    }
    

}