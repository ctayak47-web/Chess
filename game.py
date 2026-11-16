#!/usr/bin/env python3
"""
Snake Game - Classic arcade game using only Python standard library.
Run: python3 game.py
"""

import curses
import os
import random
import time
import sys


def main(stdscr):
    # Android has no terminfo database; set a basic terminal type
    if not os.environ.get("TERM"):
        os.environ["TERM"] = "xterm"
    curses.curs_set(0)                    # hide cursor
    stdscr.nodelay(1)                     # non-blocking input
    stdscr.timeout(100)                   # refresh every 100ms

    sh, sw = stdscr.getmaxyx()
    snake = [(sh // 2, sw // 2)]
    direction = curses.KEY_RIGHT
    food = place_food(snake, sh, sw)
    score = 0
    game_over = False

    while not game_over:
        key = stdscr.getch()

        if key in [curses.KEY_UP, curses.KEY_DOWN,
                   curses.KEY_LEFT, curses.KEY_RIGHT]:
            direction = key

        head_y, head_x = snake[0]
        if direction == curses.KEY_UP:
            new_head = (head_y - 1, head_x)
        elif direction == curses.KEY_DOWN:
            new_head = (head_y + 1, head_x)
        elif direction == curses.KEY_LEFT:
            new_head = (head_y, head_x - 1)
        elif direction == curses.KEY_RIGHT:
            new_head = (head_y, head_x + 1)

        # Wall collision
        if (new_head[0] <= 0 or new_head[0] >= sh - 1 or
                new_head[1] <= 0 or new_head[1] >= sw - 1):
            game_over = True
            break

        # Self collision
        if new_head in snake:
            game_over = True
            break

        snake.insert(0, new_head)

        if new_head == food:
            score += 1
            food = place_food(snake, sh, sw)
        else:
            snake.pop()

        stdscr.clear()
        draw_border(stdscr, sh, sw)
        draw_food(stdscr, food)
        draw_snake(stdscr, snake)
        draw_score(stdscr, score)
        stdscr.refresh()

    # Game over screen
    stdscr.clear()
    msg1 = "GAME OVER"
    msg2 = f"Score: {score}"
    msg3 = "Press Q to quit, R to restart"
    stdscr.addstr(sh // 2 - 1, (sw - len(msg1)) // 2, msg1,
                  curses.A_BOLD | curses.A_REVERSE)
    stdscr.addstr(sh // 2, (sw - len(msg2)) // 2, msg2)
    stdscr.addstr(sh // 2 + 1, (sw - len(msg3)) // 2, msg3)
    stdscr.refresh()

    while True:
        key = stdscr.getch()
        if key == ord('q'):
            break
        elif key == ord('r'):
            return main(stdscr)  # restart


def place_food(snake, sh, sw):
    while True:
        fy = random.randint(1, sh - 2)
        fx = random.randint(1, sw - 2)
        if (fy, fx) not in snake:
            return (fy, fx)


def draw_border(stdscr, sh, sw):
    for y in range(sh):
        stdscr.addch(y, 0, '#')
        stdscr.addch(y, sw - 1, '#')
    for x in range(sw):
        stdscr.addch(0, x, '#')
        stdscr.addch(sh - 1, x, '#')


def draw_food(stdscr, food):
    stdscr.addch(food[0], food[1], '*')


def draw_snake(stdscr, snake):
    for i, (y, x) in enumerate(snake):
        char = 'O' if i == 0 else 'o'
        stdscr.addch(y, x, char)


def draw_score(stdscr, score):
    score_str = f"Score: {score}"
    stdscr.addstr(0, 2, score_str)


if __name__ == '__main__':
    curses.wrapper(main)
