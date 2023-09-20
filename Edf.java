import java.util.ArrayList;
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
      readyQueue.addAll(processsToAdd);
    }
  }

  private void validateDeadline(Processo p) {
    p.lostDeadline = p.availableTime > 0 && p.deadline <= currentTime;
    if (p.lostDeadline) {
      System.out.println("\tProcesso " + p.name + " perdeu deadline!");
      p.deadline = p.deadline + p.deadlineAbs;
    }
  }

  public void updateBlockedQueue() {
    Iterator<Processo> it = blockedQueue.iterator();

    while (it.hasNext()) {
      Processo p = it.next();
      p.decrementBlockedTime();

      if (p.blockedTime == 0) {
        validateDeadline(p);
        readyQueue.add(p);
        it.remove();
      }
    }
  }

  public void run() {
    while (readyQueue.size() > 0 || blockedQueue.size() > 0 || processMap.size() > 0) {

      updateReadyQueue();

      Processo currentProcess = readyQueue.poll();

      // Reduz 1 do tempo de bloquei da fila de bloqueados
      updateBlockedQueue();

      if (currentProcess != null) {
        System.out.println("Ciclo " + currentTime + ". Executando processo: " + currentProcess.name);

        boolean hasExit = currentProcess.exec();

        validateDeadline(currentProcess);

        if ((currentProcess.isIo || currentProcess.availableTime == 0) && !hasExit) {

          if (currentProcess.availableTime == 0) {
            // Se o processo terminou o tempo de computação,
            // reseta o tempo de computação e incrmenta o deadline
            currentProcess.availableTime = currentProcess.computationTime;
            currentProcess.blockedTime = currentProcess.deadline - currentTime;
            currentProcess.deadline = currentProcess.deadline + currentProcess.deadlineAbs;
            currentProcess.lostDeadline = false;
            System.out.println("\tProcesso completou tempo de computação!");
          }

          // Adiciona na fila de bloqueados se saiu para IO ou se finalizou o tempo de
          // computação
          blockedQueue.add(currentProcess);

        } else if (currentProcess.availableTime != 0 && !hasExit) {
          readyQueue.add(currentProcess);
        }

      } else {
        System.out.println("Ciclo " + currentTime + ". Em IDLE");

      }

      System.out.println("\n");

      currentTime++;
    }
    System.out.println("Fim");
  }

}
