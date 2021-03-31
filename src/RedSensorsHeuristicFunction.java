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
        double totalDataVolume = 0;
        double dataLost;
        int nsens = state.getNsens();
        int ncent = state.getNcent();
        for(int src = 0; src < nsens; ++src){
            int dst = state.connexions(src);
            totalCost += state.getCost(src+ncent,dst);
            if (src < ncent)
                totalDataVolume += state.dataDC(src);
        }
        dataLost = state.getMaxData() - totalDataVolume;
        //System.out.println(state.getMaxData());
        return totalCost+dataLost*totalCost;
    }
}
