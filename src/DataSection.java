public class DataSection {
    /**
     * TODO doc setupVariables()
     * The data section class will handle assigning RAM addreses to the variables, as well as determining the content..
     * @param mmp
     * @param variables
     */
    public static void setupVariables(byte[] objBuf, MemoryMap mmp, Variable[] variables) {
        // TODO: test setupVariables() (Yay! It passed initial tests!)
        int segCsr = 0;
        int resourceCsr = 0;
        for(int i = 0; i < variables.length; i++){
            boolean configuredVariable = false;
            while(segCsr < mmp.getDataSegmentsLength() && !configuredVariable) {
                if (resourceCsr <= mmp.getDataSegment(segCsr).Address + mmp.getDataSegment(segCsr).Size - 4) {
                    // Set up the variable's address in RAM
                    variables[i].Address = mmp.getDataSegment(segCsr).Address + resourceCsr;

                    // Get the content of the variable in object file. It is at the variable's relative address.
                    variables[i].Value = new byte[variables[i].Size];
                    int startIndex = mmp.getObjDataSectionOffset() + variables[i].RelAddress;
                    int endIndex = startIndex + variables[i].Size;
                    for(int j = startIndex; j<endIndex; j++){
                        variables[i].Value[j - startIndex] = objBuf[j];
                    }

                    // Now that this variable has been processed, indicate so and advance the resourceCsr! This is 4 aligned.
                    configuredVariable = true;
                    resourceCsr += variables[i].Size;
                    resourceCsr += (4 - resourceCsr % 4) % 4;

                }
                else if (segCsr < mmp.getDataSegmentsLength()) {
                    segCsr++;
                    resourceCsr = 0;
                }
            }

            // We've run out of memory if the resourceCsr is at maximum, and we're in the last segment... WE HAVE RUN OUTTA MEMORY! REPORT!
            if(segCsr >= mmp.getDataSegmentsLength() ||
                    resourceCsr > mmp.getDataSegment(segCsr).Address + mmp.getDataSegment(segCsr).Size - 4){
                throw new OutOfMemoryError("ALL MEMORY RESOURCES HAVE BEEN DEPLETED");
            }
        }
    }
}
