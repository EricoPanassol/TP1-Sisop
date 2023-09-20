import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Processo implements Comparable<Processo> {

  int arrivalTime;
  int computationTime;
  int deadlineAbs;
  int deadline;
  int availableTime;

  boolean lostDeadline;

  boolean isIo;
  int blockedTime;

  String name;

  int pc;
  int acc;

  Map<Integer, String> instructionMap = new HashMap<Integer, String>();
  Map<String, Integer> dataMap = new HashMap<String, Integer>();
  Map<String, Integer> labelsMap = new HashMap<String, Integer>();

  ArrayList<String> immediateCommands = new ArrayList<String>(
      Arrays.asList("add", "sub", "mult", "div", "load"));
  private Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

  public Processo(String fileName, int arrivalTime, int computationTime, int deadline) throws FileNotFoundException {
    pc = 1;
    acc = 0;
    this.arrivalTime = arrivalTime;
    this.computationTime = computationTime;
    this.deadline = deadline;
    this.deadlineAbs = deadline;
    this.availableTime = computationTime;
    this.name = fileName;
    this.isIo = false;

    File process = new File(fileName);
    String p = readFile(process);
    loadProgramMaps(p);
  }

  public void loadProgramMaps(String program) {
    Scanner myReader = new Scanner(program);

    boolean insideCodeBlock = false;
    boolean insideDataBlock = false;

    int lineCounter = 1;

    while (myReader.hasNextLine()) {
      String line = myReader.nextLine().trim();

      if (line.equals(".code")) {
        insideCodeBlock = true;
        insideDataBlock = false;

      } else if (line.equals(".data")) {
        insideDataBlock = true;
        insideCodeBlock = false;

      } else if (line.equals(".endcode") || line.equals(".enddata")) {
        insideCodeBlock = false;
        insideDataBlock = false;

      } else if (insideCodeBlock) {

        line = getCleanLine(line);

        if (line.contains(":")) {
          String[] labelRef = line.split(":");

          if (labelRef.length == 2) {
            // Comando na mesma linha da label
            labelsMap.put(labelRef[0], lineCounter);
            instructionMap.put(lineCounter, labelRef[1]);
          } else {
            // Comando esacrito na linha abaixo da label
            labelsMap.put(labelRef[0], lineCounter);
            String instruction = getCleanLine(myReader.nextLine().trim());
            instructionMap.put(lineCounter, instruction);
          }
        } else {
          instructionMap.put(lineCounter, line);
        }
        lineCounter++;

      } else if (insideDataBlock) {
        String[] variable = line.trim().split(" ");
        dataMap.put(variable[0], Integer.parseInt(variable[1]));
      }
    }

    myReader.close();
  }

  private String getCleanLine(String line) {
    String[] lineRef = line.trim().split("\\s*#\\s*");

    String cleanLine = lineRef[0].trim();

    if (lineRef.length > 1 && immediateCommands.contains(lineRef[0])) {
      cleanLine = lineRef[0] + " #" + lineRef[1];
    }

    return cleanLine;
  }

  public boolean isNumeric(String strNum) {
    if (strNum == null) {
      return false;
    }
    return pattern.matcher(strNum).matches();
  }

  public String readFile(File program) throws FileNotFoundException {
    StringBuilder data = new StringBuilder();

    Scanner myReader = new Scanner(program);
    while (myReader.hasNext()) {
      String line = myReader.nextLine();
      if (!line.isEmpty()) {
        data.append(line).append("\n");
      }
    }
    myReader.close();

    return data.toString();
  }

  // Retornad true se finalizou a execução
  // @ToDo: caso haja perca de deadline, manda um sinal pro EDF para ele mostrar o
  // output
  // do nome do prog e o tempo que em perdeu
  public boolean exec() {
    // Executa o codigo
    System.out.println("\tPC: " + pc);
    System.out.println("\tInstrução: " + instructionMap.get(pc).toUpperCase());
    System.out.println("\tAcc antes da execução: " + acc);

    String[] instruction = instructionMap.get(pc).toLowerCase().split("\\s+");
    String command = instruction[0];
    String operator = instruction[1];

    boolean hasHalt = false;

    switch (command) {
      case "add":
        acc += getOp1Value(operator);
        break;
      case "sub":
        acc -= getOp1Value(operator);
        break;
      case "mult":
        acc *= getOp1Value(operator);
        break;
      case "div":
        acc /= getOp1Value(operator);
        break;
      case "load":
        acc = getOp1Value(operator);
        break;
      case "store":
        dataMap.put(operator, acc);
        break;
      case "brany":
        pc = labelsMap.get(operator);
        pc--;
        break;
      case "brpos":
        if (acc > 0) {
          pc = labelsMap.get(operator);
          pc--;
        }
        break;
      case "brzero":
        if (acc == 0) {
          pc = labelsMap.get(operator);
          pc--;
        }
        break;
      case "brneg":
        if (acc < 0) {
          pc = labelsMap.get(operator);
          pc--;
        }
        break;
      case "syscall":
        Integer index = Integer.parseInt(operator);
        if (index == 0) {
          hasHalt = true;
        } else {
          this.isIo = true;
          setBlockedTime();
          System.out.println("\t\tIo por " + blockedTime + " ciclos");

          if (index == 1) {
            System.out.println("\t\tPrint Acc: " + acc);
          } else if (index == 2) {
            Scanner sc = new Scanner(System.in);
            int t = sc.nextInt();
            this.acc = t;

            sc.nextLine();
            sc.close();
          }

        }
        break;
      default:
        System.err.println("Comando não reconhecido");
        System.out.println(command);
        System.out.println(operator);
        System.out.println("-----");
        break;
    }

    System.out.println("\tAcc depois da execução: " + acc);
    availableTime--;
    System.out.println("\tavailableTime depois da execução: " + availableTime);

    pc++;
    return hasHalt;
  }

  private int getOp1Value(String op1) {
    if (op1.contains("#")) {
      return Integer.parseInt(op1.split("#")[1]);
    }

    return dataMap.get(op1);
  }

  public void setBlockedTime() {
    Random random = new Random();
    this.blockedTime = random.nextInt(3 - 1) + 1;
  }

  public void decrementBlockedTime() {
    this.blockedTime--;
    this.isIo = blockedTime > 0;
  }

  @Override
  public int compareTo(Processo p2) {
    System.out.println("Comparou");

    if (this.lostDeadline || this.deadline < p2.deadline) {
      // This tem prioridade maior
      return -1;
    } else if (this.deadline > p2.deadline) {
      // P2 tem prioridade maior
      return 1;
    } else {
      // Os dois tem a mesma prioridade, então segue na mesma ordem
      return 0;
    }
  }

}
