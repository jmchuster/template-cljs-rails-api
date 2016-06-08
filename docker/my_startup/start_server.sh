#!/bin/sh

cd /home/app/api && bundle exec puma -b unix:/tmp/sockets/puma.sock
