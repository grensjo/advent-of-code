import re
import sys

def solve_grab(grab_string):
    R, G, B = 0, 0, 0
    for color_string in grab_string.split(','):
        number_string, color = color_string.strip().split(' ')
        if color == 'red':
            R = int(number_string)
        if color == 'green':
            G = int(number_string)
        if color == 'blue':
            B = int(number_string)
    return R, G, B

def solve_game(s):
    game_string, grabs_string = s.split(':')
    _, game_number = game_string.split(" ")
    R, G, B = 0, 0, 0
    for grab in grabs_string.split(';'):
        R1, G1, B1 = solve_grab(grab)
        R, G, B = max(R, R1), max(G, G1), max(B, B1)
    return R*G*B

print(sum(map(solve_game, sys.stdin.readlines())))