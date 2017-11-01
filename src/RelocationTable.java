import java.util.ArrayList;
import java.util.Scanner;

public class RelocationTable {

    /**
     * An Entry object represents one row in the symbol table.
     */
    static class Entry {
        int Offset;
        int Info;
        String Type;
        int SymValue;
        String SymName;

        static final int NO_VALUE = -1;
        static final String NO_NAME = "NO_NAME";
    }

    /**
     * Extracted Relocation Table
     */
    public Entry[] Table;

    public RelocationTable(String relTableReport){
        Table = parse(relTableReport);
    }

    private static Entry[] parse(String relTableReport) {
        Scanner s = new Scanner(relTableReport);

        // First line is a blank, ignore it.
        s.nextLine();
        // Parse size of table
        for(int i = 0; i < 7; i++) s.next();
        int size = s.nextInt();
//        System.out.printf("size= %d\n", size);
        s.nextLine();
        // next line is the labels row.
        s.nextLine();
        // initialize output table
        Entry[] output = new Entry[size];
        // Now parse all entries
        for(int i = 0; i < size; i++){
            Entry entry = new Entry();
            // Parse offset, Info, Type, Value and Name...
            String row = s.nextLine();
            Scanner sRow = new Scanner(row);
            entry.Offset = sRow.nextInt(16);
            entry.Info = sRow.nextInt(16);
            entry.Type = sRow.next();
            if(sRow.hasNext()){
                entry.SymValue = sRow.nextInt(16);
                if(sRow.hasNextLine()){
                    entry.SymName = sRow.nextLine().substring(3); // account for weird tabing at the start. TODO: why is this not filtered out?
                }
                else {
                    entry.SymName = Entry.NO_NAME;
                }
            }
            else {
                entry.SymValue = Entry.NO_VALUE;
                entry.SymName = Entry.NO_NAME;
            }
//            System.out.printf("%08x, %08x, %s, %08x, %s\n", entry.Offset, entry.Info, entry.Type, entry.SymValue, entry.SymName);
            // put entry into output table
            output[i] = entry;
        }

        System.out.println(relTableReport);
        return output;
    }

    public void setRelocOffsets(Variable[] variables) {
        // TODO: [deternubeRelocOffsets()] O(n^2). Not cool, change it?
        for(int i = 0; i < variables.length; i++) {
            ArrayList<Integer> RelocOffsets = new ArrayList<Integer>();
            for (int j = 0; j < Table.length; j++) {
                System.out.printf("'%s'  vs  '%s'\n", Table[j].SymName, variables[i].Name);
                if (Table[j].SymName.equals(variables[i].Name)){
                    RelocOffsets.add(Table[j].Offset);
                }
            }

            Integer[] temp = new Integer[RelocOffsets.size()];
            temp = RelocOffsets.toArray(temp);
            variables[i].RelocOffsets = temp;
        }
    }

}
