import click
import shutil
import subprocess
from pathlib import Path
from os import path


def get_path(year: int, day: int, part: int | None = None) -> Path:
    if part is None:
        return Path.cwd() / 'input' / f'{year}' / f'day{day:02d}.in'
    else:
        return Path.cwd() / 'input' / f'{year}' / f'day{day:02d}_p{part}.ans'


def get_kotlin_path(year: int, day: int, part: int | None = None) -> Path:
    if part is None:
        return Path.cwd() / 'kotlin' / 'inputs' / 'aockt' / f'y{year}' / f'd{day:02d}' / 'input.txt'
    else:
        return Path.cwd() / 'kotlin' / 'inputs' / 'aockt' / f'y{year}' / f'd{day:02d}' / f'solution_part{part}.txt'


def get_tmp_path():
    return Path.cwd() / 'input.txt'


def download_input(year: int, day: int) -> bool:
    cmd = subprocess.run(["aocdl", "-year", str(year), "-day", str(day)])
    return cmd.returncode == 0


def create_symlink(link_path, target_path):
    link_path.symlink_to(path.relpath(target_path, link_path.parent))


def fix_file(year: int, day: int, part: int | None = None):
    path = get_path(year, day, part)
    kotlin_path = get_kotlin_path(year, day, part)

    if path.exists() and kotlin_path.exists():
        click.echo(f"({year}, {day}, {part}) already exists in both places.")
    elif path.exists() and not kotlin_path.exists():
        create_symlink(kotlin_path, path)
        click.echo(f"({year}, {day}, {part}): created symlink in Kotlin directory.")
    elif not path.exists() and kotlin_path.exists():
        shutil.move(kotlin_path, path)
        create_symlink(kotlin_path, path)
        click.echo(f"({year}, {day}, {part}): moved file in Kotlin directory and replaced with symlink.")
    else:
        if part is None:
            if download_input(year, day):
                shutil.move(get_tmp_path(), path)
                create_symlink(kotlin_path, path)
                click.echo(f"({year}, {day}, {part}): downloaded input and created symlink.")
            else:
                click.echo(f"({year}, {day}, {part}): error, input download failed.")
        else:
            ans = click.prompt(f'Answer for {year} day {day} part {part}')
            with open(path, 'w') as f:
                print(ans, file=f)
            create_symlink(kotlin_path, path)
            

@click.command(help="""Initializes Advent of Code input and answer files and symlinks. Downloads input files using aocdl if not present.""")
@click.argument('year', type=click.INT)
@click.argument('day', type=click.INT)
def init_input(year, day):
    fix_file(year, day)
    fix_file(year, day, 1)
    fix_file(year, day, 2)

if __name__ == '__main__':
    init_input()
