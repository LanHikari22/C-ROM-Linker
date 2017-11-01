public class Variable {
    public String Name;             /* Name of the variable */
    public int Size;                /* Size of the variable */
    public byte[] Value;            /* Buffer representing the value of the variable */
    public int RelAddress;          /* The variable's address in the data section */
    public int Address;             /* The actual address of the variable in RAM for the injected ROM */
    public Integer[] RelocOffsets;      /* Offset of the references used of the variable in the text section*/
}
