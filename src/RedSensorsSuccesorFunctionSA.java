import java.util.*;
import java.lang.Math;
import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;

public class RedSensorsSuccesorFunctionSA implements SuccessorFunction{

    public List getSuccessors(Object aState){
        ArrayList retVal= new ArrayList();
        RedSensorsState state=(RedSensorsState) aState;
				int min = state.getNcent();
				int max = state.getnElements();
				RedSensorsState newState;
        double [][] adjacencyMatrix = state.getAdjacencyMatrix();
				int node = (int)(Math.random()*(max-min+1)+min);
				int newConnection = (int)(Math.random()*(max-min+1)+min);
				int oldConnection = (int) adjacencyMatrix[node][node];
				int limit = newConnection < min? 25:3;
				while(!state.canConnect(newConnection,limit) || !state.findLoop(node,newConnection)){
					node = (int)(Math.random()*(max-min+1)+min);
					newConnection = (int)(Math.random()*(max-min+1)+min);
          oldConnection = (int) adjacencyMatrix[node][node];
				}
				newState = new RedSensorsState(
						state.getNcent(),
						state.getnElements() - state.getNcent(),
						state.getDist(),
						adjacencyMatrix,
						state.getDatacenters(),
						state.getSensors());
        double throughput = adjacencyMatrix[node][oldConnection];
				newState.newConnection(node,oldConnection,newConnection,throughput);
				retVal.add(new Successor("NEW CONNECTION", newState));
        return retVal;
    }
}
