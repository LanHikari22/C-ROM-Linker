public class TextSection {

    /**
     * // TODO: doc setupFunctions()
     * The text section class will handle assigning actual ROM addresses to functions, determining their content,
     * as well as replacing all function relative global variable references to their appropriate values in RAM.
     * It will also replace all dummy BL calls.
     * @param objBuf
     * @param mmp
     * @param variables
     * @param functions
     */
    public static void setupFunctions(byte[] objBuf, MemoryMap mmp, Variable[] variables, Function[] functions) {
        // TODO: implement setupFunctions()
        // TODO: test setupFunctions()
        // Modify the object buffer so that all variable references relative to functions have the correct RAM references
        for (Variable variable : variables) {
            for (int j = 0; j < variable.RelocOffsets.length; j++) {
                int relocOffset = mmp.getObjTextSectionOffset() + variable.RelocOffsets[j];
                // overwrite the address in the objBuf
                byte[] address = new byte[4];
                address[0] = (byte) ((variable.Address & 0x000000FF));
                address[1] = (byte) ((variable.Address & 0x0000FF00) >> 8);
                address[2] = (byte) ((variable.Address & 0x00FF0000) >> 16);
                address[3] = (byte) ((variable.Address & 0xFF000000) >> 24); // TODO: check Order of operations
                for (int k = 0; k < 4; k++) // TODO: replace with apropriate call
                    objBuf[relocOffset + k] = address[k];
            }
        }

        // Modify the object buffer so that all dummy function calls are replaced with actual calls
        for (Function function : functions) {
            for (int j = 0; j < function.RelocOffsets.length; j++) {
                int relocOffset = mmp.getObjTextSectionOffset() + function.RelocOffsets[j];
                // construct the BL instruction to overwrite the dummy BL with
                byte[] instruction = constructBLInstruction(function, function.RelocOffsets[j]);
                // overwrite the instruction in the objBuf
                for (int k = 0; k < instruction.length; k++)
                    objBuf[relocOffset + k] = instruction[k];
            }
        }

        for (Function function : functions) {
            // Set up the text injection address for each function
            function.Address = mmp.getTextSegment().Address + function.RelAddress;

            // Now copy the function contents from the object buffer into each function
            function.Content = new byte[function.Size];
            int startIndex = mmp.getObjTextSectionOffset() + function.RelAddress;
            int endIndex = mmp.getObjTextSectionOffset() + function.RelAddress + function.Size;
            for (int j = startIndex; j < endIndex; j++)
                function.Content[j - startIndex] = objBuf[j];
        }
    }

    /**
     * TODO doc constructBLInstruction
     * This is a very important function, as it may have to be modified depending on architecture.
     * Here is an explanation of the algorithm used for the GBA ARM Architecture:
     * Assume you are at address 0x08900030
     * [-2] 0xEBFFFFFE -> bl #0x08900030
     * [-1] 0xEBFFFFFF -> bl #0x08900034
     * [00] 0xEB000000 -> bl #0x08900038
     * [+1] 0xEB000001 -> bl #0x0890003C
     * [+2] 0xEB000002 -> bl #0x08900040
     *
     * And We want to go to 0x08900068
     * 0x08900068 - 0x08900030 = 56. It is 56 bytes ahead. 56 bytes away from -2 (here).
     * Every step is 4 bytes, so let's go to (56/4) - 2 = 12
     * [54] 0xEB00000C -> bl #0x08900068
     *
     * Generally, The algorithm is:
     * uint32_t offset = (destOffset - currOffset)/4 - 2;
     * inst = 0xEB000000 | (offset & 0x00FFFFFF);
     * @param function
     * @param relocOffset
     * @return
     */
    private static byte[] constructBLInstruction(Function function, int relocOffset) {
        long offset = (function.RelAddress - relocOffset) / 4 - 2;
        long instruction = 0xEB000000 | (offset & 0x00FFFFFF);
        byte[] output = new byte[4];
        output[0] = (byte)((instruction & 0x000000FF));
        output[1] = (byte)((instruction & 0x0000FF00) >> 8);
        output[2] = (byte)((instruction & 0x00FF0000) >> 16);
        output[3] = (byte)((instruction & 0xFF000000) >> 24);

        return output;
    }
}
