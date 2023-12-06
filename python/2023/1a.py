import sys

lines = sys.stdin.readlines()

tot = 0

for line in lines:
    numstring = ""
    for ch in line:
        if ch >= '0' and ch <= '9':
            numstring += ch
            break
    for ch in reversed(line):
        if ch >= '0' and ch <= '9':
            numstring += ch
            break
    tot += int(numstring)

print(tot)
