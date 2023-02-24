package reconness.poc1;

import java.util.LinkedList;
import java.util.List;

public class Cloud extends NetworkObject implements UsersService {
    private List<Region> regions;

    public Cloud(int count) {
        this.regions = new LinkedList<>();
        for(int i = 0; i < count; i++) {
            regions.add(new Region(count * 2));
        }
    }

    @Override
    public String auth(String username, String password) {
        return route(regions).auth(username, password);
    }

    @Override
    public void register(String username, String password) {
        route(regions).register(username, password);
    }

    @Override
    public List<? extends NetworkObject> getChildren() {
        return regions;
    }
}
