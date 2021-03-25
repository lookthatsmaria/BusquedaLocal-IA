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
            if (difference > 0){
                totalDataVolume += capacity;
                dataLost += difference;
            }
            else totalDataVolume += dataVolume;
            totalCost += state.getCost_v2(src+ state.getNcent(),dst);
            //System.out.println("totalcost "+totalCost+" "+src+state.getNcent()+" "+dst+" "+ state.dist(src+state.getNcent(),dst));
        }

        for(int cd = 0; cd < state.getNcent(); ++cd) {
            totalDataVolume1 += state.dataDC(cd);
        }
//        for(int i = 0; i < state.getnElements();++i){
//            for(int j = 0; j < state.getnElements();++j) {
//                System.out.println(i+" "+j+" "+state.dist(i,j));
//            }
//        }
        //System.out.println(totalCost+(-Math.pow(totalDataVolume1,2))+Math.pow(2,dataLost));
//        System.out.println("totalcost "+totalCost);
//        System.out.println(totalDataVolume1*3000);
//        System.out.println(Math.pow(dataLost,3));
//        System.out.println (totalCost-(totalDataVolume1*3000)+Math.pow(dataLost,3));
        //700 25
        return (totalCost-(totalDataVolume1*700)+Math.pow(dataLost,3));
    }
}
