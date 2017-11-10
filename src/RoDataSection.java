import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * This module handles changing the object buffer so that all read only references in functions are absolute references in RAM.
 */
public class RoDataSection {

    /**
     * Content of section
     */
    private byte[] RoData;

    public RoDataSection(MemoryMap mmp, byte[] objBuf, RelocationTable relocTable) {
        // TODO: implement RoDataSection
        // TODO: test RoDataSection
        Integer[] relocOffsets = relocTable.getRoDataRelocOffsets();
        for (int relocOffset : relocOffsets) {
            int absRelcOff = mmp.getObjTextSectionOffset() + relocOffset;
            int roAddress = (objBuf[absRelcOff]) + (objBuf[absRelcOff+1] << 8)
                    + (objBuf[absRelcOff+2] << 16) + (objBuf[absRelcOff+3] << 24);
            roAddress += mmp.getRoDataSegment().Address + mmp.getROMAddress(); // this makes the address absolute

            // overwrite the address in the objBuf
            byte[] address = new byte[4];
            address[0] = (byte) ((roAddress & 0x000000FF));
            address[1] = (byte) ((roAddress & 0x0000FF00) >> 8);
            address[2] = (byte) ((roAddress & 0x00FF0000) >> 16);
            address[3] = (byte) ((roAddress & 0xFF000000) >> 24); // TODO: check Order of operations
            for (int k = 0; k < 4; k++) { // TODO: replace with apropriate call
                objBuf[absRelcOff + k] = address[k];
            }
        }

        RoData = new byte[mmp.getObjRoDataSectionSegment().Size];
        // Obtain the RoData section to be injected into ROM
        for(int i = 0; i < mmp.getObjRoDataSectionSegment().Size; i++){
            RoData[i] = objBuf[mmp.getObjRoDataSectionSegment().Address + i];
        }
    }

    public void writeContentIntoFile(String filename) throws IOException{
        Files.write(Paths.get(filename), RoData);
    }

}
