.data
CH4: .asciiz "CH4"
CH2: .asciiz "CH2"
.text

# Create a methane molecule in $t0
new $t0, CH4
# Add a methylene so now the molecule in $t0 is ethane
bnd $t0, CH2
mol $t0    # Should print out 30 g/mol
cln        # Clears registers, prints out message
