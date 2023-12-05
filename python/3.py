import re
import sys

grid = [line.strip() for line in sys.stdin.readlines()]
W = len(grid[0])
H = len(grid)

def is_symbol(ch):
    if ch == '.':
        return False
    if ch >= '0' and ch <= '9':
        return False
    if ch >= 'a' and ch <= 'z':
        return False
    if ch >= 'A' and ch <= 'Z':
        return False
    return True

def is_component(i, start, end):
    if i > 0:
        for j in range(max(0, start - 1), min(end + 1, W)):
            if is_symbol(grid[i-1][j]):
                return True
    if i < H-1:
        for j in range(max(0, start - 1), min(end + 1, W)):
            if is_symbol(grid[i+1][j]):
                return True
    if start > 0:
        if is_symbol(grid[i][start-1]):
            return True
    if end < W-1 and is_symbol(grid[i][end]):
        return True
    return False

sum = 0

for i, line in enumerate(grid):
    for match in re.finditer(r"(\d)+", line):
        if is_component(i, match.start(), match.end()):
            sum += int(match.group(0))

print(sum)