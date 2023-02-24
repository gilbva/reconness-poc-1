package reconness.poc1;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

class Request {
    int action;

    String username;

    String password;

    long latency;

    String token;

    boolean error;

    public Request(int action, String username, String password) {
        this.action = action;
        this.username = username;
        this.password = password;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        sb.append("action=");
        sb.append(action);
        sb.append(" username=");
        sb.append(username);
        sb.append(" password=");
        sb.append(password);
        sb.append(" latency=");
        sb.append(latency);
        sb.append(" token=");
        sb.append(token);
        sb.append(" error=");
        sb.append(error);
        return sb.toString();
    }
}

class ChaosMonkey {
    Random random = new Random();
    List<NetworkObject> objects = new ArrayList<>();

    List<NetworkObject> panicObjects = new ArrayList<>();

    public ChaosMonkey(ScheduledExecutorService executor, NetworkObject root) {
        fill(root);
        executor.scheduleAtFixedRate(this::panic, 3, 3, TimeUnit.SECONDS);
    }

    private void fill(NetworkObject root) {
        objects.add(root);
        for(var child : root.getChildren()) {
            fill(child);
        }
    }

    public void panic() {
        if(random.nextInt() % 2 == 0) {
            var obj = objects.remove(random.nextInt(objects.size()));
            obj.panic(true);
            panicObjects.add(obj);
        }
        if(random.nextInt() % 2 == 0) {
            var obj = panicObjects.remove(random.nextInt(panicObjects.size()));
            obj.panic(false);
            objects.add(obj);
        }
    }
}

public class UsersServiceTest {

    Random random = new Random();
    List<Request> requests = createRequests();

    private List<Request> createRequests() {
        List<Request> requests = new LinkedList<>();
        for(int i = 0; i < 1000000; i++) {
            requests.add(new Request(random.nextInt(2), UUID.randomUUID().toString().substring(0, 7), UUID.randomUUID().toString().substring(0, 7)));
        }
        return requests;
    }

    @Test
    public void testUsers() {
        var executor = Executors.newSingleThreadScheduledExecutor();
        var cloud = new Cloud(2);
        var chaos = new ChaosMonkey(executor, cloud);
        requests.parallelStream()
                .forEach(x -> {
            try {
                System.out.println("sending request: " + x.toString());
                long ts = System.currentTimeMillis();
                if (x.action == 0) {
                    x.token = cloud.auth(x.username, x.password);
                } else {
                    cloud.register(x.username, x.password);
                }
                x.latency = System.currentTimeMillis() - ts;
                System.out.println("request success: " + x.toString());
            }
            catch (Exception ex) {
                System.out.println("request error: " + x.toString());
                x.error = true;
            }
        });
    }

}
