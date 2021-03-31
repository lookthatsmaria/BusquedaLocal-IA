import java.util.*;
import IA.Red.*;

public class RedSensorsState {
    private final ArrayList<IA.Red.Centro> datacenters;
    private final ArrayList<IA.Red.Sensor> sensors;
    private int [] connexions;
    private double [] throughput;
    private double [] dataDC;
    private final double [][] dist;
    private final Integer ncent, nElements, nsens;
    private final double maxData;

    public RedSensorsState(int ncent, int nsens, int seedc, int seeds, int option){
        datacenters = new CentrosDatos(ncent, seedc);
        sensors = new Sensores(nsens, seeds);
        this.ncent = ncent;
        nElements = ncent+nsens;
        this.nsens = nsens;
        connexions = new int[nsens];
        for(int i = 0; i < nsens;++i) connexions[i] = -1;
        throughput = new double[nsens];
        dist = new double[nElements][nElements];
        dataDC = new double[ncent];
        maxData = calculate_maxData();
        if (option == 2) initialSolution2();
        else initialSolution1();
    }

    public RedSensorsState(int ncent, int nsens, double [][] dist, int []connexions, double[] throughput, double[] dataDC, ArrayList<IA.Red.Centro> datacenters, ArrayList<IA.Red.Sensor> sensors, double maxData){
        this.ncent = ncent;
        nElements = ncent+nsens;
        this.nsens = nsens;
        this.dist = dist;
        this.connexions = connexions;
        this.datacenters = datacenters;
        this.sensors = sensors;
        this.throughput = throughput;
        this.dataDC = dataDC;
        this.maxData = maxData;
    }

    public void initialSolution1(){
        int identificador = 0;
        double capture;
        int count = 1;
        for(int sensor = 0; sensor < nsens; ++sensor){
            capture = sensors.get(sensor).getCapacidad();
            throughput[sensor] = capture;
            if (identificador < ncent){
                dataDC[identificador] += capture;
                connexions[sensor] =  identificador;
                if ((sensor+1)%25==0) ++identificador;
            }
            else{
                connexions[sensor] = identificador;
                propagateThroughput(throughput[identificador-ncent], sensors.get(identificador-ncent).getCapacidad()*3, sensors.get(sensor).getCapacidad(), connexions[identificador-ncent], identificador);
                if (count%3==0) ++identificador;
                ++count;
            }

        }
        print_map();
        initializeDist();
    }

    public void initialSolution2(){
        int identificador;
        double capture;
        for(int sensor = 0; sensor < nsens; ++sensor) {
            identificador = 0;
            capture = sensors.get(sensor).getCapacidad();
            boolean connected = false;
            while (!connected) {
                if (identificador < ncent) {
                    if (canConnect(identificador, 25) && ((dataDC[identificador] + capture) <= (150))){
                        connexions[sensor] = identificador;
                        throughput[sensor] = capture;
                        dataDC[identificador] += capture;
                        connected = true;
                    }
                }
                else {
                    if (canConnect(identificador, 3)
                            && ((throughput[identificador-ncent] + capture) <= (3 * sensors.get(identificador-ncent).getCapacidad()))){
                        connexions[sensor] = identificador;
                        propagateThroughput(throughput[identificador-ncent], sensors.get(identificador-ncent).getCapacidad()*3, sensors.get(sensor).getCapacidad(), connexions[identificador-ncent], identificador);
                        throughput[sensor] = capture;
                        connected = true;
                        identificador=ncent;
                    }
                }
                ++identificador;
            }
        }

        print_map();
        initializeDist();

    }

    public double calculate_maxData(){
        double sum = 0;
        for(int sens = 0; sens < nsens; ++sens) sum += sensors.get(sens).getCapacidad();
        return sum;
    }

    public void initializeDist(){
        int src_X, src_Y, dst_X,  dst_Y;
        for(int i = 0; i < nElements; ++i) {
            if (i < ncent) {
                src_X = datacenters.get(i).getCoordX();
                src_Y = datacenters.get(i).getCoordY();
            } else {
                src_X = sensors.get(i - ncent).getCoordX();
                src_Y = sensors.get(i - ncent).getCoordY();
            }
            for (int identificador = 0; identificador < nElements; ++identificador) {
                if (identificador < ncent) {
                    dst_X = datacenters.get(identificador).getCoordX();
                    dst_Y = datacenters.get(identificador).getCoordY();
                } else {
                    dst_X = sensors.get(identificador - ncent).getCoordX();
                    dst_Y = sensors.get(identificador - ncent).getCoordY();
                }
                dist[i][identificador] = distance(src_X, src_Y, dst_X, dst_Y);
            }
        }
    }

    public void propagateThroughput(double throughput, double capacity, double capture, int cd, int j){
        if (capture + throughput <= capacity) this.throughput[j-ncent] += capture;
        else {
            capture = capacity - this.throughput[j - ncent];
            this.throughput[j - ncent] = capacity;
        }
        if(capture > 0) {
            if (cd >= ncent) {
                int next_node = connexions[cd - ncent];
                propagateThroughput(this.throughput[cd - ncent],
                        sensors.get(cd-ncent).getCapacidad() * 3,
                        capture,
                        next_node,
                        cd);
            }
            else{
                if (dataDC[cd] + capture <= 150) dataDC[cd] += capture;
                else dataDC[cd] = 150;
            }
        }
    }

    public int[] getConnexions(){
        int [] copyConnections = new int[nsens];
        if (nsens >= 0) System.arraycopy(connexions, 0, copyConnections, 0, nsens);
        return copyConnections;

    }

    public double[] getThroughput(){
        double [] copyThopt = new double[nsens];
        if (nsens >= 0) System.arraycopy(throughput, 0, copyThopt, 0, nsens);
        return copyThopt;

    }

    public double[] getDataDC(){
        double [] copyData = new double[ncent];
        if (ncent >= 0) System.arraycopy(dataDC, 0, copyData, 0, ncent);
        return copyData;
    }

    public int getNsens(){
        return nsens;
    }

    public void propagate_disconnect(int i){
        if (i >= ncent){
            throughput[i-ncent] = dataVolume(i);
            propagate_disconnect(connexions[i-ncent]);
        }
        else dataDC[i] = dataVolume(i);
    }

    public boolean canConnect(int j, int limit){
        int count = 0;
           for(int i = 0; i < nsens; ++i){
               if(connexions[i] == j) ++count;
               if (count >= limit) return false;
           }
       return true;
    }

    public boolean noCycle(int i, int j){
        if (j < ncent) return true;
        int next = connexions[j-ncent];
        if (next < ncent) return true;
        if (next == i) return false;
        return noCycle(i,next);
    }

    public double distance(int X1, int Y1, int X2, int Y2){
        return ((double) X1 - (double) X2)*((double) X1 - (double) X2)+((double) Y1 - (double) Y2)*((double) Y1 - (double) Y2);
    }

    public double getCost(int i, int j){
        if (i < 0 || i >= nElements || j < 0 || j >= nElements) return -1;
        return dist[i][j] * throughput[i-ncent];
    }

    public void print_map(){
        System.out.println();
        for (int i = 0; i < nsens; ++i) System.out.print(connexions[i]+" ");
        System.out.println();
        for (int j = 0; j < nsens; ++j) System.out.print(throughput[j] + " ");
        System.out.println();
        for (int j = 0; j < ncent; ++j) System.out.print(dataDC[j] + " ");

    }

    public void modifyConnection(int node, int oldConnection, int newConnection, double capture){
        disconnect(node,oldConnection);
        connect(node,newConnection,capture);
    }

    private void disconnect(int i, int j){
        connexions[i] = -1;
        if(j >= ncent) {
            throughput[j-ncent] = dataVolume(j);
            propagate_disconnect(connexions[j-ncent]);
        }
        else dataDC[j] = dataVolume(j);
    }

    private void connect(int i, int newConnection, double capture){
        connexions[i] = newConnection;
        if(newConnection >= ncent){
            propagateThroughput(throughput[newConnection-ncent],
                    sensors.get(newConnection- ncent).getCapacidad()*3,
                    capture,
                    connexions[newConnection-ncent],
                    newConnection);
        }
        else dataDC[newConnection] += capture;
    }

    public double dataVolume(int j){
        double sum = 0;
        int num_connexions = 0;
        double max_throughput;
        int limit;
        if (j >= ncent) {
            max_throughput = 3*sensors.get(j-ncent).getCapacidad();
            limit = 3;
            if (connexions[j-ncent] != -1) sum += sensors.get(j-ncent).getCapacidad();
        }
        else {
            max_throughput = 150;
            limit = 25;
        }
        for(int i = 0; i < nsens; ++i){
            if (connexions[i] ==  j){
                sum += throughput[i];
                ++num_connexions;
            }
            if(num_connexions >=limit) return Math.min(sum, max_throughput);
        }
        return Math.min(sum, max_throughput);
    }

    public int connexions(int i){return connexions[i];}

    public double dataDC(int i){return dataDC[i];}

    public int getNcent(){
        return (ncent);
		}

    public int getnElements(){
        return nElements;
    }

    public double[][] getDist() {
				return dist;
		}

    public ArrayList<Centro> getDatacenters() {
				return datacenters;
		}

    public ArrayList<Sensor> getSensors() {
				return sensors;
		}

    public double getMaxData(){return maxData;}

    @Override
    public String toString() {
        StringBuilder retVal = new StringBuilder();
        for (int i = 0; i < nsens; ++i) retVal.append(connexions[i]).append(" ");
        retVal.append("\n");
        for (int i = 0; i < nsens; ++i) retVal.append(throughput[i]).append(" ");
        retVal.append("\n");
        for (int i = 0; i < ncent; ++i) retVal.append(dataDC[i]).append(" ");
        retVal.append("\n");
        return retVal.toString();

    }
}
