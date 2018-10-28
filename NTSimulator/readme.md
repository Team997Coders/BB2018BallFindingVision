# NTSimulator project

This project simulates a network tables server so that applications that write to network tables can be integration tested without a roboRio.

It publishes an application called ntserver as a pip package, located in `./build/python/dist`.  To build the package, use `gradlew build`.

# Dependencies
1. Python3

# Credits
Big kudos to [pynetworktables](https://github.com/robotpy/pynetworktables) authors.