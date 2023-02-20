package reconness.poc1;

import java.util.List;
import java.util.Random;

public class StandardFlowClient {
    private AuthService auth;

    private MarketplaceService market;

    private Random random = new Random();

    private DashboardService dashboard;

    private ExecutionService exec;

    private RoutingService route;

    private Internet internet;

    public void run() {
        String token = auth.login("user1", "asd");
        List<String> agents = market.listAgents(token);
        market.importAgent(token, agents.get(random.nextInt(agents.size())));
        List<String> myAgents = market.listMyAgents(token);
        String targetId = dashboard.createTarget(token, "somedoimain");
        String jobId = exec.execute(token, targetId, myAgents.get(0));
        String gatewayIp = route.findGateway(token, jobId);
        GatewayServer server = internet.connect(gatewayIp);
        server.addListener(new GatewayServer.GatewayListener(jobId, gatewayIp) {
            @Override
            public void logMessage(String message) {

            }
        });
    }
}
