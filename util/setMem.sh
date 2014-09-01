#!/bin/sh

[[ ! -n $0 ]];
then
	memJava=$0
else
	memJava="2048m"
fi
cat /etc/eucalyptus/eucalyptus.conf | sed "s/CLOUD_OPTS=\".*\"/CLOUD_OPTS=\"-Xmx $memJava\"/">/etc/eucalyptus/eucalyptus.conf
