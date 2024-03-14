#!/bin/bash

redis-cli -p 7001 shutdown
redis-cli -p 7002 shutdown
redis-cli -p 7003 shutdown


rm nodes-7001.conf
rm nodes-7002.conf
rm nodes-7003.conf
redis-cli shutdown

# Function to check if a port is open and kill the process using it
kill_port() {
    port=$1
    pid=$(lsof -ti tcp:$port)
    if [ ! -z "$pid" ]; then
        echo "Killing process using port $port"
        kill -9 $pid
    fi
}

# Kill ports if they are already in use
kill_port 7001
kill_port 7002
kill_port 7003

# Start Redis servers
redis-server --port 7001 --cluster-enabled yes --cluster-config-file nodes-7001.conf &
redis-server --port 7002 --cluster-enabled yes --cluster-config-file nodes-7002.conf &
redis-server --port 7003 --cluster-enabled yes --cluster-config-file nodes-7003.conf &

# Wait for Redis servers to start
sleep 10

# Add and create the cluster
redis-cli --cluster add-node 127.0.0.1:7001 127.0.0.1:7002 127.0.0.1:7003
redis-cli --cluster create 127.0.0.1:7001 127.0.0.1:7002 127.0.0.1:7003
