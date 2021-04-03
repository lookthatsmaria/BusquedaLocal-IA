import java.util.*;

import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;

public class RedSensorsSuccesorFunctionSA implements SuccessorFunction{

    public List getSuccessors(Object aState) {
        ArrayList retVal = new ArrayList();
        RedSensorsState state = (RedSensorsState) aState;
        int min = state.getNcent();
        int max = state.getnElements();
        int[] connexions = state.getConnexions();
        double [] thropt= state.getThroughput();
        RedSensorsHeuristicFunction RDHF = new RedSensorsHeuristicFunction();
        Random rand = new Random();
				int node , newConnection , oldConnection , limit ;
				do{
					node = rand.nextInt(max);
					newConnection = rand.nextInt(max);
					limit = newConnection < min ? 25 : 3;
                }while (node < min || newConnection == node || newConnection == connexions[node-min] ||
                !state.noCycle(node, newConnection) || !state.canConnect(newConnection, limit));
        oldConnection = connexions[node-min];
        RedSensorsState newState = new RedSensorsState(state.getNcent(), state.getNsens(), state.getDist(), state.getConnexions(),state.getThroughput(), state.getDataDC(), state.getDatacenters(), state.getSensors(),state.getMaxData());
        double throughput = thropt[node-min];
        newState.modifyConnection(node-min, oldConnection, newConnection, throughput);
        double v = RDHF.getHeuristicValue(newState);
        String S = "NEW CONNECTION  Coste("+v+") ---> "+ newState.toString();
        retVal.add(new Successor(S, newState));
        //System.out.println(S);
        return retVal;
    }

}
