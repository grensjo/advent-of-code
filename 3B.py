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

gears = {}

def check_gear(row, start, end, i, j):
    if grid[i][j] != '*':
        return
    if (i, j) not in gears.keys():
        gears[(i, j)] = []
    gears[(i, j)].append(int(grid[row][start:end]))

def check_component(i, start, end):
    result = False
    if i > 0:
        for j in range(max(0, start - 1), min(end + 1, W)):
            check_gear(i, start, end, i-1, j)
    if i < H-1:
        for j in range(max(0, start - 1), min(end + 1, W)):
            check_gear(i, start, end, i+1, j)
    if start > 0:
        check_gear(i, start, end, i, start-1)
    if end < W-1:
        check_gear(i, start, end, i, end)

sum = 0

for i, line in enumerate(grid):
    for match in re.finditer(r"(\d)+", line):
        check_component(i, match.start(), match.end())

for gear, parts in gears.items():
    if len(parts) == 2:
        sum += parts[0] * parts[1]

print(sum)