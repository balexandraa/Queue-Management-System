package Model;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

public class Server implements Runnable{

    private BlockingQueue<Task> tasks;
    private AtomicInteger waitingPeriod;

    public Server(AtomicInteger waitingPeriod){
        // initialize queue and waiting time
        this.waitingPeriod = waitingPeriod;
        this.waitingPeriod.getAndSet(0);
        tasks = new LinkedBlockingDeque<>();
    }

    public void addTask(Task newTask){
        tasks.add(newTask);
        // increment the waitingPeriod
        waitingPeriod.getAndAdd(newTask.getServiceTime());
    }

    @Override
    public void run() {
        while(!tasks.isEmpty())
        {
            Task t = null;
            try {
                t = tasks.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //oprim thread-ul dupa ce s-a procesat task-ul
            try {
                Thread.sleep(t.getServiceTime() * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //scadem perioada de asteptare
            waitingPeriod.getAndDecrement();
        }
    }

    public BlockingQueue<Task> getTasks() {
        return tasks;
    }

    public AtomicInteger getWaitingPeriod() {
        return waitingPeriod;
    }
}
