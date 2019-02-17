java \
	-agentlib:jdwp=transport=dt_socket,server=y,address=8000,suspend=y \
	-jar build/libs/JavaMemoryGraph-all-0.1.jar \
	-hprof generate_hprof/heap.dump.windows.hprof 
