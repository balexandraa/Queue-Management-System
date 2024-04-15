package BusinessLogic;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
public class FileManager {

    private static FileWriter fileWriter;

    public static void createFile(String name) {
        try {
            fileWriter = new FileWriter(name);
        } catch (IOException e) {
            System.out.println("Error while creating the file!!");
            e.printStackTrace();
        }
    }

    public static void writeToFile(String content, boolean printConsole) {
        try {
            fileWriter.write(content + "\n");
            if (printConsole) {
                System.out.println(content);
            }
        } catch (IOException e) {
            System.out.println("Error while writing to the file!!");
            e.printStackTrace();
        }
    }

    public static void closeFile() {
        try {
            fileWriter.close();
        } catch (IOException e) {
            System.out.println("Error while closing the file!!");
            e.printStackTrace();
        }
    }
}
