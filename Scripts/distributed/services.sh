#!/bin/bash

ips=("10.17.5.53" "10.17.50.89" "10.17.50.201" "10.17.50.127" "10.17.50.228")
password="ghost2019"

start() {

	# Arguments:
	# $1 : Fullpath to Elasticsearch installation directory

	for ip in ${ips[*]}; do
		if [[ $ip = "10.17.5.53" ]]; then
			echo ${password} | sudo service cassandra start
			screen -d -S elastic -m  $1/bin/elasticsearch
		else
			echo ${password} | ssh -tt baadalvm@${ip} "sudo service cassandra start"
			ssh baadalvm@${ip} "screen -d -S elastic -m  $1/bin/elasticsearch"
		fi
		sleep 25s
	done
	sleep 15s
}

stop() {
	for ip in ${ips[*]}; do
		if [[ $ip = "10.17.5.53" ]]; then
			echo ${password} | sudo service cassandra stop
			screen -r elastic -X stuff ^C
		else
			echo ${password} | ssh -tt baadalvm@${ip} "sudo service cassandra stop"
			ssh baadalvm@${ip} "screen -r elastic -X stuff ^C"
		fi
	done
	sleep 10s
}

if [[ $1 = "start" ]]; then
	shift
	start $@ 
elif [[ $1 = "stop" ]]; then
	stop
else
	echo "Usage: services.sh [start | stop]"
fi