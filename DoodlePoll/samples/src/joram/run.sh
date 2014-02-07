#!/bin/bash

ant clean compile
ant reset single_server &
ant mp4_admin #run in its own terminal
ant mp4_client -Darg0=nwendt22@gmail.com #run in its own terminal
