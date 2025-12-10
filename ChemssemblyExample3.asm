.data
CH4: .asciiz "CH4"
CH2: .asciiz "CH2"
.text

# Apply for 1000 grants (will be pulling your hair out by the end of this)
grnt 1000
# Start with a humble methane
new $t0, CH4
# Loop for making a molecule greater than 1000 g/mol
experiment:
# Keep adding methylenes to the chain
bnd $t0, CH2
# If our chain is now long enough (> 1000 g/mol), then branch to success
hyld $t0, $v1, success
# Otherwise, continue grinding
exp experiment
success:
# Make sure we have it
mol $t0
# Win that dang Nobel prize
wnp
# And just for the heck of it discover a new element
dne
# And don’t forget to clean :)
cln
