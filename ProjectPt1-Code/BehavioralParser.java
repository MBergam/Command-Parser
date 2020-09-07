package sbw.project.cli.parser;

import sbw.project.cli.action.ActionBehavioral;
import sbw.project.cli.action.command.behavioral.*;


import sbw.architecture.datatype.*;
import sbw.architecture.datatype.Position.E_Position;

/**
 * 
 * @author Mark Dietzler & Max Bergam
 * @version 4 June 2019
 *
 */

public class BehavioralParser {
	
	private ActionBehavioral mActBehav;
	

	public BehavioralParser(ActionBehavioral mActBehav) {
		setmActBehav(mActBehav);
	}

	public ActionBehavioral getmActBehav() {
		return mActBehav;
	}

	public void setmActBehav(ActionBehavioral mActBehav) {
		this.mActBehav = mActBehav;
	}
	
	
	/**
	 * Initial check of command array, handles halt and gear, then sends
	 * everything else off for further processing
	 * @param array String array of the command
	 * @throws ParseException
	 */
	public void parseCommand(String[] array) throws ParseException {
		
		if(array[0].equalsIgnoreCase("halt")) {
			
			halt(array);
			
		} else if(array[2].equalsIgnoreCase("gear")){
			
			moveGear(array);
			
		} else {
			
			checkLength(array);
			
		}
		
	}
	
	/**
	 * Sorts command lengths between five, six, and seven. Length five
	 * contains brakes, flap, and all engine power. Length six contains
	 * rudder, elevator, and aileron 
	 * @param command String array of the command
	 */
	private void checkLength(String[] command) throws ParseException{
		
		if(command.length < 5 || command.length > 7) {
			
			throw new ParseException("command is not the right length.");
			
		}
		
		//sort commands
		if(command.length == 6) {
			
			lengthSix(command);
			
		} else if(command.length == 5){
			
			lengthFive(command);
			
		} else {
			
			lengthSeven(command);
			
		}
	}
	
	/**
	 * Commands of length five include brake, flap, and all engines to power X
	 * @param command String array of the command
	 */
	private void lengthFive(String[] command) throws ParseException {
		
		if(!command[3].equalsIgnoreCase("brake") && !command[3].equalsIgnoreCase("flap") && !command[3].equalsIgnoreCase("power")) {
			
			throw new ParseException("Command doesn't deal with the speed brakes, flaps, or engine thrust settings");
			
		}
		
		//sort commands
		if(command[3].equalsIgnoreCase("brake")) {
			
			moveBrake(command);
			
		} else if (command[3].equalsIgnoreCase("flap")) {
			
			moveFlap(command);
			
		} else {
			
			setEngines(command);
			
		}
		
	}
	
	/**
	 * Commands of length six include rudder, elevator, aileron commands
	 * @param command String array of the command
	 */
	private void lengthSix(String[] command) throws ParseException {
		
		if(!command[3].equalsIgnoreCase("rudder") && !command[3].equalsIgnoreCase("elevator") && !command[3].equalsIgnoreCase("ailerons")) {
			
			throw new ParseException("Command doesn't deal with the rudder, elevators, or ailerons");
			
		}
		
		//sort commands
		if(command[3].equalsIgnoreCase("rudder")) {
			
			moveRudder(command);
			
		} else if (command[3].equalsIgnoreCase("elevator")) {
			
			moveElevator(command);
			
		} else {
			
			moveAilerons(command);
			
		}
	}
	
	/**
	 * Only length seven command. Sets specific engine power to all controlled engines
	 * via the specified engine controller.
	 * @param command String array of the command
	 * @throws ParseException If given angle is negative or not parseable by Double.parse()
	 */
	private void lengthSeven(String[] command) throws ParseException {
		
		Identifier controller = new Identifier(command[1]);
		Identifier engine = new Identifier(command[6]);
		
		double thrust = Double.parseDouble(command[4]);		
		Power enginePower = new Power(thrust);
		
		CommandDoSetEnginePowerSingle engineCommand = new CommandDoSetEnginePowerSingle(controller, enginePower, engine);
		
		mActBehav.submitCommand(engineCommand);
	}
	
	/**
	 * Moves gear to extended/retracted
	 * @param gearCommand String array of the command
	 */
	private void moveGear(String[] gearCommand) {
		boolean isDown = false; //true if down, false if up
		if(gearCommand[3].equalsIgnoreCase("down")) {
			isDown = true;
		}
		
		Identifier gearID = new Identifier(gearCommand[1]);
		CommandDoSelectGear gearMoveCommand = new CommandDoSelectGear(gearID, isDown);
		
		mActBehav.submitCommand(gearMoveCommand);
	}
	
	/**
	 * Halts the given command
	 * @param haltCommand String array of the command
	 */
	private void halt(String[] haltCommand) {
		
		Identifier haltID = new Identifier(haltCommand[1]);
		CommandDoHalt stopMe = new CommandDoHalt(haltID);
		
		mActBehav.submitCommand(stopMe);
	}
	
	//begin length 6 group
	/**
	 * deflects rudder to the given value
	 * @param rudderCommand String array of the command
	 * @throws ParseException If given angle is negative or not parseable by Double.parse()
	 */
	private void moveRudder(String[] rudderCommand) throws ParseException {
		boolean isRight = false; //true if right, false if left
		if(rudderCommand[5].equalsIgnoreCase("right")) {
			isRight = true;
		}
		
		Identifier rudderID = new Identifier(rudderCommand[1]);
		double angle = Double.parseDouble(rudderCommand[4]);
		Angle RudderAngle = new Angle(angle);
		
		CommandDoDeflectRudder rudderDeflectCommand = new CommandDoDeflectRudder(rudderID, RudderAngle, isRight);
		mActBehav.submitCommand(rudderDeflectCommand);
	}
	
	/**
	 * Deflects elevators to the given value
	 * @param elevatorCommand String array of the command
	 * @throws ParseException If given angle is negative or not parseable by Double.parse()
	 */
	private void moveElevator(String[] elevatorCommand) throws ParseException {
		boolean isDown = false; //true if down, false if up
		if(elevatorCommand[5].equalsIgnoreCase("down")) {
			isDown = true;
		}
		
		Identifier elevatorID = new Identifier(elevatorCommand[1]);
		double angle = Double.parseDouble(elevatorCommand[4]);
		Angle elevatorDeflect = new Angle(angle);
		
		CommandDoDeflectElevator elevatorDeflectCommand = new CommandDoDeflectElevator(elevatorID, elevatorDeflect, isDown);
		mActBehav.submitCommand(elevatorDeflectCommand);
	}
	
	/**
	 * Deflects ailerons to the given value
	 * @param aileronCommand String array of the command
	 * @throws ParseException If given angle is negative or not parseable by Double.parse()
	 */
	private void moveAilerons(String[] aileronCommand) throws ParseException {
		
		boolean isDown = false; //true if down, false if up
		if(aileronCommand[5].equalsIgnoreCase("down")) {
			isDown = true;
		}
		Identifier aileronsID = new Identifier(aileronCommand[1]);
		double angle = Double.parseDouble(aileronCommand[4]);
		Angle aileronDeflect = new Angle(angle);
		
		CommandDoDeflectAilerons aileronDeflectCommand = new CommandDoDeflectAilerons(aileronsID, aileronDeflect, isDown);
		mActBehav.submitCommand(aileronDeflectCommand);
		
	}
	//end length six group
	
	//begin length five group
	/**
	 * Extends speed brakes
	 * @param speedBrakeCommand String array of the command
	 */
	private void moveBrake(String[] speedBrakeCommand) {
		
		boolean isDeployed = true;
		if (speedBrakeCommand[4].equalsIgnoreCase("off")) {
			isDeployed = false;
		}
		
		Identifier brakeID = new Identifier(speedBrakeCommand[1]);
		CommandDoDeploySpeedBrake speedBrakeDeployCommand = new CommandDoDeploySpeedBrake(brakeID, isDeployed);
		mActBehav.submitCommand(speedBrakeDeployCommand);
	}
	
	/**
	 * Move flaps to the commanded position
	 * @param flapCommand String array of the command
	 */
	private void moveFlap(String[] flapCommand) {
		
		Identifier flapID = new Identifier(flapCommand[1]);
		
		String p = flapCommand[4];
		if (p.equalsIgnoreCase("up")) {
			p = "UP";
		} else if (p.equalsIgnoreCase("1")) {
			p = "ONE";
		} else if (p.equalsIgnoreCase("2")) {
			p = "TWO";
		} else if (p.equalsIgnoreCase("3")) {
			p = "THREE";
		} else if (p.equalsIgnoreCase("4")) {
			p = "FOUR";
		}
		Position.E_Position pos;
		try {
			pos = E_Position.valueOf(p);
		}
		catch (Exception e) {
			throw new RuntimeException("invalid flap position");
		}
		
		Position position = new Position(pos);
		
		CommandDoSetFlaps flapPositionCommand = new CommandDoSetFlaps(flapID, position);
		mActBehav.submitCommand(flapPositionCommand);
	}
	
	/**
	 * Sets given engine power to all engines via the given engine controller
	 * @param powerCommand String array of the command
	 */
	private void setEngines(String[] powerCommand) {
		
		Identifier enginesControllerID = new Identifier(powerCommand[1]);
		double thrust = Double.parseDouble(powerCommand[4]);
		Power power = new Power(thrust);
		
		CommandDoSetEnginePowerAll enginePowerCommand = new CommandDoSetEnginePowerAll(enginesControllerID, power);
		mActBehav.submitCommand(enginePowerCommand);
		
	}

	/**
	 * Validates commands for length and non-variable words
	 * @param command The string array representing the command
	 * @return True if the command matches a pattern, false if it does not
	 */
	//validates that the command is in a correct Behavioral format with acceptable values. Returns true if it is, false if it is not.
		public boolean validateCommand(String[] command) throws ParseException {
			
			
			//rudder
			if (command.length == 6) {
				if (command[2].equalsIgnoreCase("deflect")) {
					if (command[3].equalsIgnoreCase("rudder")) {
						if (command[5].equalsIgnoreCase("left") || command[5].equalsIgnoreCase("right")) {
							if (isDoublePositive(command[4]) >= 0) {
								return true;
							} else {
								throw new RuntimeException("Attempt to use an invalid value");
							}
						}
					}
				}
			}
			
			
			// elevator, ailerons
			if (command.length == 6) {
				if (command[2].equalsIgnoreCase("deflect")) {
					if (command[3].equalsIgnoreCase("elevator") || command[3].equalsIgnoreCase("ailerons")) {
						if (command[5].equalsIgnoreCase("up") || command[5].equalsIgnoreCase("down")) {
							if (isDoublePositive(command[4]) >= 0) {
								return true;
							} else {
								throw new RuntimeException("Attempt to use an invalid value");
							}
						}
					}
				}
			}
			
			
			if (command.length == 5) {//speed brake
				if (command[2].equalsIgnoreCase("speed") && command[3].equalsIgnoreCase("brake") && (command[4].equalsIgnoreCase("on") || command[4].equalsIgnoreCase("off"))) {
					return true;
				}
			} 
			
			if (command.length == 5) {//flap
				if (command[2].equalsIgnoreCase("deflect") && command[3].equalsIgnoreCase("flap") && (command[4].equalsIgnoreCase("up") || command[4].equalsIgnoreCase("1") 
					|| command[4].equalsIgnoreCase("2") || command[4].equalsIgnoreCase("3") || command[4].equalsIgnoreCase("4"))) {
						return true;
				}
			} 
			
			
			if (command.length == 5) {//set power 1
				if (command[2].equalsIgnoreCase("set") && command[3].equalsIgnoreCase("power")) {
					if (isDoublePositive(command[4]) >= 0 && isDoublePositive(command[4]) <= 100) {
						return true;	
					} else {
						throw new RuntimeException("Attempt to use an invalid value");
					}							
				}
			}
			
			
			
			if (command.length == 7) {//set power 2
				if (command[2].equalsIgnoreCase("set") && command[3].equalsIgnoreCase("power") && command[5].equalsIgnoreCase("engine")) {
					if (isDoublePositive(command[4]) >= 0 && isDoublePositive(command[4]) <= 100) {
						return true;	
					} else {
						throw new RuntimeException("Attempt to use an invalid value");
					}
				}
			}
			
			
			
			if (command.length == 4) {//gear
				if (command[2].equalsIgnoreCase("gear") && (command[3].equalsIgnoreCase("up") || command[3].equalsIgnoreCase("down"))) {
					return true;
				}
			}
			
			if (command.length == 2  && command[0].equalsIgnoreCase("halt")) {//halt
				return true;
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
		

}//end class


