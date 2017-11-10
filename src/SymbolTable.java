import java.util.ArrayList;
import java.util.Scanner;

public class SymbolTable {

    /**
     * An Entry object represents one row in the symbol table.
     */
    static class Entry {
        int Num;
        int Value;
        int Size;
        String Type;
        String Bind;
        String Vis;
        int Ndx;
        String Name;

        static final int Ndx_UND = -1;
        static final int Ndx_ABS = -2;
        static final int Ndx_COM = -3;
    }

    /**
     * Extracted Symbol Table
     */
    public Entry[] Table;

    public SymbolTable(String symTableReport){
        Table = parse(symTableReport);
    }

    /**
     * parses the symTableReport, which is the output of the arm-none-eabi-readelf program for the symbol table into
     * an array of Entries and returns that.
     * @param symTableReport    Table to prase
     * @return The array of the parsed entries
     */
    private static Entry[] parse(String symTableReport) {
        Scanner s = new Scanner(symTableReport);
        // ignore initial line
        s.nextLine();
        // extract size from second line
        for(int i=0;i<4;i++) s.next();
        int size = s.nextInt();
        s.nextLine();
        // initialize table output
        Entry[] output = new Entry[size];
        // Next line shows labels of entrys, not an entry.
        s.nextLine();
        // Now parse the next size entries into the output table
        for(int i = 0; i < size; i++) {
            Entry entry = new Entry();
            // parse Num
            String temp = s.next();
            entry.Num = Integer.parseInt(temp.substring(0,temp.length()-1));
//            System.out.print(entry.Num + ": ");
            // parse Value, Size, Type, Bind, and Vis
            entry.Value = s.nextInt(16);
            entry.Size = s.nextInt();
            entry.Type = s.next();
            entry.Bind = s.next();
            entry.Vis = s.next();
//            System.out.printf("%08x, %d, %s, %s, %s", entry.Value, entry.Size, entry.Type, entry.Bind, entry.Vis);
            // parse Ndx
            temp = s.next();
            if(temp.equals("UND")) entry.Ndx = Entry.Ndx_UND;
            else if(temp.equals("ABS")) entry.Ndx = Entry.Ndx_ABS;
            else if(temp.equals("COM")) entry.Ndx = Entry.Ndx_COM;
            else entry.Ndx = Integer.parseInt(temp);
//            System.out.print(", " + entry.Ndx);
            // parse Name
            entry.Name = s.nextLine().substring(1); // TODO: space not ignored? why?
//            System.out.println(", " + entry.Name);
            // put entry into table
            output[i] = entry;
        }
//        System.out.println(symTableReport);
        return output;
    }

    public Variable[] getVariables(){
        ArrayList<Variable> variables = new ArrayList<Variable>();
        for(int i = 0; i < Table.length; i++){
            if(Table[i].Type.equals("OBJECT")){
                Variable var = new Variable();
                var.Name = Table[i].Name;
                var.RelAddress = Table[i].Value;
                var.Size = Table[i].Size;
                variables.add(var);
            }
        }

        Variable[] output = new Variable[variables.size()];
        output = variables.toArray(output);
        return output;
    }

    public Function[] getFunctions(){
        ArrayList<Function> functions = new ArrayList<Function>();
        for(int i = 0; i < Table.length; i++) {
            if (Table[i].Type.equals("FUNC")) {
                Function func = new Function();
                func.Name = Table[i].Name;
                func.RelAddress = Table[i].Value;
                func.Size = Table[i].Size;
                functions.add(func);
            }
        }

        Function[] output = new Function[functions.size()];
        output = functions.toArray(output);
        return output;
    }
}
