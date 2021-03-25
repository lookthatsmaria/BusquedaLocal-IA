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
        double totalDataVolume1 = 0;
        double dataVolume;
        double dataLost = 0;
        double capacity;
        for(int src = 0; src < state.getNsens(); ++src){
            int dst = state.connexions(src);
            dataVolume = state.thropt(src);
            if(dst >= state.getNcent()) capacity = state.getCapacityOfSensor(dst- state.getNcent());
            else capacity = 150;
            double difference = dataVolume - capacity;
            if (difference > 0) dataLost += difference;
            totalCost += state.getCost_v2(src+ state.getNcent(),dst);
        }
        for(int cd = 0; cd < state.getNcent(); ++cd) {
            totalDataVolume1 += state.dataDC(cd);
        }
        return (totalCost+(-totalDataVolume1*70000000*state.getNsens())+Math.pow(dataLost,2));
    }
}
