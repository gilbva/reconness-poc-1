package reconness.poc1;

import java.util.LinkedList;
import java.util.List;

public class Region extends NetworkObject implements UsersService {
    private List<Zone> zones;

    public Region(int count) {
        this.zones = new LinkedList<>();
        for(int i = 0; i < count; i++) {
            zones.add(new Zone(count * 2));
        }
    }

    @Override
    public String auth(String username, String password) {
        return route(zones).auth(username, password);
    }

    @Override
    public void register(String username, String password) {
        route(zones).register(username, password);
    }

    @Override
    public List<? extends NetworkObject> getChildren() {
        return zones;
    }
}
