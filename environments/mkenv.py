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

    here = os.path.dirname(os.path.abspath(sys.argv[0]))
    envdir = os.path.join(here,name)

    if os.path.exists(envdir):
        sys.exit('An environment named "{0}" already exists. Program will exit.'.format(name))

    shutil.copytree(os.path.join(here,'..','aws'),envdir)

    with open(os.path.join(envdir,'samples','awscluster.json'), 'r') as f:
        env = json.load(f)

    env['EnvironmentName'] = name

    with open(os.path.join(envdir,'config','awscluster.json'),'w') as f:
        json.dump(env,f,indent=3)

    with open(os.path.join(here,name,'.gitignore'),'w') as f:
        f.write('*\n')
        f.write('!.gitignore\n')
        f.write('!ihg-keypair.pem\n')
        f.write('!aws_runtime.json\n')
        f.write('!ssh.py\n')

    origdir = os.getcwd()
    os.chdir(envdir)
    try:
        subprocess.check_call(['python','generateAWSCluster.py'])
        subprocess.check_call(['python','aws_provision_storage.py'])
        subprocess.check_call(['python','aws_provision.py'])
        subprocess.check_call(['python','setup.py'])
        subprocess.check_call(['ansible-playbook','--inventory=inventory.ini','setup_labs.yml'])
        subprocess.check_call(['ansible-playbook','--inventory=inventory.ini','install_cluster_config.yml'])
    finally:
        os.chdir(origdir)
