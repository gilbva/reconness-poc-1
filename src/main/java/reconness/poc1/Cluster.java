package reconness.poc1;

import java.util.*;

public class Cluster extends NetworkObject implements UsersService {

    Map<String, String> users = new HashMap<>();

    @Override
    public String auth(String username, String password) {
        if(users.containsKey(username)) {
            String pass = users.get(username);
            if(pass.equals(password)) {
                return username + ":" + UUID.randomUUID().toString().substring(0, 12);
            }
        }
        return null;
    }

    @Override
    public void register(String username, String password) {
        users.put(username, password);
    }

    @Override
    public List<? extends NetworkObject> getChildren() {
        return Collections.emptyList();
    }
}
