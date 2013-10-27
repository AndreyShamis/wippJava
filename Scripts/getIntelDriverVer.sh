#!/bin/bash

modinfo iwlwifi | grep "^version:" | grep -Go "linux.*"
