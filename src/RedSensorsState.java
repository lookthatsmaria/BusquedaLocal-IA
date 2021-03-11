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
        n = ncent;
        m = ncent+nsens;
        map = new int[m][m];
        dist = new double[m][m];
        int X1, Y1, X2,  Y2;
        for(int i = 0; i < m; ++i){
            System.out.print("fila: "+i+" ");
            if (i < ncent) {X1 = datacenters.get(i).getCoordX(); Y1 = datacenters.get(i).getCoordY(); }
            else { X1 = sensors.get(i-ncent).getCoordX(); Y1 = sensors.get(i-ncent).getCoordY(); }
            for(int j = 0; j < m; ++j){
                if (i == j || (i < ncent && j < ncent)) {dist[i][j] = 0; map[i][j]=0;}
                else {
                    if (i < ncent) {
                        map[i][j] = 0;
                        dist[i][j] = 0;
                    }
                    else {
                        if (j < ncent) {
                            X2 = datacenters.get(j).getCoordX();
                            Y2 = datacenters.get(j).getCoordY();
                            if ((i < (25+ncent)+j*25) && (i >= (25+ncent)+(j-1)*25)) map[i][j] = 1;
                            else map[i][j] = 0;
                        }
                        else {
                            X2 = sensors.get(j - ncent).getCoordX();
                            Y2 = sensors.get(j - ncent).getCoordY();
                            if (i > ((25*ncent)+ncent) && connexions_sensor(j) <= 3) map[i][j] = 1;
                            else map[i][j] = 0;
                        }
                        dist[i][j] = distance(X1, Y1, X2, Y2);
                    }
                }

                System.out.print(map[i][j]);
            }
            System.out.println("");
        }

    }

    public int connexions_sensor(int j){
        int sum = 0;
        for(int i = m-n; i < m; ++i) sum+=map[i][j];
        return sum;
    }

    public double distance(int X1, int Y1, int X2, int Y2){
        return Math.sqrt(((double) X1 - (double) X2)*((double) X1 - (double) X2)+((double) Y1 - (double) Y2)*((double) Y1 - (double) Y2));
    }

    public double getDistance(int i, int j){
        if (i < 0 || i > n || j < 0 || j > m) return -1;
        else return dist[i][j];
    }


}