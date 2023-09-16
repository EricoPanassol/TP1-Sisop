import java.io.FileNotFoundException;

public class App {

    public static void main(String[] args) {
        UserInput userInput = new UserInput();
        Edf edf = new Edf();

        boolean addingProcess = true;

        while (addingProcess) {
            try {
                userInput.paramAsk();
                Processo p = new Processo(userInput.processFile, userInput.arrivalTime, userInput.computationTime,
                        userInput.deadline);

                edf.addProcess(p);
                userInput.sc.nextLine();

                System.out.println("Deseja adicionar outro processo? (s/n)");
                String answer = userInput.sc.nextLine();
                addingProcess = answer.toLowerCase().equals("s");
            } catch (FileNotFoundException e) {
                System.out.println(e);
                System.out.println("Erro ao ler arquivo, tente novamente.\n\n");
            }
        }

        edf.run();
    }

}
