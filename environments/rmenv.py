#!/usr/bin/python
import argparse
import json
import os
import os.path
import shutil
import subprocess
import sys

if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument("name", nargs=1)
    args = parser.parse_args()

    name = args.name[0]

    origdir =os.getcwd()
    try:
        here = os.path.dirname(os.path.abspath(sys.argv[0]))

        os.chdir(os.path.join(here,name))

        subprocess.check_call(['python', 'aws_destroy.py'])
        subprocess.check_call(['python', 'aws_destroy_storage.py'])

        shutil.rmtree(os.path.join(here,name))

    finally:
        os.chdir(origdir)
