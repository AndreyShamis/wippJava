#!/bin/bash

PASSWORD=$2
SSID=$1
PROTO=$3
SECURITY=$4
sudo wpa_cli -i wlan0 remove_network 0
sudo wpa_cli -i wlan0 add_network 0
sudo wpa_cli -i wlan0 set_network 0 ssid \"${SSID}\"
sudo wpa_cli -i wlan0 set_network 0 scan_ssid 1
sudo wpa_cli -i wlan0 set_network 0 proto ${PROTO}
sudo wpa_cli -i wlan0 set_network 0 key_mgmt WPA-PSK
sudo wpa_cli -i wlan0 set_network 0 auth_alg OPEN
sudo wpa_cli -i wlan0 set_network 0 group ${SECURITY}
sudo wpa_cli -i wlan0 set_network 0 pairwise ${SECURITY}
sudo wpa_cli -i wlan0 set_network 0 psk \"${PASSWORD}\"
sudo wpa_cli -i wlan0 enable_network 0
sleep 4
sudo dhclient -r wlan0
sudo dhclient wlan0
