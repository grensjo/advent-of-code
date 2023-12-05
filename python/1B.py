import re
import sys

d = {
    'zero': 0,
    'one': 1,
    'two': 2,
    'three': 3,
    'four': 4,
    'five': 5,
    'six': 6,
    'seven': 7,
    'eight': 8,
    'nine': 9,
    '1': 1,
    '2': 2,
    '3': 3,
    '4': 4,
    '5': 5,
    '6': 6,
    '7': 7,
    '8': 8,
    '9': 9,
    '0': 0,
}

tot = 0
for line in sys.stdin.readlines():
    first = re.search(r"\d|one|two|three|four|five|six|seven|eight|nine", line)
    last = re.search(r"\d|eno|owt|eerht|ruof|evif|xis|neves|thgie|enin", line[::-1])
    tot += 10 * d[first.group(0)] + d[str(last.group(0)[::-1])]
print(tot)
