package sbw.project.cli.parser;

/**
 * 
 * @author Max Bergam
 * @version 7 June 2019
 *
 */
import sbw.project.cli.action.ActionBehavioral;
import sbw.project.cli.action.ActionCreational;
import sbw.project.cli.action.ActionMiscellaneous;
import sbw.project.cli.action.ActionSet;
import sbw.project.cli.action.ActionStructural;

public class CommandParser {
	
	private ActionSet aSet;
	private String text;
	private String[] command;
	
	private ActionCreational ac;
	private ActionMiscellaneous am;
	private ActionStructural as;
	private ActionBehavioral ab;
	
	
	public CommandParser(ActionSet actionSet, String text) {
		this.aSet = actionSet;
		this.text = text;
		
		this.ac = this.aSet.getActionCreational();
		this.as = this.aSet.getActionStructural();
		this.ab = this.aSet.getActionBehavioral();
		this.am = this.aSet.getActionMiscellaneous();
		
	}
	
	public void parse() throws ParseException {
		
		int numOfCommands = prepCommand();
		String[] tempCmd;
		
		for (int x = 0; x < numOfCommands; x++) {
			
			tempCmd = this.command[x].trim().split(" ");
			
			if (tempCmd[0].equalsIgnoreCase("create")) {
				CreationalParser cPar = new CreationalParser(this.ac);
				if (cPar.validateCommand(tempCmd)) {
					cPar.parseCommand(tempCmd);
				} else {
					throw new ParseException("Invalid Creational Command");
				}
				
				
			} else if (tempCmd[0].equalsIgnoreCase("declare")) {
				StructuralParser sPar = new StructuralParser(this.as);
				if (sPar.validateCommand(tempCmd)) {
					sPar.parseCommand(tempCmd);
				} else {
					throw new ParseException("Invalid Structural Command");
				}
				
			} else if (tempCmd[0].equalsIgnoreCase("do") || tempCmd[0].equalsIgnoreCase("halt")) {
				BehavioralParser bPar = new BehavioralParser(this.ab);
				if (bPar.validateCommand(tempCmd)) {
					bPar.parseCommand(tempCmd);
				} else {
					throw new ParseException("Invalid Behavioral Command");
				}
				
			} else if (tempCmd[0].charAt(0) == '@') {
				MiscellaneousParser mPar = new MiscellaneousParser(this.am);
				if (mPar.validateCommand(tempCmd)) {
					mPar.parseCommand(tempCmd);
				} else {
					throw new ParseException("Invalid Miscellaneous Command");
				}
				
			} else if (tempCmd[0].equalsIgnoreCase("commit")) {
				this.as.doCommit();
				
			} else {
				throw new ParseException("Invalid Command");
			}
		}
	
			
		
	}
	
	private int prepCommand() {
		
		this.text = this.text.trim().replaceAll("\\s+", " ");//get rid of all extra white space
		
		String[] temp;
		if (this.text.charAt(0) == '/' && this.text.charAt(1) == '/') { //get rid of comments
			this.text = "";
			return 0;
		} else if (this.text.contains("//")) {
			temp = this.text.split("//");
			this.text = temp[0];
	
		}
		
		//count how many commands are in the entered in statement based off of how many semicolons
		this.command = this.text.split(";");
		int numOfCommands = this.command.length;
		return numOfCommands;
		
	}
	
}
