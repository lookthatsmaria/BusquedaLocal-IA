import java.util.*;
public class Main {
    public static void main(String[] args) throws Exception {
        RedSensorsState red = new RedSensorsState(4, 100, 1234, 4321, true);

    }
}

//Sol inicial 1: es posible que se pierda informacion
//Sol inicial 2: se maximiza la información transmitida (no se pierde info, si es posible)