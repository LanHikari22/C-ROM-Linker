import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {

    /**
     * TODO: doc main()
     *
     * @param args Format: {ROM Path} {Path of Object File To Inject} (Memory Map Config file [*.mmp])
     */
    public static void main(String[] args) {
        if(args.length < 3) {
            System.err.println("Invalid argument format to ROM Linker. Format: <ROM> <ObjFile> <MemMap.mmp> [suppressReport (true/false)]");
            return;
        }
        String objFile = args[1]; // "C:\\Users\\alzakariyamq\\Development\\ARM\\new.o";
        String memoryMapPath = args[2]; // "ROM_Memory_Map.mmp";
        String ROM_path = args[0]; // "BlankFile";
        boolean suppressReport = false;
        if(args.length == 4)
            suppressReport = new Scanner(args[3]).nextBoolean();

        try {
            // Get the array of variables and functions from the symbol table
            String output = runBatScript("GetObjSymbols " + objFile + " " + "SYMBOL_TABLE");
            SymbolTable symTbl = new SymbolTable(output);
            Variable[] variables = symTbl.getVariables();
            Function[] functions = symTbl.getFunctions();

            // Find out where the variables are used in the functions so that you can modify the RAM reference
            // Also find out where the functions are called so you can modify dummy BLs.
            output = runBatScript("GetObjSymbols " + objFile + " " + "RELOCATION_TABLE");
            RelocationTable relTbl = new RelocationTable(output);
            relTbl.setRelocOffsets(variables, functions);

            // Configure the memory map object to be used to determine where variabels and functions are placed
            // Now we need to know where the text sections and the data sections are in the object file
            output = runBatScript("GetObjSymbols " + objFile + " " + "SECTION_HEADERS");
            // We also need to know where in the ROM (and RAM) to put things. This basically loads our ROM Memory map config file.
            MemoryMap mmp = new MemoryMap(memoryMapPath, output);

            // The data section class will handle assigning RAM addreses to the variables, as well as determining the content..
            byte[] objBuf = createBuffer(objFile);
            DataSection.setupVariables(objBuf, mmp, variables);

            // Now account for fixing references to .rodata
            RoDataSection rodatasection = new RoDataSection(mmp, objBuf, relTbl);

            // The text section class will handle assigning actual ROM addresses to functions, determining their content,
            // as well as replacing all function relative global variable references to their appropriate values in RAM.
            // It will also replace all dummy BL calls.
            TextSection.setupFunctions(objBuf, mmp, variables, functions);


            // Yay! Time to inject this stuff into ROM! We will use a temporary file to send data to the ROMInjector module.
            // Injecting .rodata
            rodatasection.writeContentIntoFile("temp.bin");
            runBatScript("inject_into_ROM " + ROM_path + " temp.bin " + String.format("0x%08x", mmp.getRoDataSegment().Address));
            // Injecting all functions
            if(!suppressReport)
                System.out.println(); // Just a new line so the report is nicely formatted
            for(int i = 0; i < functions.length; i++) {
                if (suppressReport) {
                    writeIntoFile("temp.bin", functions[i]);
                    runBatScript("inject_into_ROM " + ROM_path + " temp.bin " + String.format("0x%08x", functions[i].Address));
                } else {
                    System.out.printf("%s: (Address= 0x%08x)\n", functions[i].Name, functions[i].Address, functions[i].RelAddress);
                    writeIntoFile("temp.bin", functions[i]);
                    System.out.println("Injecting into ROM...");
                    System.out.println(
                            runBatScript("inject_into_ROM " + ROM_path + " temp.bin " + String.format("0x%08x", functions[i].Address))
                    );
                }
            }

        } catch(IOException e){
            System.err.println(e.getMessage());
        }

    }

    private static void writeIntoFile(String path, Function function) throws IOException {
        Files.write(Paths.get(path), function.Content);
    }

    /**
     * TODO: doc createBuffer()
     * @param path
     * @return
     */
    private static byte[] createBuffer(String path) {
        byte[] output = null;
        try {
            output = Files.readAllBytes(Paths.get(path));
        } catch(IOException e){
            System.err.println("Error while executing createBuffer(): " + e.getMessage());
        }
        return output;
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
