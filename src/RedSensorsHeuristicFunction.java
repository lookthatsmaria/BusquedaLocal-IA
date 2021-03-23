import aima.search.framework.HeuristicFunction;

public class RedSensorsHeuristicFunction implements HeuristicFunction {

    public boolean equals(Object obj) {
        boolean retValue;

        retValue = super.equals(obj);
        return retValue;
    }

    // heuristic has to take into account the distances and the data lost
    public double getHeuristicValue(Object n){
        RedSensorsState state=(RedSensorsState)n;
        double totalCost = 0;
        double totalDataVolume = 0;
        double dataVolume;
        double dataLost = 0;
        double capacity;
        for(int src = state.getNcent(); src < state.getnElements(); ++src){
            int dst = (int)state.adjacencyMatrix(src,src);
            dataVolume = state.adjacencyMatrix(src,dst);
            if(dst >= state.getNcent()) capacity = state.getCapacityOfSensor(dst- state.getNcent());
            else capacity = 150;
            double difference = dataVolume - capacity;
            if (difference > 0){
                totalDataVolume += capacity;
                dataLost += difference;
            }
            else totalDataVolume += dataVolume;
            totalCost += state.getCost(src,dst);
        }

        return (totalCost+(-Math.pow(totalDataVolume,2))+Math.pow(dataLost,3));
    }
}
