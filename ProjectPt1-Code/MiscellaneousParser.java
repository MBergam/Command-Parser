package sbw.project.cli.parser;


import sbw.architecture.datatype.Rate;
import sbw.project.cli.action.ActionMiscellaneous;
import sbw.project.cli.action.command.misc.CommandDoClockUpdate;
import sbw.project.cli.action.command.misc.CommandDoExit;
import sbw.project.cli.action.command.misc.CommandDoRunCommandFile;
import sbw.project.cli.action.command.misc.CommandDoSetClockRate;
import sbw.project.cli.action.command.misc.CommandDoSetClockRunning;
import sbw.project.cli.action.command.misc.CommandDoShowClock;
import sbw.project.cli.action.command.misc.CommandDoWait;

/**
 * 
 * @author Max Bergam
 * @version 7 June 2019
 *
 */

public class MiscellaneousParser {

	private ActionMiscellaneous am;
	
	public MiscellaneousParser(ActionMiscellaneous am) {
		this.am = am;
	}

	public boolean validateCommand(String[] command) {
		
		if (command.length == 1) {//clock & exit
			if (command[0].equalsIgnoreCase("@clock") || command[0].equalsIgnoreCase("@exit")) {
				return true;
			}
		}
		
		if (command.length == 2) {//clock rate & pause/resume/update
			if (command[0].equalsIgnoreCase("@clock") && (command[1].equalsIgnoreCase("pause") || command[1].equalsIgnoreCase("resume") || command[1].equalsIgnoreCase("update"))) {
				return true;
			} else if (isIntegerPositive(command[1]) > 0) {
				return true;
			}
		}
		
		if (command.length == 2) {//clock rate & pause/resume/update
			if (command[0].equalsIgnoreCase("@wait") && isIntegerPositive(command[1]) > 0) {
				return true;
			}
		}
		
		if (command.length == 2) {//run 
			if (command[0].equalsIgnoreCase("@run")) {
				int length = command[1].length();
				if (command[1].charAt(0) == '"' && command[1].charAt(length - 1) == '"') {
					return true;
				}
			}
		}
		
		return false; //invalid command
	}

	public void parseCommand(String[] command) {
		
		if (command[0].equalsIgnoreCase("@exit")) {
			CommandDoExit comDoExit = new CommandDoExit();
			this.am.submitCommand(comDoExit);
		}
		
		if (command[0].equalsIgnoreCase("@clock") && command.length == 1) {
			CommandDoShowClock comDoShowClock = new CommandDoShowClock();
			this.am.submitCommand(comDoShowClock);
		}
		
		
		if (command[0].equalsIgnoreCase("@clock") && command.length == 2) {
			if (isIntegerPositive(command[1]) > 0) {
				int r = Integer.parseInt(command[1]);
				Rate rate = new Rate(r);
				CommandDoSetClockRate comDoSetClockRate = new CommandDoSetClockRate(rate);
				this.am.submitCommand(comDoSetClockRate);
				
			} else if(command[1].equalsIgnoreCase("pause")) {
				CommandDoSetClockRunning comDoSetClockPause = new CommandDoSetClockRunning(false);
				this.am.submitCommand(comDoSetClockPause);
				
			} else if(command[1].equalsIgnoreCase("resume")) {
				CommandDoSetClockRunning comDoSetClockResume = new CommandDoSetClockRunning(true);
				this.am.submitCommand(comDoSetClockResume);
				
			} else if(command[1].equalsIgnoreCase("update")) {
				CommandDoClockUpdate comDoClockUpdate = new CommandDoClockUpdate();
				this.am.submitCommand(comDoClockUpdate);
			}
			
		}
		
		if (command[0].equalsIgnoreCase("@wait")) {
			int r = Integer.parseInt(command[1]);
			Rate rate = new Rate(r);
			CommandDoWait comDoWait = new CommandDoWait(rate);
			this.am.submitCommand(comDoWait);
		}
		
		if (command[0].equalsIgnoreCase("@run")) {
			int length = command[1].length();
			String filename = command[1].substring(1, length - 1);
			CommandDoRunCommandFile comDoRunCommandFile = new CommandDoRunCommandFile(filename);
			this.am.submitCommand(comDoRunCommandFile);
		}
		
		
		
	}
	
	//helper method to check whether an entered in string is a number, and if it is positive
	private int isIntegerPositive(String number) {
		
	    try {
	        int num = Integer.parseInt(number);
	        return num;
	    }
	    catch(Exception e) {
	        return -1;
	    }
	}

}
