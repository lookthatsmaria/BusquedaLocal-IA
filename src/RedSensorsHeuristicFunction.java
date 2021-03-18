import java.util.*;
import IA.Red.*;

import aima.search.framework.HeuristicFunction;

public class RedSensorsHeuristicFunction implements HeuristicFunction {

    public double getHeuristicValue(Object n){

        return ((RedSensorsState) n).heuristic();
    }
}
