#!/bin/bash

# Created by Javier Cabero on 05/14/2019.
#
# Executes requests without and with keep-alive
# headers and shows some performance metrics.
# Feel free to change the url, port and request 
# to point to your running instance and target
# request.

# Requests without keep-alive.
ab -n 5000 -c 15 192.168.1.22:52052/index.html

# Requests with keep-alive.
ab -k -n 5000 -c 15 192.168.1.22:52052/index.html