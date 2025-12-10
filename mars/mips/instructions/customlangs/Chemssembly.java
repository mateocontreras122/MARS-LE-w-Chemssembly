    package mars.mips.instructions.customlangs;
    import mars.simulator.*;
    import mars.mips.hardware.*;
    import mars.mips.instructions.syscalls.*;
    import mars.*;
    import mars.util.*;
    import java.util.*;
    import java.io.*;
    import mars.mips.instructions.*;
    import java.util.Random;
    import java.util.Map;

public class Chemssembly extends CustomAssembly{
    private static final Map<String, Integer> MASS = Map.ofEntries(
            Map.entry("H",1), Map.entry("He",4), Map.entry("Li",7), Map.entry("Be",9),
            Map.entry("B",11), Map.entry("C",12), Map.entry("N",14), Map.entry("O",16),
            Map.entry("F",19), Map.entry("Ne",20), Map.entry("Na",23), Map.entry("Mg",24),
            Map.entry("Al",27), Map.entry("Si",28), Map.entry("P",31), Map.entry("S",32),
            Map.entry("Cl",35), Map.entry("Ar",40), Map.entry("K",39), Map.entry("Ca",40),
            Map.entry("Sc",45), Map.entry("Ti",48), Map.entry("V",51), Map.entry("Cr",52),
            Map.entry("Mn",55), Map.entry("Fe",56), Map.entry("Co",59), Map.entry("Ni",59),
            Map.entry("Cu",63), Map.entry("Zn",65), Map.entry("Ga",70), Map.entry("Ge",73),
            Map.entry("As",75), Map.entry("Se",79), Map.entry("Br",80), Map.entry("Kr",84),
            Map.entry("Rb",85), Map.entry("Sr",88), Map.entry("Y",89), Map.entry("Zr",91),
            Map.entry("Nb",93), Map.entry("Mo",96), Map.entry("Tc",98), Map.entry("Ru",101),
            Map.entry("Rh",103), Map.entry("Pd",106), Map.entry("Ag",108), Map.entry("Cd",112),
            Map.entry("In",115), Map.entry("Sn",119), Map.entry("Sb",122), Map.entry("Te",128),
            Map.entry("I",127), Map.entry("Xe",131), Map.entry("Cs",133), Map.entry("Ba",137),
            Map.entry("La",139), Map.entry("Ce",140), Map.entry("Pr",141), Map.entry("Nd",144),
            Map.entry("Pm",145), Map.entry("Sm",150), Map.entry("Eu",152), Map.entry("Gd",157),
            Map.entry("Tb",159), Map.entry("Dy",162), Map.entry("Ho",165), Map.entry("Er",167),
            Map.entry("Tm",169), Map.entry("Yb",173), Map.entry("Lu",175), Map.entry("Hf",178),
            Map.entry("Ta",181), Map.entry("W",184), Map.entry("Re",186), Map.entry("Os",190),
            Map.entry("Ir",192), Map.entry("Pt",195), Map.entry("Au",197), Map.entry("Hg",201),
            Map.entry("Tl",204), Map.entry("Pb",207), Map.entry("Bi",209), Map.entry("Po",209),
            Map.entry("At",210), Map.entry("Rn",222), Map.entry("Fr",223), Map.entry("Ra",226),
            Map.entry("Ac",227), Map.entry("Th",232), Map.entry("Pa",231), Map.entry("U",238),
            Map.entry("Np",237), Map.entry("Pu",244), Map.entry("Am",243), Map.entry("Cm",247),
            Map.entry("Bk",247), Map.entry("Cf",251), Map.entry("Es",252), Map.entry("Fm",257),
            Map.entry("Md",258), Map.entry("No",259), Map.entry("Lr",262), Map.entry("Rf",267),
            Map.entry("Db",270), Map.entry("Sg",271), Map.entry("Bh",270), Map.entry("Hs",277),
            Map.entry("Mt",276), Map.entry("Ds",281), Map.entry("Rg",280), Map.entry("Cn",285),
            Map.entry("Nh",284), Map.entry("Fl",289), Map.entry("Mc",288), Map.entry("Lv",293),
            Map.entry("Ts",294), Map.entry("Og",294)
            );

    public static int getMass(String symbol) {
        return MASS.getOrDefault(symbol, -1);
    }

    @Override
    public String getName(){
        return "Chemssembly";
    }

    @Override
    public String getDescription(){
        return "Assembly language to let your computer simulate chemistry synthesis and achieve any chemist's life-long dream: win a Nobel prize";
    }

    @Override
    protected void populate(){
        // 1. Create New Molecule
        instructionList.add(
                new BasicInstruction("new $t0, label",
            	 "Create New Molecule : takes in a register to put the molecule in, and uses a label to take in the molecule (ex. CH3, CO2, H2O)",
                BasicInstructionFormat.I_FORMAT,
                "001000 fffff 00000 ssssssssssssssss",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                      int[] operands = statement.getOperands();
                      if (RegisterFile.getValue(operands[0]) != 0) {
                          SystemIO.printString("Flask is not empty, clean your glassware!\n");
                      }
                      else {
                          char ch = 0;
                          // Get the name of the label from the token list
                          String label = statement.getOriginalTokenList().get(2).getValue();
                          // Look up the label in the program symbol table to get its address
                          int byteAddress = Globals.program.getLocalSymbolTable().getAddressLocalOrGlobal(label);

                          try
                          {
                              ch = (char) Globals.memory.getByte(byteAddress);
                              String mol = "";
                              // won't stop until NULL byte reached!
                              while (ch != 0) {
                                  mol = mol + ch;
                                  byteAddress++;
                                  ch = (char) Globals.memory.getByte(byteAddress);
                              }

                              int total = 0;
                              for (int i = 0; i < mol.length(); ) {
                                  char c = mol.charAt(i);
                                  String symbol = "" + c;
                                  i++;

                                  // Checks for two-letter elements (He, Li, Be, etc.)
                                  if (i < mol.length() && Character.isLowerCase(mol.charAt(i))) {
                                      symbol += mol.charAt(i);
                                      i++;
                                  }

                                  // Reads the subscript or amount of that molecule (H2, C10, etc.)
                                  int num = 0;
                                  while (i < mol.length() && Character.isDigit(mol.charAt(i))) {
                                      num = num * 10 + (mol.charAt(i) - '0');
                                      i++;
                                  }
                                  // If no subscript, assume there's only one of that element
                                  if (num == 0) {
                                      num = 1;
                                  }

                                  // Use getMass function to get the molar mass of the element
                                  int mass = getMass(symbol);
                                  if (mass == -1) {
                                      throw new IllegalArgumentException("Unknown element: " + symbol);
                                  }

                                  total += mass * num;
                              }

                              SystemIO.printString("Molar mass of the new molecule: " + total + " g/mol\n");
                              RegisterFile.updateRegister(operands[0], total);
                          }
                          catch (AddressErrorException e) {
                              throw new ProcessingException(statement, e);
                          }
                      }
                  }
               }));
        // 2. Form Bond
        instructionList.add(
                new BasicInstruction("bnd $t0, label",
                        "Form Bond : takes in a register of the molecule you want to bond an atom(s) to, uses a label to take in the atom(s) you want to add to the molecule (ex. C, H, N, O, F, or another molecule)",
                        BasicInstructionFormat.I_FORMAT,
                        "000010 fffff 00000 ssssssssssssssss",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                int[] operands = statement.getOperands();
                                if (RegisterFile.getValue(operands[0]) == 0) {
                                    SystemIO.printString("Flask is empty, nothing to bond to!\n");
                                }
                                else {
                                    char ch = 0;
                                    // Get the name of the label from the token list
                                    String label = statement.getOriginalTokenList().get(2).getValue();
                                    // Look up the label in the program symbol table to get its address
                                    int byteAddress = Globals.program.getLocalSymbolTable().getAddressLocalOrGlobal(label);

                                    try
                                    {
                                        ch = (char) Globals.memory.getByte(byteAddress);
                                        String mol = "";
                                        // won't stop until NULL byte reached!
                                        while (ch != 0) {
                                            mol = mol + ch;
                                            byteAddress++;
                                            ch = (char) Globals.memory.getByte(byteAddress);
                                        }

                                        int total = 0;
                                        for (int i = 0; i < mol.length(); ) {
                                            char c = mol.charAt(i);
                                            String symbol = "" + c;
                                            i++;

                                            // Checks for two-letter elements (He, Li, Be, etc.)
                                            if (i < mol.length() && Character.isLowerCase(mol.charAt(i))) {
                                                symbol += mol.charAt(i);
                                                i++;
                                            }

                                            // Reads the subscript or amount of that molecule (H2, C10, etc.)
                                            int num = 0;
                                            while (i < mol.length() && Character.isDigit(mol.charAt(i))) {
                                                num = num * 10 + (mol.charAt(i) - '0');
                                                i++;
                                            }
                                            // If no subscript, assume there's only one of that element
                                            if (num == 0) {
                                                num = 1;
                                            }

                                            // Use getMass function to get the molar mass of the element
                                            int mass = getMass(symbol);
                                            if (mass == -1) {
                                                throw new IllegalArgumentException("Unknown element: " + symbol);
                                            }

                                            total += mass * num;
                                        }
                                        total = total + RegisterFile.getValue(operands[0]);
                                        RegisterFile.updateRegister(operands[0], total);
                                        SystemIO.printString("The bond increased the molar mass of the molecule to: " + total + " g/mol\n");
                                    }
                                    catch (AddressErrorException e) {
                                        throw new ProcessingException(statement, e);
                                    }
                                }
                            }
                        }));
        // 3. Mix Molecules
        instructionList.add(
                new BasicInstruction("mix $t0,$t1,$t2",
            	 "Mix Molecules : takes in two registers of the molecules you want to mix together, $t1 and $t2, then puts the result into $t0",
                BasicInstructionFormat.R_FORMAT,
                "000000 sssss ttttt fffff 00000 000011",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     int mol1 = RegisterFile.getValue(operands[1]);
                     int mol2 = RegisterFile.getValue(operands[2]);
                     int flask = operands[0] - 8;
                     int result = mol1 + mol2;
                     // Put the new mass in the register
                     RegisterFile.updateRegister(operands[0], result);
                     // Get rid of the old masses (transferred those molecules to a new flask)
                     RegisterFile.updateRegister(operands[1], 0);
                     RegisterFile.updateRegister(operands[2], 0);
                     SystemIO.printString("The new mixture in flask $t" + flask + " has a molar mass of: " + result + " g/mol\n");
                  }
               }));
        // 4. Break Bond
        instructionList.add(
                new BasicInstruction("brk $t0, label",
                        "Break Bond : takes in a register of the molecule you want to break bonds with atom(s), then uses a label to take in the atom(s) you want to remove from the molecule",
                        BasicInstructionFormat.I_FORMAT,
                        "000100 fffff 00000 ssssssssssssssss",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                int[] operands = statement.getOperands();
                                if (RegisterFile.getValue(operands[0]) == 0) {
                                    SystemIO.printString("Flask is empty, no bonds to break!\n");
                                }
                                else {
                                    char ch = 0;
                                    // Get the name of the label from the token list
                                    String label = statement.getOriginalTokenList().get(2).getValue();
                                    // Look up the label in the program symbol table to get its address
                                    int byteAddress = Globals.program.getLocalSymbolTable().getAddressLocalOrGlobal(label);

                                    try
                                    {
                                        ch = (char) Globals.memory.getByte(byteAddress);
                                        String mol = "";
                                        // won't stop until NULL byte reached!
                                        while (ch != 0) {
                                            mol = mol + ch;
                                            byteAddress++;
                                            ch = (char) Globals.memory.getByte(byteAddress);
                                        }

                                        int total = 0;
                                        for (int i = 0; i < mol.length(); ) {
                                            char c = mol.charAt(i);
                                            String symbol = "" + c;
                                            i++;

                                            // Checks for two-letter elements (He, Li, Be, etc.)
                                            if (i < mol.length() && Character.isLowerCase(mol.charAt(i))) {
                                                symbol += mol.charAt(i);
                                                i++;
                                            }

                                            // Reads the subscript or amount of that molecule (H2, C10, etc.)
                                            int num = 0;
                                            while (i < mol.length() && Character.isDigit(mol.charAt(i))) {
                                                num = num * 10 + (mol.charAt(i) - '0');
                                                i++;
                                            }
                                            // If no subscript, assume there's only one of that element
                                            if (num == 0) {
                                                num = 1;
                                            }

                                            // Use getMass function to get the molar mass of the element
                                            int mass = getMass(symbol);
                                            if (mass == -1) {
                                                throw new IllegalArgumentException("Unknown element: " + symbol);
                                            }

                                            total += mass * num;
                                        }
                                        int newMass = RegisterFile.getValue(operands[0]) - total;
                                        if (newMass < 0) {
                                            SystemIO.printString("Cannot break off more atoms than are in the molecule!\n");
                                        }
                                        else {
                                            RegisterFile.updateRegister(operands[0], newMass);
                                            SystemIO.printString("The broken bond decreased the molar mass of the molecule to: " + newMass + " g/mol\n");
                                        }
                                    }
                                    catch (AddressErrorException e) {
                                        throw new ProcessingException(statement, e);
                                    }
                                }
                            }
                        }));
        // 5. Separate Molecules
        instructionList.add(
                new BasicInstruction("sep $t0,$t1,$t2",
                        "Separate Molecules : takes in a register of two molecules you want to separate, $t1 and $t2, and puts the separation (difference) into $t0",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 fffff sssss ttttt 00000 000101",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                int[] operands = statement.getOperands();
                                int mol1 = RegisterFile.getValue(operands[1]);
                                int mol2 = RegisterFile.getValue(operands[2]);
                                int flask = operands[0] - 8;
                                int result = mol1 - mol2;
                                // Put the new mass in the register
                                RegisterFile.updateRegister(operands[0], result);
                                // Get rid of the old masses (transferred those molecules to a new flask)
                                RegisterFile.updateRegister(operands[1], 0);
                                RegisterFile.updateRegister(operands[2], 0);
                                SystemIO.printString("The separation in flask $t" + flask + " has a molar mass of: " + result + " g/mol\n");
                            }
                        }));
        // 6. Experiment
        instructionList.add(
                new BasicInstruction("exp label",
            	 "Experiment : takes in a label of an address you want to experiment on, unconditionally",
                BasicInstructionFormat.I_BRANCH_FORMAT,
                "000110 00000 00000 ffffffffffffffff",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                      int[] operands = statement.getOperands();
                      SystemIO.printString("Initiating the experiment.\n");
                      Globals.instructionSet.processBranch(operands[0]);
                  }
                           
               }));
        // 7. Hypothesize
        instructionList.add(
                new BasicInstruction("hyp $t0, $t1, label",
                        "Hypothesize : branches to the specified label if your hypothesis is correct and two molecules, $t0 and $t1, have the same molar mass (total mass of atoms in the molecule)",
                        BasicInstructionFormat.I_BRANCH_FORMAT,
                        "000111 fffff ttttt ssssssssssssssss",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                int[] operands = statement.getOperands();

                                if (RegisterFile.getValue(operands[0]) == RegisterFile.getValue(operands[1]))
                                {
                                    SystemIO.printString("Eureka!\n");
                                    Globals.instructionSet.processBranch(operands[2]);
                                }
                            }
                        }));
        // 8. Peer-Reviewed Critique
        instructionList.add(
                new BasicInstruction("prc $t0, $t1, label",
                        "Peer-Reviewed Critique : branches to the specified label if your hypothesis wasn't as correct, $t0 and $t1 don't have the same molar mass (the peer reviewers caught on to your mistake)",
                        BasicInstructionFormat.I_BRANCH_FORMAT,
                        "001000 fffff ttttt ssssssssssssssss",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                int[] operands = statement.getOperands();

                                if (RegisterFile.getValue(operands[0]) != RegisterFile.getValue(operands[1]))
                                {
                                    SystemIO.printString("The critics are always so harsh...\n");
                                    Globals.instructionSet.processBranch(operands[2]);
                                }
                            }
                        }));
        // 9. High-Yield
        instructionList.add(
                new BasicInstruction("hyld $t0, $t1, label",
                        "High-Yield : branches to the specified label if the $t0 molecule has a higher molar mass than $t1, meaning your reaction did very well (good job!)",
                        BasicInstructionFormat.I_BRANCH_FORMAT,
                        "001001 fffff ttttt ssssssssssssssss",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                int[] operands = statement.getOperands();

                                if (RegisterFile.getValue(operands[0]) > RegisterFile.getValue(operands[1]))
                                {
                                    SystemIO.printString("IT WORKED!\n");
                                    Globals.instructionSet.processBranch(operands[2]);
                                }
                            }
                        }));
        // 10. Low-Yield
        instructionList.add(
                new BasicInstruction("lyld $t0, $t1, label",
                        "Low-Yield : branches to the specified label if the $t0 molecule has a lower molar mass than $t1, meaning your reaction did very poorly :(",
                        BasicInstructionFormat.I_BRANCH_FORMAT,
                        "001010 fffff ttttt ssssssssssssssss",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                int[] operands = statement.getOperands();

                                if (RegisterFile.getValue(operands[0]) < RegisterFile.getValue(operands[1]))
                                {
                                    SystemIO.printString("Back to the drawing board...\n");
                                    Globals.instructionSet.processBranch(operands[2]);
                                }
                            }
                        }));
        // 11. Start Reaction
        instructionList.add(
                new BasicInstruction("rxn",
                        "Start Reaction : starts a reaction in your fume hood that starts off with a base rate of reaction of 1, stored in $v0",
                        BasicInstructionFormat.R_FORMAT,
                        "00000 00000 00000 00000 000000 001011",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                RegisterFile.updateRegister(2, 1);
                                SystemIO.printString("Started a reaction!\n");
                            }
                        }));

        // 12. Catalyze
        instructionList.add(
                new BasicInstruction("cat",
                        "Catalyze : adds a catalyst to your reaction flask, effectively doubling the rate of reaction value",
                        BasicInstructionFormat.R_FORMAT,
                        "00000 00000 00000 00000 000000 001100",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                int rr = RegisterFile.getValue(2) * 2;
                                RegisterFile.updateRegister(2, rr);
                                SystemIO.printString("Added a catalyst to the reaction, rate of reaction is now: " + rr + " M/s\n");
                            }
                        }));
        // 13. Quench
        instructionList.add(
                new BasicInstruction("qnch",
                        "Quench : adds a quencher to your reaction flask, effectively halving the rate of reaction value",
                        BasicInstructionFormat.R_FORMAT,
                        "00000 00000 00000 00000 000000 001101",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                int rr = RegisterFile.getValue(2) / 2;
                                RegisterFile.updateRegister(2, rr);
                                SystemIO.printString("Quenched the reaction, rate of reaction is now: " + rr + " M/s\n");
                            }
                        }));
        // 14. Shake Flask
        instructionList.add(
                new BasicInstruction("shk",
                        "Shake Flask : shakes your flask, and adds a random value of rate of reaction from 1-5",
                        BasicInstructionFormat.R_FORMAT,
                        "00000 00000 00000 00000 000000 001110",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                Random random = new Random();
                                int roll = random.nextInt(4) + 1;
                                int rr = RegisterFile.getValue(2) + roll;
                                RegisterFile.updateRegister(2, rr);
                                SystemIO.printString("Shook the reaction flask, rate of reaction is now: " + rr + " M/s\n");
                            }
                        }));
        // 15. End Reaction
        instructionList.add(
                new BasicInstruction("erxn label",
                        "End Reaction : ends your reaction, and branches to the label if the rate of reaction is greater or equal to 1",
                        BasicInstructionFormat.I_BRANCH_FORMAT,
                        "001111 00000 00000 ffffffffffffffff",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                int[] operands = statement.getOperands();
                                int rr = RegisterFile.getValue(2);
                                if (rr >= 1) {
                                    Globals.instructionSet.processBranch(operands[0]);
                                    SystemIO.printString("Reaction was successfully stopped with a rate of reaction of " + rr + " M/s\n");
                                }
                                else {
                                    SystemIO.printString("Reaction failed, had a rate of only " + rr + " M/s\n");
                                }
                            }
                        }));
        // 16. Clean Glassware
        instructionList.add(
                new BasicInstruction("cln",
                "Clean Glassware : simulates cleaning glassware (thankfully it's just a simulation), and empties all flasks (registers) which have molecules in it",
            	 BasicInstructionFormat.R_FORMAT,
                "00000 00000 00000 00000 000000 010000",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int cleaned = 0;
                     // Iterate through every $t register
                     for (int i = 8; i < 16; i++){
                        int val = RegisterFile.getValue(i);
                        if (val > 0){
                            RegisterFile.updateRegister(i, 0);
                            cleaned++;
                        }
                     }
                     SystemIO.printString("Cleaned glassware for " + cleaned + " hour(s).\n");
                  }
               }));
        // 17. Calculate Molar Mass
        instructionList.add(
                new BasicInstruction("mol $t0",
                        "Calculate Molar Mass : prints out the molar mass that is stored in the register $t0 for that specific molecule",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 fffff 00000 00000 00000 010001",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                int[] operands = statement.getOperands();
                                int operand = RegisterFile.getValue(operands[0]);
                                int flask = operands[0] - 8;
                                SystemIO.printString("Molar mass in flask $t" + flask + " is: " + operand + " g/mol\n");
                            }
                        }));
        // 18. Apply For Grants
        instructionList.add(
                new BasicInstruction("grnt -100",
                        "Apply For Grants : takes in an immediate, which is how many grants you are applying for, stores amount of grants in $v1",
                        BasicInstructionFormat.I_FORMAT,
                        "010010 00000 00000 ffffffffffffffff",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                int[] operands = statement.getOperands();
                                int grants = operands[0];
                                RegisterFile.updateRegister(3, grants);
                                SystemIO.printString("Applied for " + grants + " grants.\n");
                            }
                        }));
        // 19. Win Nobel Prize
        instructionList.add(
                new BasicInstruction("wnp",
                        "Win Nobel Prize : you will win the Nobel prize if the total molar mass you’ve synthesized in all registers adds up to more than 1000 g/mol, so make sure you DON’T clean your glassware (not recommended)",
                        BasicInstructionFormat.R_FORMAT,
                        "00000 00000 00000 00000 000000 010011",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                int totalMass = 0;
                                // Iterate through every $t register
                                for (int i = 8; i < 16; i++){
                                    int val = RegisterFile.getValue(i);
                                    if (val > 0){
                                        totalMass += val;
                                    }
                                }

                                if (totalMass >= 1000) {
                                    SystemIO.printString("You won the Nobel prize for synthesizing " + totalMass + " g/mol worth of molecules!\n");
                                }
                                else {
                                    SystemIO.printString("You only synthesized " + totalMass + " g/mol worth of molecules, better luck next year...\n");
                                }
                            }
                        }));
        // 20. Discover New Element
        instructionList.add(
                new BasicInstruction("dne",
                        "Discover New Element : rolls for a randomly named element (funny name + ium) and prints the element you’ve synthesized",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 00000 00000 00000 00000 010100",
                        new SimulationCode()
                        {

                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                // roll for a random number
                                Random random = new Random();
                                int roll = random.nextInt(6);

                                // print element discovered based on roll
                                switch (roll){
                                    case 0:
                                        SystemIO.printString("You discovered adamantium!\n");
                                        break;
                                    case 1:
                                        SystemIO.printString("You discovered vibranium!\n");
                                        break;
                                    case 2:
                                        SystemIO.printString("You discovered pyrholidon!\n");
                                        break;
                                    case 3:
                                        SystemIO.printString("You discovered thirium!\n");
                                        break;
                                    case 4:
                                        SystemIO.printString("You discovered unobtanium!\n");
                                        break;
                                    case 5:
                                        SystemIO.printString("You discovered orichalcum!\n");
                                        break;
                                    default:
                                        SystemIO.printString("You discovered mythril!\n");
                                }
                            }
                        }));

    }
}