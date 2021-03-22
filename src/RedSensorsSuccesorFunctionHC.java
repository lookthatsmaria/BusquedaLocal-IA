import java.util.*;

import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;

public class RedSensorsSuccesorFunctionHC implements SuccessorFunction{

    public List getSuccessors(Object aState){
        ArrayList retVal= new ArrayList();
        RedSensorsState state=(RedSensorsState) aState;
        for(int node = state.getNcent(); node < state.getnElements(); ++node) {
            double [][] adjacencyMatrix = state.getAdjacencyMatrix();
            int oldConnection = (int) adjacencyMatrix[node][node];
            double throughput = adjacencyMatrix[node][oldConnection];
            for (int newConnection = 0; newConnection < state.getnElements(); ++newConnection) {
                if(newConnection != node && newConnection != oldConnection){
                    RedSensorsState newState = new RedSensorsState(state.getNcent(), state.getnElements() - state.getNcent(), state.getDist(), adjacencyMatrix, state.getDatacenters(), state.getSensors());
                    int limit = newConnection < newState.getNcent() ? 25:3;
                    if (newState.canConnect(newConnection,limit) && newState.findLoop(node,newConnection)) {
                        newState.newConnection(node,oldConnection,newConnection,throughput);
                        retVal.add(new Successor("NEW CONNECTION", newState));
                    }
                }
            }
        }
        return retVal;
    }
}
