package BusinessLogic;

import Model.Server;
import Model.Task;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ConcreteStrategyTime implements Strategy{
    @Override
    public void addTask(List<Server> servers, Task t) {
        // initializam cu valori random
        Server server = servers.get(0);
        AtomicInteger minWaitingPeriod = servers.get(0).getWaitingPeriod();

        //gasim task ul cu minimum waiting time si il adaugam in server
        for (Server s: servers) {
            if (s.getWaitingPeriod().get() < minWaitingPeriod.get()) {
                minWaitingPeriod = s.getWaitingPeriod();
                server = s;
            }
        }
        server.addTask(t);

        System.out.println("Current client: " + t);
        System.out.println("Corresponding queue: " + servers.indexOf(server));
        System.out.println("Waiting time queues:");
        for(Server s: servers)
            System.out.println(s.getWaitingPeriod());

    }
}
