package reconness.poc1;

import java.util.HashMap;
import java.util.Map;

public class Internet {
    private Map<String, GatewayServer> gateways = new HashMap<>();

    public GatewayServer connect(String ip) {
        return gateways.get(ip);
    }
}
