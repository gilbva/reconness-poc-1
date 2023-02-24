package reconness.poc1;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public abstract class NetworkObject {

    Random random = new Random();

    boolean error;

    public void panic(boolean value) {
        error = value;
    }

    public <T> T route(List<T> servers) {
        return servers.get(random.nextInt(servers.size()));
    }

    public abstract List<? extends NetworkObject> getChildren();
}
