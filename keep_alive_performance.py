#!/usr/bin/env python

# Created by Javier Cabero on 05/14/2019.
#
# Executes requests without and with keep-alive
# headers and shows some performance metrics.
# 
# Feel free to change the url, port and request 
# to point to your running instance and target
# request.

import os
import time
import logging
import requests

logging.basicConfig(level=logging.INFO)

def new_connection_test(url, transactions):
  '''Using new TCP connection for each HTTP request.'''
  start_time = time.time()
  for i in range(transactions):
    response = requests.get(url, timeout=4)
  return time.time() - start_time

def keep_alive_test(url, transactions):
  '''Uses keep-alive for new connections.'''
  start_time = time.time()
  session = requests.Session() 
  for i in range(transactions):
    response = session.get(url, timeout=4)
  return time.time() - start_time

def main():
  url = "http://192.168.1.22:52052/index.html"
  transactions = 500
  print("Starting test...")
  print("New connection: %s" % new_connection_test(url, transactions))
  print("Keep alive: %s" % keep_alive_test(url, transactions))

if __name__ == "__main__":
  main()