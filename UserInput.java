import java.util.Scanner;

public class UserInput {

    Scanner sc;

    String processFile;
    int arrivalTime;
    int computationTime;
    int deadline;

    public UserInput() {
        this.sc = new Scanner(System.in);
    }

    public void paramAsk() {
        System.out.println("Insira o arquivo do processo:");
        processFile = sc.nextLine();

        System.out.println("Insira o arrival time:");
        arrivalTime = sc.nextInt();

        System.out.println("Insira o computation time:");
        computationTime = sc.nextInt();

        System.out.println("Insira o deadline:");
        deadline = sc.nextInt();
    }

}