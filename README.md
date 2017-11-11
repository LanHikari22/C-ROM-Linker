# C-ROM-Linker
This is a C Linker wrapper that essentially takes a relocatable combined object file of a C project and injects
all global/file static variables and functions from the object file into the ROM file!
You can compile and link your C project directly into a game ROM!
This is currently only supporting the ARM7TDMI architecture.

Limitations:
Check the issues section of the git repository. All limitations are reported as issues.

Dependencies:
Both python3 and the JVM are required, since this project uses java and python.
DevkitPro must also be intalled.
The project depends on this absolute path for the required binaries: "C:\devkitPro\devkitARM\bin"
This is also only usable on windows, so far. It has been developed on windows 10.

Usage:  
The linker accepts a ROM to modify, a combined relocatable object file, which represents the C project to inject
into the ROM, and a memory map configuration object. The memory map configuration object specifies the regions
of the text section in ROM, as well as the RAM sections the global variables are expected to be at.
The text section location is specified in a ROM-relative manner, but the memory must be specified relative to
what will be running this ROM. (ie, the text section addresses are relative to the file modified, but the variables
are actually loaded into the machine running the ROM. so they msut be absolute)

Check the Tests folder. It has a live demo. The makefile uses the ROM Linker inside the Default folder of MMBN6.
The src folder can be modified, and to build the project into the ROM, the command "make rom" needs to be specified
in the Default folder.