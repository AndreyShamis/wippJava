#!/bin/bash
ifconfig -a | grep HWaddr|awk '{print $1 " " $5}'
