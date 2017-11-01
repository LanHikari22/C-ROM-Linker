import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class MemoryMap {

    /**
     * A memory segment in ROM or RAM. Consists of a start address, and a size.
     */
    private class Segment{
        public int Address;
        public int Size;
    }

    public Segment[] Data;
    public Segment Text;

    public MemoryMap(String memoryMapPath) throws IOException {
        Scanner s = new Scanner(Paths.get(memoryMapPath));
        if(!s.nextLine().equals("TEXT")) throw new IOException("Invalid mmp format: Expected TEXT to be first line.");
        // Parse the Text Segment
        Text = new Segment();
        Text.Address = s.nextInt(16);
        Text.Size = s.nextInt(16);
        s.nextLine();
        if(!s.nextLine().equals("DATA")) throw new IOException("Invalid mmp format: Expected DATA to be the third line.");
        // There can be variable segments for data, so parse them all
        ArrayList<Segment> dataArrayList = new ArrayList<>();
        while(s.hasNextLine()){
            Segment seg = new Segment();
            seg.Address = s.nextInt(16);
            seg.Size = s.nextInt(16);
            s.nextLine();
            dataArrayList.add(seg);
        }
        // Assign arraylist to Data Array of segments
        Data = new Segment[dataArrayList.size()];
        Data = dataArrayList.toArray(Data);
    }
}
