#! /bin/bash

# Kill Java process


kill -9 $(jps | grep "kafka-monitoring" | cut -d " " -f 1)
