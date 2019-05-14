:: ----------------------
:: Javier Cabero 2019 (c)
::
:: Load tests the server.
::
:: Feel free to change
:: the file retrieved.
::
:: Tested on Windows 10.
:: ----------------------

start /b curl -s "http://localhost:52052/index.html?[1-100000]"
start /b curl -s "http://localhost:52052/index.html?[100001-200000]"
start /b curl -s "http://localhost:52052/index.html?[200001-300000]"
start /b curl -s "http://localhost:52052/index.html?[300001-400000]"
start /b curl -s "http://localhost:52052/index.html?[400001-500000]"
start /b curl -s "http://localhost:52052/index.html?[500001-600000]"
start /b curl -s "http://localhost:52052/index.html?[600001-700000]"
start /b curl -s "http://localhost:52052/index.html?[700001-800000]"
start /b curl -s "http://localhost:52052/index.html?[800001-900000]"
start /b curl -s "http://localhost:52052/index.html?[900001-1000000]"
start /b curl -s "http://localhost:52052/index.html?[1000001-1100000]"
start /b curl -s "http://localhost:52052/index.html?[1100001-1200000]"
start /b curl -s "http://localhost:52052/index.html?[1200001-1300000]"
start /b curl -s "http://localhost:52052/index.html?[1300001-1400000]"
start /b curl -s "http://localhost:52052/index.html?[1400001-1500000]"
start /b curl -s "http://localhost:52052/index.html?[1500001-1600000]"
start /b curl -s "http://localhost:52052/index.html?[1600001-1700000]"
start /b curl -s "http://localhost:52052/index.html?[1700001-1800000]"
start /b curl -s "http://localhost:52052/index.html?[1800001-1900000]"
start /b curl -s "http://localhost:52052/index.html?[1900001-2000000]"
