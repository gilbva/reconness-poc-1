package reconness.poc1;


public class GatewayServer {
    public static abstract class GatewayListener {
        String jobId;
        String userToken;

        public GatewayListener(String jobId, String userToken) {
            this.jobId = jobId;
            this.userToken = userToken;
        }

        public abstract void logMessage(String message);
    }

    public void addListener(GatewayListener listener) {

    }
}
