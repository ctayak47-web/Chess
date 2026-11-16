"""
WinLocker - educational lock-screen simulator.
Opens 3 terminals that infinitely print the creator tag,
then shows a full-screen dark-green lock window.
Type the password (999) and press Enter to unlock.

This is an educational prank tool - it does NOT delete data,
damage the system, or exfiltrate anything.
"""

import os
import sys
import subprocess

CREATOR = "@swatlolz"
PASSWORD = "999"
BG_COLOR = "#004d00"
TEXT_COLOR = "#ffffff"


def spawn_terminals():
    """Spawn 3 background processes that infinitely print the creator tag."""
    code = (
        "import sys, time\n"
        "while True:\n"
        f"    sys.stdout.write('{CREATOR}\\n')\n"
        "    sys.stdout.flush()\n"
        "    time.sleep(0.05)\n"
    )
    for _ in range(3):
        try:
            subprocess.Popen(
                [sys.executable, "-c", code],
                stdout=subprocess.DEVNULL,
                stderr=subprocess.DEVNULL,
            )
        except Exception:
            pass


def main():
    spawn_terminals()

    # Use tkinter with a fallback approach
    try:
        import tkinter as tk
    except ImportError:
        # Fallback: print lock message to console
        print("WinLocker - type 999 to unlock")
        while True:
            try:
                inp = input("> ")
                if inp.strip() == PASSWORD:
                    print("Unlocked!")
                    sys.exit(0)
            except (EOFError, KeyboardInterrupt):
                sys.exit(0)

    root = tk.Tk()
    root.attributes("-fullscreen", True)
    root.configure(bg=BG_COLOR)
    root.title("WinLocker")
    root.protocol("WM_DELETE_WINDOW", lambda: None)

    lbl_creator = tk.Label(
        root, text=CREATOR, fg=TEXT_COLOR, bg=BG_COLOR,
        font=("Arial", 28, "bold"),
    )
    lbl_creator.pack(expand=True)

    lbl_prompt = tk.Label(
        root, text="Enter password:", fg=TEXT_COLOR, bg=BG_COLOR,
        font=("Arial", 18),
    )
    lbl_prompt.pack(pady=10)

    entry = tk.Entry(root, font=("Arial", 18), justify="center", width=20)
    entry.pack(pady=10)
    entry.focus_set()

    def check():
        if entry.get() == PASSWORD:
            root.destroy()
        else:
            entry.delete(0, tk.END)

    entry.bind("<Return>", lambda e: check())

    root.mainloop()


if __name__ == "__main__":
    main()
