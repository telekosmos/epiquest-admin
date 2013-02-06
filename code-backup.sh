#!/bin/sh
cd ~/Development/Projects/admtool
tar zcf admtool-jpop.tar.gz . --exclude admtool-jpop.tar.gz
scp admtool-jpop.tar.gz gcomesana@gredos:~/Development/backup/
mv -f admtool-jpop.tar.gz ..