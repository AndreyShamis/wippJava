#!/bin/bash

watch -n 0.1 'dmesg | tail -15' > ./dmesg.log 2>&1
