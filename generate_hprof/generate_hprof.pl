#!/usr/bin/env perl
use Cwd 'abs_path';
my $bin=abs_path($0);

system("javac src/Main.java");
my $pid = fork();
die if not defined $pid;
if (not $pid) {
	exec("java -cp src Main &");
}

# wait 10 seconds for the memory to load
sleep(10);
# generate the heap dump
system("jmap -dump:live,format=b,file=$bin/heap.dump.unix.hprof $pid");
kill($pid);
