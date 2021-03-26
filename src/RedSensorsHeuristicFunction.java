import aima.search.framework.HeuristicFunction;

public class RedSensorsHeuristicFunction implements HeuristicFunction {

    public boolean equals(Object obj) {
        boolean retValue;

        retValue = super.equals(obj);
        return retValue;
    }

    public double getHeuristicValue(Object n){
        RedSensorsState state=(RedSensorsState)n;
        double totalCost = 0;
        double totalDataVolume1 = 0;
        double dataLost = 0;
        for(int src = 0; src < state.getNsens(); ++src){
            int dst = state.connexions(src);
            totalCost += state.getCost_v2(src+ state.getNcent(),dst);
        }
        for(int cd = 0; cd < state.getNcent(); ++cd) {
            totalDataVolume1 += state.dataDC(cd);
        }
        dataLost = state.getMaxData() - totalDataVolume1;
        return totalCost+dataLost*totalCost;
    }
}
