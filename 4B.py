import sys

n_matches = []

for line in sys.stdin.readlines():
    _, card = line.split(':')
    winning_str, yours_str = card.split('|')
    winning = set([s.strip() for s in winning_str.strip().split()])
    yours = set([s.strip() for s in yours_str.strip().split()])
    both = winning & yours
    n_matches.append(len(both))

n_cards = [0]*len(n_matches)
for i in reversed(range(len(n_cards))):
    res = 1
    for j in range(n_matches[i]):
        res += n_cards[i+j+1]
    n_cards[i] = res

print(sum(n_cards))