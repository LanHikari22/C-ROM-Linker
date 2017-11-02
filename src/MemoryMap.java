import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class MemoryMap {

    /**
     * The addresses for RAM are absolute, since are written to variables and since functions are modified to match
     * The addresses for ROM are relative to the actual ROM file, since they are used for injecting the functions into the ROM.
     */
    public Segment[] getData() {
        return Data;
    }

    public Segment getText() {
        return Text;
    }

    /**
     * A memory segment in ROM or RAM. Consists of a start address, and a size.
     */
    private class Segment{
        public int Address;
        public int Size;
    }

    private Segment[] Data;
    private Segment Text;
    private int dataSectionOffset;
    private int textSectionOffset;

    public MemoryMap(String memoryMapPath, String headerSectionReport) throws IOException {
        // parse data and text section offsets
        dataSectionOffset = parseDataSectionOffset(headerSectionReport);
        textSectionOffset = parseTextSectionOffset(headerSectionReport);

        parseTextAndDataSegments(memoryMapPath);
    }

    private void parseTextAndDataSegments(String memoryMapPath) throws IOException {
        Scanner s = new Scanner(Paths.get(memoryMapPath));
        if(!s.nextLine().equals("TEXT")) throw new IOException("Invalid mmp format: Expected TEXT to be first line.");
        // Parse the Text Segment
        Text = new Segment();
        Text.Address = s.nextInt(16);
        Text.Size = s.nextInt(10);
        s.nextLine();
        if(!s.nextLine().equals("DATA")) throw new IOException("Invalid mmp format: Expected DATA to be the third line.");
        // There can be variable segments for data, so parse them all
        ArrayList<Segment> dataArrayList = new ArrayList<>();
        while(s.hasNextLine()){
            Segment seg = new Segment();
            seg.Address = s.nextInt(16);
            seg.Size = s.nextInt(10);
            s.nextLine();
            dataArrayList.add(seg);
        }
        // Assign arraylist to Data Array of segments
        Data = new Segment[dataArrayList.size()];
        Data = dataArrayList.toArray(getData());

        // That is all! Thank you, thank you! Bye bye!
        s.close();
    }

    private static int parseDataSectionOffset(String headerSectionReport) {
        int output;
        Scanner s = new Scanner(headerSectionReport);

        // there are 3 unnecessary lines: "There are 10..." line, blank line, and a "Section Headers:" line. Ignore those
        for(int i = 0; i<3; i++)
            s.nextLine();
        // 5th row is data, that is including the labels row
        for(int i = 0; i<4; i++)
            s.nextLine();
        // Offset is 6 nexts in
        for(int i = 0; i<5; i++)
            s.next();
        output = s.nextInt(16);

//        System.out.println(headerSectionReport);
//        System.out.printf("%08x", output);

        return output;
    }

    private int parseTextSectionOffset(String headerSectionReport) {
        int output;
        Scanner s = new Scanner(headerSectionReport);

        // there are 3 unnecessary lines: "There are 10..." line, blank line, and a "Section Headers:" line. Ignore those
        for(int i = 0; i<3; i++)
            s.nextLine();
        // 3rd row is text, that is including the labels row
        for(int i = 0; i<2; i++)
            s.nextLine();
        // Offset is 6 nexts in
        for(int i = 0; i<5; i++)
            s.next();
        output = s.nextInt(16);

//        System.out.println(headerSectionReport);
        System.out.printf("%08x", output);

        return output;
    }
}
