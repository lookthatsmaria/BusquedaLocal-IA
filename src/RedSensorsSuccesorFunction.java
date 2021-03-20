import java.util.*;

import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;

public class RedSensorsSuccesorFunction implements SuccessorFunction{

    public List getSuccessors(Object aState){
        ArrayList retVal= new ArrayList();
        RedSensorsState state=(RedSensorsState) aState;
        for(int i = state.getNcent(); i < state.getnElements(); ++i) {
            double [][] adjacencyMatrix = state.getAdjacencyMatrix();
            int destination = (int) adjacencyMatrix[i][i];
            double throughput = adjacencyMatrix[i][destination];
            for (int j = 0; j < state.getnElements(); ++j) {
                if(j != destination){
                    RedSensorsState newState = new RedSensorsState(state.getNcent(), state.getnElements() - state.getNcent(), state.getDist(), adjacencyMatrix, state.getDatacenters(), state.getSensors());
                    int limit;
                    if (j < newState.getNcent()) limit = 25;
                    else limit = 3;
                    if (newState.canConnect(j,limit)) {
                        newState.newConnection(i,destination,j,throughput);
                        retVal.add(new Successor("NEW CONNECTION", newState));
                    }
                }
            }
        }
        return retVal;
    }
}
