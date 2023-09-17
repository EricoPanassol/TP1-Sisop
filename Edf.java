import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
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
        ArrayList<Processo> processsToAdd = processMap.remove(currentTime);
        if (processsToAdd != null) {
            Arrays.toString(processsToAdd.toArray());

            readyQueue.addAll(processsToAdd);
        }
    }

    public void updateBlockedQueue() {
        Iterator<Processo> it = blockedQueue.iterator();

        while (it.hasNext()) {
            Processo p = it.next();
            p.decrementBlockedTime();

            if (p.blockedTime == 0) {
                readyQueue.add(p);
                it.remove();
            }
        }
    }

    public void run() {
        while (readyQueue.size() > 0 || blockedQueue.size() > 0 || processMap.size() > 0) {

            // System.out.println("--> Tempo Atual: " + currentTime);

            updateReadyQueue();

            Processo currentProcess = readyQueue.poll();

            // Reduz 1 do tempo de bloquei da fila de bloqueados
            updateBlockedQueue();

            if (currentProcess != null) {
                System.out.println("Ciclo " + currentTime + ". Executando processo: " + currentProcess.name);
                // System.out.println("Executando processo: " + currentProcess.name);

                boolean hasExit = currentProcess.exec();

                if (currentProcess.isIo) {
                    blockedQueue.add(currentProcess);
                } else if (!hasExit) {
                    readyQueue.add(currentProcess);
                }

            } else {
                System.out.println("Ciclo " + currentTime + ". Em IDLE");

            }

            // System.out.println("+++++ReadyQueue.size()" + readyQueue.size());

            System.out.println("\n");

            currentTime++;
        }
        System.out.println("Fim");
    }

}
