import java.util.*;
import IA.Red.*;

public class RedSensorsState {
    private final ArrayList<IA.Red.Centro> datacenters;
    private final ArrayList<IA.Red.Sensor> sensors;
    private int [][] map;
    private final double [][] dist;
    private final int n, m;

    public RedSensorsState(int ncent, int nsens, int seedc, int seeds){
        datacenters = new CentrosDatos(ncent, seedc);
        sensors = new Sensores(nsens, seeds);
        m = nsens;
        n = ncent+nsens;
        map = new int[n][m];
        dist = new double[n][m];
        for(int i = 0; i < n; ++i){
            for(int j = 0; j < m; ++j){
                map[i][j] = 0;
                if (i == j) dist[i][j] = 0;
                else if (i < ncent) dist[i][j]=distance(datacenters.get(i).getCoordX(), datacenters.get(i).getCoordY(), sensors.get(j).getCoordX(), sensors.get(j).getCoordY());
                else dist[i][j]=distance(sensors.get(i-ncent).getCoordX(), sensors.get(i-ncent).getCoordY(), sensors.get(j).getCoordX(), sensors.get(j).getCoordY());
            }
        }
        System.out.println(datacenters.size());

    }

    public double distance(int X1, int Y1, int X2, int Y2){
        return Math.sqrt(((double) X1 - (double) X2)*((double) X1 - (double) X2)+((double) Y1 - (double) Y2)*((double) Y1 - (double) Y2));
    }

    public double getDistance(int i, int j){
        if (i < 0 || i > n || j < 0 || j > m) return -1;
        else return dist[i][j];
    }


}