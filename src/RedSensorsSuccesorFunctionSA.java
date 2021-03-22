import java.util.*;
import java.lang.Math;
import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;

public class RedSensorsSuccesorFunctionSA implements SuccessorFunction{

    public List getSuccessors(Object aState) {
        ArrayList retVal = new ArrayList();
        RedSensorsState state = (RedSensorsState) aState;
        int min = state.getNcent();
        int max = state.getnElements();
        RedSensorsState newState;
        double[][] adjacencyMatrix = state.getAdjacencyMatrix();
        Random rand = new Random();
        int range = max - min + 1;
				int node , newConnection , oldConnection , limit ;
				do{
					node = rand.nextInt((range) + min);
					newConnection = rand.nextInt((range) + min);
					oldConnection = (int) adjacencyMatrix[node][node];
					limit = newConnection < min ? 25 : 3;
        }while (newConnection != node && newConnection != oldConnection &&
                (!state.findLoop(node, newConnection) || !state.canConnect(newConnection, limit)));
        newState = new RedSensorsState(
                state.getNcent(),
                state.getnElements() - state.getNcent(),
                state.getDist(),
                adjacencyMatrix,
                state.getDatacenters(),
                state.getSensors());
        double throughput = adjacencyMatrix[node][oldConnection];
        newState.newConnection(node, oldConnection, newConnection, throughput);
        retVal.add(new Successor("NEW CONNECTION", newState));
        return retVal;
    }

}
