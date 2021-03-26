import java.util.*;

import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;

public class RedSensorsSuccesorFunctionHC implements SuccessorFunction{

    public List getSuccessors(Object aState){
        ArrayList retVal= new ArrayList();
        RedSensorsState state=(RedSensorsState) aState;
        RedSensorsHeuristicFunction RDHF = new RedSensorsHeuristicFunction();
        for(int node = 0; node < state.getNsens(); ++node) {
            int[] connexions = state.getConnexions();
            double [] thropt= state.getThropt();
            int oldConnection = connexions[node];
            double throughput = thropt[node];
            for (int newConnection = 0; newConnection < state.getnElements(); ++newConnection) {
                if(newConnection != node+ (state.getNcent()) && newConnection != oldConnection){
                    RedSensorsState newState = new RedSensorsState(state.getNcent(), state.getNsens(), state.getDist(), state.getConnexions(),state.getThropt(), state.getDataDC(), state.getDatacenters(), state.getSensors(),state.getMaxData());
                    int limit = newConnection < newState.getNcent() ? 25:3;
                    if (newState.canConnect_v2(newConnection,limit) && newState.findLoop_v2(node+ state.getNcent(),newConnection)) {
                        newState.newConnection_v2(node,oldConnection,newConnection,throughput);
                        double v = RDHF.getHeuristicValue(newState);
                        String S = "NEW CONNECTION  Coste("+v+") --->"+ newState.toString();
                        retVal.add(new Successor(S, newState));
                    }
                }
            }
        }
        return retVal;
    }
}

