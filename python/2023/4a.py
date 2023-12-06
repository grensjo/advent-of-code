import sys

sum = 0

for line in sys.stdin.readlines():
    _, card = line.split(':')
    winning_str, yours_str = card.split('|')
    winning = set([s.strip() for s in winning_str.strip().split()])
    yours = set([s.strip() for s in yours_str.strip().split()])
    both = winning & yours
    if len(both) > 0:
        sum += 2**(len(both) - 1)

print(sum)
