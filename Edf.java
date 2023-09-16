import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class Edf {

    int currentTime;

    Map<Integer, ArrayList<Processo>> processMap;
    ArrayList<Process> processList;
    PriorityQueue<Processo> readyQueue;
    ArrayList<Processo> blockedQueue;

    public Edf() {
        this.currentTime = 0;
        this.blockedQueue = new ArrayList<Processo>();
        this.readyQueue = new PriorityQueue<Processo>();

        processMap = new HashMap<>();
    }

    // Adiciona a lista de processos a serem executados no tempo X
    public void addProcess(Processo newProcess) {
        if (processMap.containsKey(newProcess.arrivalTime)) {
            processMap.get(newProcess.arrivalTime).add(newProcess);
        } else {
            ArrayList<Processo> list = new ArrayList<>();
            list.add(newProcess);
            processMap.put(newProcess.arrivalTime, list);
        }
    }

    // Adiciona na fila de prontos os processos que chegam no tempo atual
    public void updateReadyQueue() {
        ArrayList<Processo> processsToAdd = processMap.get(currentTime);
        if (processsToAdd != null) {
            Arrays.toString(processsToAdd.toArray());

            readyQueue.addAll(processsToAdd);
        }
    }

    public void updateBlockedQueue() {
        for (Processo p : blockedQueue) {
            p.decrementBlockedTime();
            if (p.blockedTime == 0) {
                readyQueue.add(p);
            }
        }
    }

    public void run() {
        while (readyQueue.size() > 0 || blockedQueue.size() > 0 || processMap.size() > 0) {
            updateReadyQueue();

            Processo currentProcess = readyQueue.poll();

            if (currentProcess != null) {
                boolean hasExit = currentProcess.exec();

                // Reduz 1 do tempo de bloquei da fila de bloqueados
                updateBlockedQueue();

                if (currentProcess.isIo) {
                    currentProcess.setBlockedTime();
                    blockedQueue.add(currentProcess);
                } else if (!hasExit) {
                    readyQueue.add(currentProcess);
                }

            }

            currentTime++;
        }
        System.out.println("Fim");
    }

}
