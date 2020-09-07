package sbw.project.cli.parser;

import sbw.architecture.datatype.Acceleration;
import sbw.architecture.datatype.Angle;
import sbw.architecture.datatype.Identifier;
import sbw.architecture.datatype.Speed;
import sbw.project.cli.action.ActionCreational;

/**
 * 
 * @author Max Bergam
 * @version 7 June 2019
 *
 */

public class CreationalParser {
	
	//The object used to access the methods needed to send values to the architecture
	private ActionCreational ac;
	
	public CreationalParser(ActionCreational ac) {
		this.ac = ac;
	}
	
	
	//After checking that the command is valid, this method then parses the command, extracts the values needed, then sends them off to the
	//architecture appropriately.
	public void parseCommand(String[] array) throws ParseException {
		
		if (array[1].equalsIgnoreCase("rudder")) {
			String id = array[2];
			Identifier rid = new Identifier(id);
			
			double angle = Double.parseDouble(array[5]);
			Angle ang = new Angle(angle);
			
			double speed = Double.parseDouble(array[7]);
			Speed spe = new Speed(speed);
			
			double acceleration = Double.parseDouble(array[9]);
			Acceleration acc = new Acceleration(acceleration);
			
			this.ac.doCreateRudder(rid, ang, spe, acc);
		}
		
		if (array[1].equalsIgnoreCase("elevator")) {
			String id = array[2];
			Identifier rid = new Identifier(id);
			
			double angle = Double.parseDouble(array[5]);
			Angle ang = new Angle(angle);
			
			double speed = Double.parseDouble(array[7]);
			Speed spe = new Speed(speed);
			
			double acceleration = Double.parseDouble(array[9]);
			Acceleration acc = new Acceleration(acceleration);
			
			this.ac.doCreateElevator(rid, ang, spe, acc);
		}
		
		if (array[1].equalsIgnoreCase("aileron")) {
			String id = array[2];
			Identifier rid = new Identifier(id);
			
			double angleUp = Double.parseDouble(array[6]);
			Angle angU = new Angle(angleUp);
			
			double angleDown = Double.parseDouble(array[8]);
			Angle angD = new Angle(angleDown);
			
			double speed = Double.parseDouble(array[10]);
			Speed spe = new Speed(speed);
			
			double acceleration = Double.parseDouble(array[12]);
			Acceleration acc = new Acceleration(acceleration);
			
			this.ac.doCreateAileron(rid, angU, angD, spe, acc);
		}
		
		if (array[1].equalsIgnoreCase("split") || array[1].equalsIgnoreCase("fowler")) {
			
			boolean isFowler = true;
			if (array[1].equalsIgnoreCase("split")) {
				isFowler = false;
			}
			
			String id = array[3];
			Identifier rid = new Identifier(id);
			
			double angle = Double.parseDouble(array[6]);
			Angle ang = new Angle(angle);
			
			double speed = Double.parseDouble(array[8]);
			Speed spe = new Speed(speed);
			
			double acceleration = Double.parseDouble(array[10]);
			Acceleration acc = new Acceleration(acceleration);
			
			this.ac.doCreateFlap(rid, isFowler, ang, spe, acc);
		}
		
		
		if (array[1].equalsIgnoreCase("engine")) {
			
			String id = array[2];
			Identifier rid = new Identifier(id);
			
			double speed = Double.parseDouble(array[5]);
			Speed spe = new Speed(speed);
			
			double acceleration = Double.parseDouble(array[7]);
			Acceleration acc = new Acceleration(acceleration);
			
			this.ac.doCreateEngine(rid, spe, acc);
		}
		
		if (array[1].equalsIgnoreCase("nose")) {
			
			String id = array[3];
			Identifier rid = new Identifier(id);
			
			double speed = Double.parseDouble(array[6]);
			Speed spe = new Speed(speed);
			
			double acceleration = Double.parseDouble(array[8]);
			Acceleration acc = new Acceleration(acceleration);
			
			this.ac.doCreateGearNose(rid, spe, acc);
		}
		
		if (array[1].equalsIgnoreCase("main")) {
			
			String id = array[3];
			Identifier rid = new Identifier(id);
			
			double speed = Double.parseDouble(array[6]);
			Speed spe = new Speed(speed);
			
			double acceleration = Double.parseDouble(array[8]);
			Acceleration acc = new Acceleration(acceleration);
			
			
			this.ac.doCreateGearMain(rid, spe, acc);
		}
		
		
	}

	
	
	//validates that the command is in a correct Creational format with acceptable values. Returns true if it is, false if it is not.
	public boolean validateCommand(String[] command) throws ParseException {
		
		
		//rudder, elevator
		if (command.length == 10) {
			if (command[1].equalsIgnoreCase("rudder") || command[1].equalsIgnoreCase("elevator")) {
				if (command[3].equalsIgnoreCase("with") && command[4].equalsIgnoreCase("limit") && command[6].equalsIgnoreCase("speed") && command[8].equalsIgnoreCase("acceleration")) {
					if (isIdValid(command[2]) && isDoublePositive(command[5]) >= 0 && isDoublePositive(command[7]) > 0 && isDoublePositive(command[9]) >= 0) {
						return true;
					} else {
						throw new RuntimeException("Attempt to use an invalid value");
					}
				}
			}
		}
		
		
		if (command.length == 13) {//aileron
			if (command[1].equalsIgnoreCase("aileron") && command[3].equalsIgnoreCase("with") && command[4].equalsIgnoreCase("limit") && command[5].equalsIgnoreCase("up")
				&& command[7].equalsIgnoreCase("down") && command[9].equalsIgnoreCase("speed") && command[11].equalsIgnoreCase("acceleration")) {
				if (isIdValid(command[2]) && isDoublePositive(command[6]) >= 0 && isDoublePositive(command[8]) >= 0
					&& isDoublePositive(command[10]) > 0 && isDoublePositive(command[12]) >= 0) {
						return true;
				} else {
					throw new RuntimeException("Attempt to use an invalid value");
				}
			}
		} 
		
		
		if (command.length == 11) {//split flap, fowler flap
			if (command[1].equalsIgnoreCase("split") || command[1].equalsIgnoreCase("fowler")) {
				if (command[2].equalsIgnoreCase("flap") && command[4].equalsIgnoreCase("with") && command[5].equalsIgnoreCase("limit") && command[7].equalsIgnoreCase("speed")
					&& command[9].equalsIgnoreCase("acceleration")) {
					if (isIdValid(command[3]) && isDoublePositive(command[6]) >= 0 && isDoublePositive(command[8]) > 0 && isDoublePositive(command[10]) >= 0) {
						return true;	
					} else {
						throw new RuntimeException("Attempt to use an invalid value");
					}						
				}
			}
		}
		
		
		
		if (command.length == 8) {//engine
			if (command[1].equalsIgnoreCase("engine") && command[3].equalsIgnoreCase("with") && command[4].equalsIgnoreCase("speed") && command[6].equalsIgnoreCase("acceleration")) {
				if (isIdValid(command[2]) && isDoublePositive(command[5]) > 0 && isDoublePositive(command[7]) >= 0) {
							return true;	
				} else {
					throw new RuntimeException("Attempt to use an invalid value");
				}
			}
		}
		
		
		
		if (command.length == 9) {//Nose gear, main gear
			if (command[1].equalsIgnoreCase("nose") || command[1].equalsIgnoreCase("main")) {
				if (command[2].equalsIgnoreCase("gear") && command[4].equalsIgnoreCase("with") && command[5].equalsIgnoreCase("speed") && command[7].equalsIgnoreCase("acceleration")) {
					if (isIdValid(command[3]) && isDoublePositive(command[6]) > 0 && isDoublePositive(command[8]) >= 0) {
						return true;
					} else {
						throw new RuntimeException("Attempt to use an invalid value");
					}
				}
			}
		}
		
		
		return false; //not a valid command
	}
	
	
	//private helper method to check whether a numerical value entered in a command is positive, and then return that value. This method is called in validateCommand()
	private double isDoublePositive(String number) {
		
		if (number.charAt(0) == '.') {
    		return -1;
    	}
		
	    try {
	    	
	        double num = Double.parseDouble(number);
	        return num;
	    }
	    catch(Exception e) {
	        return -1;
	    }
	}
	
	//private helper method to check whether an entered in ID is valid (i.e. is not null, doesn't contain any '.', and does not start with a number. Id must be like a valid java string variable
	private boolean isIdValid(String id) {

		//make sure the id does not contain a '.' or is not null
		if (id.contains(".") || id == null) {
            return false;
        } 
		
		//check if the first character is a number, if it is, return false. If it isn't, it will catch the exception and return true
        try { 
            
            String firstChar = "" + id.charAt(0);
            int num = Integer.parseInt(firstChar);
            
            return false;
        }
        catch(Exception e) {
            return true;
        }
        
        
    }
	
}
