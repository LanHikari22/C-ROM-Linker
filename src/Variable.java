public class Variable {
    public String name;         /* Name of the variable */
    public int size;            /* Size of the variable */
    public byte[] Value;        /* Buffer representing the value of the variable */
    public int Address;         /* The actual address of the variable in RAM for the injected ROM */
    public int[] relocOffsets;  /* Offset of the references used of the variable in the text section*/
}
