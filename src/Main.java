import aima.search.framework.Problem;
import aima.search.framework.Search;
import aima.search.framework.SearchAgent;
import aima.search.informed.HillClimbingSearch;
import aima.search.informed.SimulatedAnnealingSearch;


import java.util.*;

//Sol inicial 1: es posible que se pierda informacion
//Sol inicial 2: se maximiza la información transmitida (no se pierde info, si es posible)
//Sol inicial 3: test para la funcion findLoop

public class Main {
    public static void main(String[] args) throws Exception {
        RedSensorsState red = new RedSensorsState(Integer.parseInt(args[0]), Integer.parseInt(args[1]), 1234, 4321, 2);
        //tests
        boolean canConnectResult = true;
        for (int i = red.getNcent(); i < red.getnElements(); ++i) {
            canConnectResult = canConnectResult && red.canConnect(i, 3);
        }
        if (canConnectResult) System.out.println(" se puede conectar en todos nodo");
        else System.out.println("no se puede conectar en todos los nodos");


        // Create the Problem object
        Problem p = new Problem(red,
                new RedSensorsSuccesorFunctionSA(),
                new RedSensorsGoalTest(),
                new RedSensorsHeuristicFunction());

        long start = System.currentTimeMillis();
        // Instantiate the search algorithm
        //Search alg = new HillClimbingSearch();
        Search alg = new SimulatedAnnealingSearch(100000000,1000,50,0.001);

        // Instantiate the SearchAgent object
        SearchAgent agent = new SearchAgent(p, alg);

        // We print the results of the search
        printActions(agent.getActions());
        printInstrumentation(agent.getInstrumentation());

        long finish = System.currentTimeMillis();
        System.out.println("elapsed time: "+(finish- start));


    }
    private static void printInstrumentation(Properties properties) {
        for (Object o : properties.keySet()) {
            String key = (String) o;
            String property = properties.getProperty(key);
            System.out.println(key + " : " + property);
        }

    }

    private static void printActions(List actions) {
        for (Object o : actions) {
            if (o instanceof String) {
                String action = (String) o;
                System.out.println(action);
            } else {
                System.out.println(o);
            }
        }
    }
}
