public class Function {
    public String Name;         /* Name of the function */
    public int Size;            /* Size of the function */
    public byte[] Content;      /* Buffer representing the value of the variable */
    public int RelAddress;      /* Relative address of the function in the text section */
    public int Address;         /* Address of function in ROM. All Functions must be maintained relative to each other address wise. */
}
