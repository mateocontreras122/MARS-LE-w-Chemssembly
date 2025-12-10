.data
CH2O: .asciiz "CH2O"
C6H12O6: .asciiz "C6H12O6"
CH3: .asciiz "CH3"
.text

# Create formaldehyde in $t0 and glucose in $t1
new $t0, CH2O
new $t1, C6H12O6
# Mix molecules together into $t2
mix $t2, $t0, $t1
# Break off a single methyl group
brk $t2, CH3
# Start a reaction, shake the flask a little, and then add a catalyst
rxn
reaction:
shk
cat
# End the reaction, jump to success if rate of reaction > 1
erxn success
# If it wasn’t a success, then go back and shake it more
exp reaction
success:
# At the end, clean glassware
cln
