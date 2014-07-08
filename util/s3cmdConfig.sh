#!/bin/sh
cd ~
git clone https://github.com/eucalyptus/s3cmd
cd s3cmd
python setup.py install
cd ~
