package reconness.poc1;

//10^9 / 2000 = 5 * 10^5

//500 000 accounts per day
//6 accounts per second

//20 accounts per second

// region -> zone -> cluster

//2 cluster -> 2 cluster per zone

// 99.999

//inter-region latency west-east 100 - 300 ms sync 2/3
//intra-region latency

// log-based

//quorum

//distributed-tra

// 0 1 2 3 4 5
// 0 1 2

//intra-region sync
//inter-region async

public interface UsersService {

    String auth(String username, String password);

    void register(String username, String password);
}
