package sbw.project.cli.parser;

import sbw.architecture.datatype.Identifier;
import sbw.architecture.datatype.Percent;
import sbw.architecture.datatype.AileronSlaveMix;
import sbw.project.cli.action.ActionStructural;
import java.util.ArrayList;


/**
 * 
 * @author John Petrovich
 * @version 5 June 2019
 *
 */

public class StructuralParser {

    ActionStructural as;

    public StructuralParser(ActionStructural as) {
        this.as = as;
    }

    public void parseCommand(String[] array) throws ParseException {
        if(array[1].equalsIgnoreCase("rudder")) {
            String id1 = array[3];
            Identifier cID = new Identifier(id1);

            String id2 = array[6];
            Identifier rID = new Identifier(id2);

            this.as.doDeclareRudderController(cID, rID);
        }

        else if(array[1].equalsIgnoreCase("elevator")) {
        	String id1 = array[3];
            Identifier cID = new Identifier(id1);

            String id2 = array[6];
            Identifier leftID = new Identifier(id2);
            
            String id3 = array[7];
            Identifier rightID = new Identifier(id3);

            this.as.doDeclareElevatorController(cID, leftID, rightID);
        }
        
        else if(array[1].equalsIgnoreCase("aileron")) {
        	String id1 = array[3];
        	Identifier cID = new Identifier(id1);
        	
        	ArrayList<Identifier> ailerons = new ArrayList<Identifier>();
        	int ix = 6;
        	while(!array[ix].equalsIgnoreCase("primary")) {
        		String idn = array[ix];
        		Identifier aID = new Identifier(idn);
        		ailerons.add(aID);
        		
        		ix++;
        	}
        	if(ailerons.size() % 2 != 0)
        		throw new ParseException("Invalid Command, odd number of ailerons");
        	
        	ix++; // "PRIMARY" --> <idx>
        	
        	String idx = array[ix];
        	Identifier primary = new Identifier(idx);
        	ix++; // <idx> --> "SLAVE"
        	
        	ArrayList<AileronSlaveMix> mixes = new ArrayList<AileronSlaveMix>();
        	while(ix < array.length) {
        		ix++; // "SLAVE" -> <idslave>
        		String idSlave = array[ix];
        		Identifier sID = new Identifier(idSlave);
        		
        		ix += 2; // <idslave> --> "TO" --> <idmaster>
        		String idMaster = array[ix];
        		Identifier mID = new Identifier(idMaster);
        		
        		ix += 2; // <idmaster> --> "BY" --> <percent>
        		Double per = Double.parseDouble(array[ix]);
        		Percent percent = new Percent(per);
        		
        		AileronSlaveMix asm = new AileronSlaveMix(mID, sID, percent);
        		mixes.add(asm);
        		
        		ix += 2; // <percent> --> "PERCENT" --> "SLAVE" (or null index)
        	}
        	
        	this.as.doDeclareAileronController(cID, ailerons, primary, mixes);
        }
        
        else if(array[1].equalsIgnoreCase("flap")) {
        	String id = array[3];
        	Identifier cID = new Identifier(id);
        	
        	ArrayList<Identifier> flaps = new ArrayList<Identifier>();
        	for(int ix = 6; ix < array.length; ix++) {
        		String idn = array[ix];
        		Identifier fID = new Identifier(idn);
        		flaps.add(fID);
        	}
        	
        	if(flaps.size() % 2 == 0)
        		this.as.doDeclareFlapController(cID, flaps);
        	else
        		throw new ParseException("Invalid Command, odd number of flaps");
        }
        
        else if(array[1].equalsIgnoreCase("engine")) {
        	String id = array[3];
        	Identifier cID = new Identifier(id);
        	
        	ArrayList<Identifier> engines = new ArrayList<Identifier>();
        	for(int ix = 6; ix < array.length; ix++) {
        		String idn = array[ix];
        		Identifier eID = new Identifier(idn);
        		engines.add(eID);
        	}
        	
        	this.as.doDeclareEngineController(cID, engines);
        }
        
        else if(array[1].equalsIgnoreCase("gear")) {
        	String id1 = array[3];
        	Identifier cID = new Identifier(id1);
        	
        	String id2 = array[7];
        	Identifier nID = new Identifier(id2);
        	
        	String id3 = array[9];
        	Identifier leftID = new Identifier(id3);
        	
        	String id4 = array[10];
        	Identifier rightID = new Identifier(id4);
        	
        	this.as.doDeclareGearController(cID, nID, leftID, rightID);
        }
        
        else if(array[1].equalsIgnoreCase("bus")) {
        	String id1 = array[2];
        	Identifier bID = new Identifier(id1);
        	
        	ArrayList<Identifier> controllers = new ArrayList<Identifier>();
        	for(int ix = 5; ix < array.length; ix++) {
        		String idn = array[ix];
        		Identifier cID = new Identifier(idn);
        		controllers.add(cID);
        	}
        	
        	this.as.doDeclareBus(bID, controllers);
        }
    }

    public boolean validateCommand(String[] cmd) throws ParseException {
    	if(cmd.length == 7 && cmd[1].equalsIgnoreCase("rudder") && cmd[2].equalsIgnoreCase("controller") && cmd[4].equalsIgnoreCase("with") && cmd[5].equalsIgnoreCase("rudder")) {
    		if(isIdValid(cmd[3]))
    			return true;
    		else
    			throw new ParseException("Invalid rudder controller ID");
    	}
    	if(cmd.length == 8 && cmd[1].equalsIgnoreCase("elevator") && cmd[2].equalsIgnoreCase("controller") && cmd[4].equalsIgnoreCase("with") && cmd[5].equalsIgnoreCase("elevators")) {
    		if(isIdValid(cmd[3]))
    			return true;
    		else
    			throw new ParseException("Invalid elevator controller ID");
    	}
    	if(cmd.length >= 10 && cmd[1].equalsIgnoreCase("aileron") && cmd[2].equalsIgnoreCase("controller") && cmd[4].equalsIgnoreCase("with") && cmd[5].equalsIgnoreCase("ailerons")) {
            int ix= 6;
            while(!cmd[ix].equalsIgnoreCase("primary")) {
            	ix++;
            }
            ix+=2; 
            int numOfSlaveCmds = cmd.length - ix;
            if (numOfSlaveCmds == 0 && isIdValid(cmd[3])) {
            	return true;
            }
            
            if (numOfSlaveCmds % 7 != 0) {
            	return false;
            } else {
            	for (int x = ix; x < cmd.length; x+=7) {
            		if(!cmd[x].equalsIgnoreCase("slave") || !cmd[x+2].equalsIgnoreCase("to") || !cmd[x+4].equalsIgnoreCase("by") || !cmd[x+6].equalsIgnoreCase("percent")) {
                        return false;
            		}
            	}
            }
            
            if(isIdValid(cmd[3]))
                return true;
            else
                throw new ParseException("Invalid aileron controller ID");
        }
    	if(cmd.length >= 7 && cmd[1].equalsIgnoreCase("flap") && cmd[2].equalsIgnoreCase("controller") && cmd[4].equalsIgnoreCase("with") && cmd[5].equalsIgnoreCase("flaps")) {
    		if(isIdValid(cmd[3]))
    			return true;
    		else
    			throw new ParseException("Invalid flap controller ID");
    	}
    	if(cmd.length >= 7 && cmd[1].equalsIgnoreCase("engine") && cmd[2].equalsIgnoreCase("controller") && cmd[4].equalsIgnoreCase("with") && (cmd[5].equalsIgnoreCase("engine") || cmd[5].equalsIgnoreCase("engines"))) {
    		if(isIdValid(cmd[3]))
    			return true;
    		else
    			throw new ParseException("Invalid engine controller ID");
    	}
    	if(cmd.length == 11 && cmd[1].equalsIgnoreCase("gear") && cmd[2].equalsIgnoreCase("controller") && cmd[4].equalsIgnoreCase("with") && cmd[5].equalsIgnoreCase("gear") && cmd[6].equalsIgnoreCase("nose") && cmd[8].equalsIgnoreCase("main")) {
    		if(isIdValid(cmd[3]))
    			return true;
    		else
    			throw new ParseException("Invalid gear controller ID");
    	}
    	if(cmd.length >= 6 && cmd[1].equalsIgnoreCase("bus") && cmd[3].equalsIgnoreCase("with") && (cmd[4].equalsIgnoreCase("controller") || cmd[4].equalsIgnoreCase("controllers"))) {
    		if(isIdValid(cmd[2]))
    			return true;
    		else
    			throw new ParseException("Invalid bus controller ID");
    	}
    	return false;
    }
    
    private boolean isIdValid(String id) {
    	if(id.contains(".") || id == null)
    		return false;
    	
    	if(id.charAt(0) >= '0' && id.charAt(0) <= '9')
    		return false;
    	
    	return true;
    }
}

