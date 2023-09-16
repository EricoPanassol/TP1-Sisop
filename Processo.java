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
    int deadline;
    int availableTime;

    boolean isIo;
    int blockedTime;

    String name;

    int pc;
    int acc;

    Map<Integer, String> instructionMap = new HashMap<Integer, String>();
    Map<String, Integer> dataMap = new HashMap<String, Integer>();
    Map<String, Integer> labelsMap = new HashMap<String, Integer>();

    ArrayList<String> immediateCommands = new ArrayList<String>(
            Arrays.asList("add", "sub", "mult", "div", "load", "store"));
    private Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

    public Processo(String fileName, int arrivalTime, int computationTime, int deadline) throws FileNotFoundException {
        pc = 1;
        acc = 0;
        this.arrivalTime = arrivalTime;
        this.computationTime = computationTime;
        this.deadline = deadline;
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

                String[] lineRef = line.trim().split("#");

                if (lineRef.length == 1) {
                    line = lineRef[0].trim();
                } else {
                    if (immediateCommands.contains(lineRef[0])) {
                        if (isNumeric(lineRef[1])) {
                            line = lineRef[0] + " #" + lineRef[1];
                        } else {
                            line = lineRef[0] + " " + lineRef[1];
                        }
                    } else {
                        line = lineRef[0].trim();
                    }
                }

                if (line.contains(":")) {
                    String[] labelRef = line.split(":");

                    if (labelRef.length == 2) {
                        labelsMap.put(labelRef[0], lineCounter);
                        instructionMap.put(lineCounter, labelRef[1]);
                    } else {
                        labelsMap.put(labelRef[0], lineCounter);
                        String instruction = myReader.nextLine().trim();
                        instructionMap.put(lineCounter, instruction);
                    }
                } else {
                    instructionMap.put(lineCounter, line);
                }

            } else if (insideDataBlock) {
                String[] variable = line.trim().split(" ");
                dataMap.put(variable[0], Integer.parseInt(variable[1]));

            }
            lineCounter++;
        }

        myReader.close();
    }

    public boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        return pattern.matcher(strNum).matches();
    }

    public String readFile(File program) throws FileNotFoundException {
        StringBuilder data = new StringBuilder();

        // try {
        Scanner myReader = new Scanner(program);
        while (myReader.hasNext()) {
            String line = myReader.nextLine();
            if (!line.isEmpty()) {
                data.append(line).append("\n");
            }
        }
        myReader.close();

        // } catch (FileNotFoundException e) {
        // System.out.println("File not found: " + e.getMessage());
        // // e.printStackTrace();
        // }
        return data.toString();
    }

    // Retornad true se finalizou a execução
    // @ToDo: caso haja perca de deadline, manda um sinal pro EDF para ele mostrar o
    // output
    // do nome do prog e o tempo que em perdeu
    public boolean exec() {
        // Executa o codigo
        availableTime--;
        pc++;
        System.out.println("PC: " + pc);
        System.out.println(instructionMap.get(pc));

        // Verifica se é uma instrução de IO
        this.isIo = false;

        return instructionMap.get(pc).equals("syscall 0");
    }

    public void setBlockedTime() {
        Random random = new Random();
        this.blockedTime = random.nextInt(3 - 1) + 1;
    }

    public void decrementBlockedTime() {
        if (blockedTime > 0) {
            this.blockedTime--;
        } else {
            this.isIo = false;
        }
    }

    @Override
    public int compareTo(Processo p2) {
        System.out.println("Comparou");
        if (this.deadline < p2.deadline) {
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
