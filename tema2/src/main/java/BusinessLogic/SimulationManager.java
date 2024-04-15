package BusinessLogic;

import GUI.SimulationFrame;
import Model.Server;
import Model.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class SimulationManager implements Runnable{

    //data read from UI
    public int timeLimit; //maximum processing time - read from UI
    public int maxProcessingTime;
    public int minProcessingTime;
    public int maxArrivalTime;
    public int minArrivalTime;
    public int numberOfServers;
    public int numberOfClients;
    public SelectionPolicy selectionPolicy = SelectionPolicy.SHORTEST_TIME;


    //entity responsable with queue management and client distribution
    private Scheduler scheduler;
    //frame for displaying simulation
    private SimulationFrame frame;
    //pool of tasks (clint shopping in the store)
    private List<Task> generatedTasks;
    AtomicInteger currentTime = new AtomicInteger(0);
    private List<Integer> peakTimes = new ArrayList<>();
    int waitingTime = 0;

    public SimulationManager(SimulationFrame frame) {
        this.frame = frame;
        List<Integer> inputs = frame.getInputs();
        setInputs(inputs);
        scheduler = new Scheduler(numberOfServers, 100);  // 100 tasks per server (max)
        generatedTasks = new ArrayList<>();

        generateNRandomTasks(minArrivalTime, maxArrivalTime);

        // Print the generated tasks
        System.out.println("Generated Tasks:");
        for (Task task : generatedTasks) {
            System.out.println(task);
        }

        //create and start threads
        List<Server> servers = new ArrayList<>();
        for (int i = 0; i < numberOfServers; i++) {
            Server server = new Server(new AtomicInteger(0));
            servers.add(server);
        }

        scheduler.setServers(servers);
        scheduler.setMaxNoServers(1000);
        scheduler.setMaxTasksPerServer(1000);
        for (int i = 0; i < numberOfServers; i++) {
            Thread thread = new Thread(servers.get(i));
            thread.start();
        }
    }

    public void setInputs(List<Integer> inputs){
        numberOfClients = inputs.get(0);
        numberOfServers = inputs.get(1);
        timeLimit = inputs.get(2);
        minArrivalTime = inputs.get(3);
        maxArrivalTime = inputs.get(4);
        minProcessingTime = inputs.get(5);
        maxProcessingTime = inputs.get(6);

    }

    public void generateNRandomTasks(int minArrivalTime, int maxArrivalTime) {
        Random rand = new Random();
        for (int i = 1; i <= numberOfClients; i++) {
            int arrivalTime = rand.nextInt(maxArrivalTime - minArrivalTime + 1) + minArrivalTime;
            int processingTime = rand.nextInt(maxProcessingTime - minProcessingTime + 1) + minProcessingTime;
            Task task = new Task(i, arrivalTime, processingTime);
            generatedTasks.add(task);
        }
        Collections.sort(generatedTasks); // sort after arrivalTime
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public Double getAverageServiceTime(){
        //calculez serviceTime mediu per client, avand toti clientii cunoscuti
        float sum=0;
        for(Task t:generatedTasks)
            sum += t.getServiceTime();

        return sum/ (double) numberOfClients;
    }

    public int getPeakTime()
    { int max = -1, peak = -1;
        for(int i = 0; i < peakTimes.size(); i++)
            if(peakTimes.get(i) > max) {
                max = peakTimes.get(i);
                peak = i;
            }
        return peak;
    }

    @Override
    public void run() {
        FileManager.createFile("Test3.txt"); // Create the file

        while (currentTime.get() <= timeLimit) {
            if (currentTime.get() == 0)
                FileManager.writeToFile("Average Service Time: " + String.valueOf(getAverageServiceTime()), true);

            FileManager.writeToFile(" ", true);
            FileManager.writeToFile("Time " + currentTime, true);

            List<Task> tasksToRemove = new ArrayList<>();
            for (Task t : generatedTasks) {
                if (t.getArrivalTime() <= currentTime.get()) {
                    tasksToRemove.add(t);
                }
            }

            for (Task t : tasksToRemove) {
                scheduler.dispatchTask(t);
                generatedTasks.remove(t);
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            FileManager.writeToFile("Waiting clients:", true);
            for (Task t : generatedTasks) {
                FileManager.writeToFile("(" + t.getID() + ", " + t.getArrivalTime() + ", " + t.getServiceTime() + ")", true);
            }

            int k = 0;
            for (Server s: scheduler.getServers()) {
                if (!s.getTasks().isEmpty()) {
                    FileManager.writeToFile("Queue " + k + ": ", true);
                    for (Task t: s.getTasks())
                        FileManager.writeToFile("(" + t.getID() + ", " + t.getArrivalTime() + ", " + t.getServiceTime() + ")", true);
                }
                else
                    FileManager.writeToFile("Queue " + k + ": closed", true);
                k++;
            }

            currentTime.getAndIncrement();

            //scadem waiting time cu 1 din fiecare coada
            for(Server s: scheduler.getServers()) {
                if (!s.getTasks().isEmpty() && s.getWaitingPeriod().get() >= 1)
                    s.getWaitingPeriod().getAndAdd(-1);
            }

            for (Task t: generatedTasks) {
                if (t.getArrivalTime() == currentTime.get()) {
                    if (t.getArrivalTime() + t.getServiceTime() <= timeLimit)
                        waitingTime += t.getServiceTime();
                }
            }

            //peak time
            int clients = 0;
            for(int i = 0; i < scheduler.getServers().size(); i++)
                clients += scheduler.getServers().get(i).getTasks().size();
            peakTimes.add(clients);

            for(Server s: scheduler.getServers())
            {  //fac rost de taskul din varful cozii
                Task t=s.getTasks().peek();
                if(t != null) {
                    if (t.getServiceTime() >= 1)
                        t.setServiceTime(t.getServiceTime() - 1);
                    if (t.getServiceTime() == 0) {
                        s.getTasks().remove(t);
                        int qIndex = scheduler.getServers().indexOf(s);
                        System.out.println("Remove task from queue " + qIndex);
                    }
                }
            }
        }

        FileManager.writeToFile("", true);
        FileManager.writeToFile("Average Waiting Time: " + waitingTime/ (double) numberOfClients, true);
        FileManager.writeToFile("Peak Hour: " + String.valueOf(getPeakTime()), true);
        FileManager.closeFile();

    }

    public static void main(String[] args){
        SimulationFrame frame = new SimulationFrame();
    }
}
