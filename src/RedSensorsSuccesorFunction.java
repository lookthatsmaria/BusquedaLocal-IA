import java.util.*;
import IA.Red.*;

import aima.search.framework.SuccessorFunction;
import aima.search.framework.Successor;

public class RedSensorsSuccesorFunction implements SuccessorFunction{

    public List getHillClimbSuccessors(Object state){

        return (List)state;

    }

    public List getAnealingSuccessor(Object state){

        return (List)state;

    }

}
