import sys


paragraphs = sys.stdin.read().split('\n\n')

current_numbers = list(map(int, paragraphs[0].split()[1:]))

maps = []

for paragraph in paragraphs[1:]:
    m = []
    for entry in paragraph.strip().split('\n')[1:]:
        dest, source, length = entry.strip().split()
        m.append({
            'source': int(source),
            'dest': int(dest),
            'length': int(length),
        })
    maps.append(m)

def transform(num, m):
    for entry in m:
        if num >= entry['source'] and num < entry['source'] + entry['length']:
            return num + entry['dest'] - entry['source']
    return num

for i in range(len(current_numbers)):
    for m in maps:
        current_numbers[i] = transform(current_numbers[i], m)

print(min(current_numbers))
