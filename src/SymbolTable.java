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
            else entry.Ndx = Integer.parseInt(temp);
//            System.out.print(", " + entry.Ndx);
            // parse Name
            entry.Name = s.nextLine();
//            System.out.println(", " + entry.Name);
            // put entry into table
            output[i] = entry;
        }
        System.out.println(symTableReport);
        return output;
    }

    public Variable[] getVariables(){
        return null;
    }

    public Function[] getFunctions(){
        return null;
    }
}
