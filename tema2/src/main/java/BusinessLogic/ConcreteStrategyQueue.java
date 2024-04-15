package BusinessLogic;

import Model.Server;
import Model.Task;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ConcreteStrategyQueue implements Strategy{
    @Override
    public void addTask(List<Server> servers, Task t) {
        // initializam cu valori random
        Server server = servers.get(0);
        int minSize = server.getTasks().size();

        for (Server s: servers) {
            if (s.getTasks().size() < minSize) {
                minSize = s.getTasks().size();
                server = s;
            }
        }
        server.addTask(t);

        System.out.println("Current client: " + t);
        System.out.println("Corresponding queue: " + servers.indexOf(server));
    }
}
