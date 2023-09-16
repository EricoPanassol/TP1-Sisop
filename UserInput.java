import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;

public class UserInput {

    static int pc = 0;
    static int acc = 0;
    static ArrayList<Object> readyQueue = new ArrayList<>();
    static ArrayList<Object> blockedQueue = new ArrayList<>();

    public static String readFile(File program) {
        StringBuilder data = new StringBuilder();

        try {
            Scanner myReader = new Scanner(program);
            while(myReader.hasNext()) {
                String line = myReader.nextLine();
                if (!line.isEmpty()) {
                    data.append(line).append("\n"); 
                }
            }
            myReader.close();

        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
            e.printStackTrace();
        }
        
        return data.toString();
    }

    public static List<List<String>> getInstructionDataParametersAndSaveToList(String program) {
        ArrayList<String> instructionList = new ArrayList<>();
        ArrayList<String> dataList = new ArrayList<>();
        ArrayList<String> parameterList = new ArrayList<>();
    
        ArrayList<List<String>> all = new ArrayList<>();
        all.add(instructionList);
        all.add(dataList);
        all.add(parameterList);
    
        Scanner myReader = new Scanner(program);
    
        boolean insideCodeBlock = false;
        boolean insideDataBlock = false;
    
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
                instructionList.add(line);
            } else if (insideDataBlock) {
                dataList.add(line);
            } else {
                try {
                    int number = Integer.parseInt(line);
                    parameterList.add(String.valueOf(number));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
    
        myReader.close();
        return all;
    }
    

    public static void main (String args[]){
        File p1 = new File("./p1.txt");
        String filed = readFile(p1);
        
        List<List<String>> a = getInstructionDataParametersAndSaveToList(filed);

        System.out.println(a);
    }
}