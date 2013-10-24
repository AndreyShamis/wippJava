#!/bin/bash

PASSWORD=$2
SSID=$1

sudo wpa_cli -i wlan0 add_network 0
sudo wpa_cli -i wlan0 set_network 0 ssid \"${SSID}\"
sudo wpa_cli -i wlan0 set_network 0 scan_ssid 1
sudo wpa_cli -i wlan0 set_network 0 key_mgmt NONE
sudo wpa_cli -i wlan0 set_network 0 auth_alg OPEN
sudo wpa_cli -i wlan0 set_network 0 group WEP40
sudo wpa_cli -i wlan0 set_network 0 wep_key1 \"${PASSWORD}\"
sudo wpa_cli -i wlan0 set_network 0 wep_tx_keyidx 1

