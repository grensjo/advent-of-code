import re
import sys

R, G, B = map(int, input("Total number of red,green,blue: ").strip().split(","))

def solve_grab(grab_string):
    for color_string in grab_string.split(','):
        number_string, color = color_string.strip().split(' ')
        if color == 'red' and int(number_string) > R:
            return False
        if color == 'green' and int(number_string) > G:
            return False
        if color == 'blue' and int(number_string) > B:
            return False
    return True

def solve_game(s):
    game_string, grabs_string = s.split(':')
    _, game_number = game_string.split(" ")
    return int(game_number) if all(map(solve_grab, grabs_string.split(';'))) else 0

print(sum(map(solve_game, sys.stdin.readlines())))