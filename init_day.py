import click
from pathlib import Path


def solution_path(year: int, day: int):
    return Path.cwd() / 'kotlin' / 'solutions' / 'aockt' / f'y{year}' / f'Y{year}D{day:02d}.kt'


def test_path(year: int, day: int):
    return Path.cwd() / 'kotlin' / 'tests' / 'aockt' / f'y{year}' / f'Y{year}D{day:02d}Test.kt'


def input_folder(year: int, day: int):
    return Path.cwd() / 'kotlin' / 'inputs' / 'aockt' / f'y{year}' / f'd{day:02d}'


def copy_and_replace(src: Path, dst: Path, year: int, day: int):
    if dst.exists():
        click.echo(f'{dst} already exists, not overwriting.')
        return

    with open(src, 'r') as file:
        src_content = file.read()
    
    dst_content = src_content.replace(
            "Y9999D01", f'Y{year}D{day:02d}'
        ).replace(
            '9999, 1', f'{year}, {day}'
        ).replace(
            '9999', f'{year}'
        ).replace(
            'Example Puzzle', 'TODO'
        )

    with open(dst, 'w') as file:
        file.write(dst_content)


@click.command(help='Initializes Advent of Code Kotlin skeleton code for a specified year and day.')
@click.argument('year', type=click.INT)
@click.argument('day', type=click.INT)
def init_day(year, day):
    # Create folders
    solution_path(year, day).parent.mkdir(parents=True, exist_ok=True)
    test_path(year, day).parent.mkdir(parents=True, exist_ok=True)
    input_folder(year, day).mkdir(parents=True, exist_ok=True)

    # Copy solution and test templates
    copy_and_replace(solution_path(9999, 1), solution_path(year, day), year, day)
    copy_and_replace(test_path(9999, 1), test_path(year, day), year, day)


if __name__ == '__main__':
    init_day()
