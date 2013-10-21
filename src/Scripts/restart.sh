#!/bin/bash

sudo killall wpa_supplicant

sudo modprobe -r iwlmvm
sudo modprobe -r iwlwifi
sudo modprobe iwlwifi
#sudo modprobe iwlmvm
sleep 2

sudo wpa_supplicant -d -i wlan0 -c /etc/wpa_supplicant/wpa_supplicant.conf -B -f /tmp/wpaSupplicant.log 
