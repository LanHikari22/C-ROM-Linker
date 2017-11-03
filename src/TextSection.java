public class TextSection {

    /**
     * // TODO: doc setupFunctions()
     *  The text section class will handle assigning actual ROM addresses to functions, determining their content,
     *  as well as replacing all function relative global variable references to their appropriate values in RAM.
     * @param objBuf
     * @param mmp
     * @param variables
     * @param functions
     */
    public static void setupFunctions(byte[] objBuf, MemoryMap mmp, Variable[] variables, Function[] functions) {
        // TODO: implement setupFunctions()
        // TODO: test setupFunctions()
        // Modify the object buffer so that all variable references relative to functions have the correct RAM references
        for(int i = 0; i < variables.length; i++){
            for(int j = 0; j < variables[i].RelocOffsets.length; j++){
                int relocOffset = mmp.getTextSectionOffset() + variables[i].RelocOffsets[j];
                // overwrite a the address in the objBuf
                byte[] address = new byte[4];
                address[0] = (byte)((variables[i].Address & 0x000000FF));
                address[1] = (byte)((variables[i].Address & 0x0000FF00) >> 8);
                address[2] = (byte)((variables[i].Address & 0x00FF0000) >> 16);
                address[3] = (byte)((variables[i].Address & 0xFF000000) >> 24); // TODO: check Order of operations
                for(int k = 0; k < 4; k++) // TODO: replace with apropriate call
                    objBuf[relocOffset + k] = address[k];
            }
        }

        for(int i = 0; i < functions.length; i++){
            // Set up the text injection address for each function
            functions[i].Address = mmp.getTextSegment().Address + functions[i].RelAddress;

            // Now copy the function contents from the object buffer into each function
            functions[i].Content = new byte[functions[i].Size];
            int startIndex = mmp.getTextSectionOffset() + functions[i].RelAddress;
            int endIndex = mmp.getTextSectionOffset() + functions[i].RelAddress + functions[i].Size;
            for(int j = startIndex; j < endIndex; j++)
                functions[i].Content[j - startIndex] = objBuf[j];
        }
    }
}
