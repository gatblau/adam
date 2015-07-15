#!/usr/bin/env bash

ROOT="$PWD"
cd provision
sh fetch.sh
cd $ROOT/image
packer build adam-dev-centos71-vb.json
cd $ROOT
echo Vagrant Box created in image/output folder.