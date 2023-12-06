import sys
import pprint
pp = pprint.PrettyPrinter(indent=4)

MAX_INT = 4_294_967_295

paragraphs = sys.stdin.read().split('\n\n')
seeds = list(map(int, paragraphs[0].split()[1:]))

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

# Fill in the gaps in all the maps to reduce the number of special cases to consider.
for m in maps:
    m.sort(key = lambda item: item['source'])
    to_add = []
    if m[0]['source'] > 0:
        to_add.append({'source': 0, 'dest': 0, 'length': m[0]['source']})
    for entry1, entry2 in zip(m, m[1:]):
        if entry1['source'] + entry1['length'] < entry2['source']:
            to_add.append({
                'source': entry1['source'] + entry1['length'],
                'dest': entry1['source'] + entry1['length'],
                'length': entry2['source'] - (entry1['source'] + entry1['length']),
            })
    to_add.append({
        'source': m[-1]['source'] + m[-1]['length'],
        'dest': m[-1]['source'] + m[-1]['length'],
        'length': MAX_INT,
    })
    m.extend(to_add)
    m.sort(key=lambda item: item['source'])

def transform_range(start, length, i):
    if i >= len(maps):
        return start
    if length <= 0:
        return MAX_INT

    best = MAX_INT

    for entry in maps[i]:
        # (start, start+length) is entirely after entry
        if start >= entry['source'] + entry['length']:
            continue
        # (start, start+length) is entirely before entry
        if entry['source'] >= start + length:
            break

        # There is an overlap, compute it and recurse.
        overlap_start = max(start, entry['source'])
        overlap_end = min(start + length, entry['source'] + entry['length'])
        overlap_length = overlap_end - overlap_start
        delta = entry['dest'] - entry['source']
        best = min(best, transform_range(overlap_start + delta, overlap_length, i + 1))

    return best

best = MAX_INT
for start, length in zip(seeds[::2], seeds[1::2]):
    best = min(best, transform_range(start, length, 0))
print(best)
