#!/bin/bash
SSID=$1

sudo wpa_cli -i wlan0 remove_network 0
sudo wpa_cli -i wlan0 add_network 0
sudo wpa_cli -i wlan0 set_network 0 ssid \"${SSID}\"
sudo wpa_cli -i wlan0 set_network 0 scan_ssid 1
sudo wpa_cli -i wlan0 set_network 0 key_mgmt NONE
sudo wpa_cli -i wlan0 set_network 0 auth_alg OPEN
sudo wpa_cli -i wlan0 enable_network 0
