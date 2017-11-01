import java.io.*;
import java.util.Scanner;

public class Main {

    /**
     * TODO: doc main()
     *
     * @param args Format: {ROM Path} {Path of Object File To Inject}
     */
    public static void main(String[] args) {
        String objFile = "C:\\Users\\alzakariyamq\\Development\\ARM\\new.o";
//        String type = "RELOCATION_TABLE";
        String type = "SYMBOL_TABLE";
        try {
            String output = runBatScript("GetObjSymbols " + objFile + " " + type);
            SymbolTable symTbl = new SymbolTable(output);

        } catch(IOException e){
            System.err.println(e.getMessage());
        }

    }

    /**
     * Executes a batch script and returns the output of it as a string.
     * One simple rule. The first two lines must be "\n" and "echo off".
     * Unexpected behavior may occur otherwise.
     * @param batScript The path to the batch script to run
     * @throws IOException Errors that arise through the runtime environment, or through errors in the batch file.
     * @return The output of the script.
     */
    private static String runBatScript(String batScript) throws IOException {
        StringBuilder sb = new StringBuilder();
        Process p = Runtime.getRuntime().exec("cmd /c \"\" " + batScript);
        Scanner s = new Scanner(p.getInputStream());

        // first two lines are "\n" and "echo off".
        if(s.hasNextLine()) s.nextLine();
        else System.err.println("Invalid batch file format");
        if(s.hasNextLine()) s.nextLine();
        else System.err.println("Invalid batch file format");

        while(s.hasNextLine()) {
            sb.append(s.nextLine() + "\n");
        }

        // If there are any errors, throw an IOException indicating
        s = new Scanner(p.getErrorStream());
        if(s.hasNextLine()){
            StringBuilder sb_error = new StringBuilder();
            sb_error.append("Encountered Errors while running batch file:\n");
            while(s.hasNextLine())
                sb_error.append((s.nextLine() + "\n"));
            throw new IOException(sb_error.toString());
        }
        return sb.toString();
    }
}
