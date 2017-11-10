import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class MemoryMap {

    public int getROMAddress() { return ROMAddress; }

    public Segment getDataSegment(int segIndex) {
        return DataSegments[segIndex];
    }

    public int getDataSegmentsLength(){
        return DataSegments.length;
    }

    public Segment getTextSegment() {
        return TextSegment;
    }

    public Segment getRoDataSegment() { return RoDataSegment; }

    public int getObjDataSectionOffset() {
        return objDataSectionOffset;
    }

    public int getObjTextSectionOffset() {
        return objTextSectionOffset;
    }

    public Segment getObjRoDataSectionSegment() { return objRoDataSectionSegment; }


    /**
     * A memory segment in ROM or RAM. Consists of a start address, and a size.
     */
    public class Segment{
        public int Address;
        public int Size;
    }

    /**
     * The addresses for RAM are absolute, since are written to variables and since functions are modified to match
     * The addresses for ROM are relative to the actual ROM file, since they are used for injecting the functions into the ROM.
     */
    private int ROMAddress;
    private Segment TextSegment;
    private Segment RoDataSegment;
    private Segment[] DataSegments;
    private int objDataSectionOffset;
    private int objTextSectionOffset;
    private Segment objRoDataSectionSegment;

    public MemoryMap(String memoryMapPath, String headerSectionReport) throws IOException {
        // parse data and text section offsets
        objDataSectionOffset = parseDataSectionOffset(headerSectionReport);
        objTextSectionOffset = parseTextSectionOffset(headerSectionReport);
        objRoDataSectionSegment = parseRoDataSectionSegment(headerSectionReport);

        // Parses where the TEXT, RODATA, and DATA sections should be in the ROM/RAM.
        parseMMPFile(memoryMapPath);
    }

    private void parseMMPFile(String memoryMapPath) throws IOException {
        Scanner s = new Scanner(Paths.get(memoryMapPath));
        if(!s.nextLine().equals("ROM")) throw new IOException("Invalid mmp format: Expected ROM to come next.");

        // Parse the ROM address
        ROMAddress = s.nextInt(16);
        s.nextLine();
        if(!s.nextLine().equals("TEXT")) throw new IOException("Invalid mmp format: Expected TEXT to come next.");

        // Parse the Text Segment
        TextSegment = new Segment();
        TextSegment.Address = s.nextInt(16);
        TextSegment.Size = s.nextInt(10);
        s.nextLine();
        if(!s.nextLine().equals("RODATA")) throw new IOException("Invalid mmp format: Expected RODATA to come next.");

        // Parse the RoData Segment
        RoDataSegment = new Segment();
        RoDataSegment.Address = s.nextInt(16);
        RoDataSegment.Size = s.nextInt(10);
        s.nextLine();
        if(!s.nextLine().equals("DATA")) throw new IOException("Invalid mmp format: Expected DATA to come next.");

        // There can be variable segments for data, so parse them all
        ArrayList<Segment> dataArrayList = new ArrayList<>();
        while(s.hasNextLine()){
            Segment seg = new Segment();
            seg.Address = s.nextInt(16);
            seg.Size = s.nextInt(10);
            s.nextLine();
            dataArrayList.add(seg);
        }
        // Assign arraylist to DataSegments Array of segments
        DataSegments = new Segment[dataArrayList.size()];
        DataSegments = dataArrayList.toArray(DataSegments);

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
//        System.out.printf("%08x", output);

        return output;
    }

    private Segment parseRoDataSectionSegment(String headerSectionReport) {
        Segment output = new Segment();
        Scanner s = new Scanner(headerSectionReport);

        // there are 3 unnecessary lines: "There are 10..." line, blank line, and a "Section Headers:" line. Ignore those
        for(int i = 0; i<3; i++)
            s.nextLine();
        // 5th row is text, that is including the labels row
        for(int i = 0; i<4; i++)
            s.nextLine();
        // Offset is 6 nexts in
        for(int i = 0; i<5; i++)
            s.next();
        output.Address = s.nextInt(16);
        // Size is the next. Haha, get it?
        output.Size = s.nextInt(16);

//        System.out.println(headerSectionReport);
//        System.out.printf("%08x", output);

        return output;
    }
}
