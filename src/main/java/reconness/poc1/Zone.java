package reconness.poc1;

import java.util.LinkedList;
import java.util.List;

public class Zone extends NetworkObject implements UsersService {
    private List<Cluster> clusters;

    public Zone(int count) {
        this.clusters = new LinkedList<>();
        for(int i = 0; i < count; i++) {
            clusters.add(new Cluster());
        }
    }

    @Override
    public String auth(String username, String password) {
        return route(clusters).auth(username, password);
    }

    @Override
    public void register(String username, String password) {
        route(clusters).register(username, password);
    }

    @Override
    public List<? extends NetworkObject> getChildren() {
        return clusters;
    }
}
