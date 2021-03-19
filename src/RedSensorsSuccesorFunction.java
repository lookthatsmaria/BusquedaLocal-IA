import java.util.*;
import IA.Red.*;

import aima.search.framework.SuccessorFunction;
import aima.search.framework.Successor;

public class RedSensorsSuccesorFunction implements SuccessorFunction{

    public List getSuccessors(Object state){

        return (List)state;

    }

}
