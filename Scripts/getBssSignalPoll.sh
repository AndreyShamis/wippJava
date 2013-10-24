#!/bin/bash

INTERFACE=$1
sudo wpa_cli -i ${INTERFACE} signal_poll
