package reconness.poc1;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
        executor.scheduleAtFixedRate(this::panic, 100, 100, TimeUnit.MILLISECONDS);
    }

    private void fill(NetworkObject root) {
        objects.add(root);
        for(var child : root.getChildren()) {
            fill(child);
        }
    }

    public void panic() {
        System.out.println("throwing random panic");
        if(random.nextInt(100) % 2 == 0) {
            var obj = objects.remove(random.nextInt(objects.size()));
            obj.panic(true);
            panicObjects.add(obj);
        }
        if(random.nextInt(100) % 2 == 0) {
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
        Map<String, String> passwords = new HashMap<>();
        List<String> users = new ArrayList<>();
        List<Request> requests = new LinkedList<>();
        for(int i = 0; i < 1000; i++) {
            var user = UUID.randomUUID().toString().substring(0, 7);
            users.add(user);
            passwords.put(user, UUID.randomUUID().toString().substring(0, 7));
            requests.add(new Request(1, user, passwords.get(user)));
        }
        for(int i = 0; i < 1000; i++) {
            var user = users.get(random.nextInt(users.size()));
            requests.add(new Request(0, user, passwords.get(user)));
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
                long ts = System.currentTimeMillis();
                if (x.action == 0) {
                    System.out.println("sending auth request: " + x.toString());
                    x.token = cloud.auth(x.username, x.password);
                } else {
                    System.out.println("sending register request: " + x.toString());
                    cloud.register(x.username, x.password);
                }
                x.latency = System.currentTimeMillis() - ts;
                System.out.println("request success: " + x.toString());
                Thread.sleep(100);
            }
            catch (Exception ex) {
                System.out.println("request error: " + x.toString());
                x.error = true;
            }
        });

        var miss= requests.stream().filter(x -> x.action == 0 && x.token == null).count();
        var total= requests.stream().filter(x -> x.action == 0).count();
        var missPercent = (double) miss / total * 100d;
        System.out.println("total: " + total + ", misses: " + miss + ", missPercent: " + missPercent + "%");
    }

}
