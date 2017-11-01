import java.io.*;
import java.util.Scanner;

public class Main {

    /**
     * TODO: doc main()
     *
     * @param args Format: {ROM Path} {Path of Object File To Inject} (Memory Map Config file [*.mmp])
     */
    public static void main(String[] args) {
        String objFile = "C:\\Users\\alzakariyamq\\Development\\ARM\\new.o";
        String memoryMapPath = "ROM_Memory_Map.mmp";
        try {
            // Get the array of variables and functions from the symbol table
            String output = runBatScript("GetObjSymbols " + objFile + " " + "SYMBOL_TABLE");
            SymbolTable symTbl = new SymbolTable(output);
            Variable[] variables = symTbl.getVariables();
            Function[] functions = symTbl.getFunctions();
            // Find out where the variables are used in the functions so that you can modify the RAM reference
            output = runBatScript("GetObjSymbols " + objFile + " " + "RELOCATION_TABLE");
            RelocationTable relTbl = new RelocationTable(output);
            relTbl.setRelocOffsets(variables);

            // Now we need to know where the text sections and the data sections are in the object file
            output = runBatScript("GetObjSymbols " + objFile + " " + "SECTION_HEADERS");
            // We also need to know where in the ROM (and RAM) to put things. This basically loads our ROM Memory map config file.
            MemoryMap mmp = new MemoryMap(memoryMapPath);
            // The data section class will handle assigning RAM addreses to the variables, as well as determining the content..
//            DataSection.setupVariables(mmp, output, variables);
            // The text section class will handle assigning actual ROM addresses to functions, determining their content,
            // as well as replacing all function relative global variable references to their appropriate values in RAM.
//            TextSection.setupFunctions(mmp, output, variables, functions);
            // Done! Time to inject this stuff into ROM!
            // TODO: Implement injection logic

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
