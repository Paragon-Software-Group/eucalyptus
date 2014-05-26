#PARAGON EUCALYPTUS VERSION

The active branch is paragon/3.4/build

We build _java part only_.

## OS

CentOS 6.5 x86_64

## INSTALL DEPENDENCIES

yum install git
yum install gcc
yum install ant
yum install ivy
yum install java-1.7.0-openjdk-devel
export JAVA_HOME=/usr/lib/jvm/java-1.7.0
yum install axis2c
export AXIS2C_HOME=/usr/lib64/axis2c #need to recheck if /usr/lib64/axis2c exists, use /usr/lib/axis2c otherwise
yum install libvirt

## CHECKOUT CODE

git clone https://github.com/Paragon-Software-Group/eucalyptus.git -b paragon/3.4/build # Note, original location is git clone https://github.com/eucalyptus/eucalyptus.git
git clone --depth=1 -b master git://github.com/eucalyptus/eucalyptus-cloud-libs.git eucalyptus/lib # approx. 200MB of extra libraries
cd eucalyptus

## CHOOSE ONE OF THE WAYS TO BUILD:

### 1) THE HONEST WAY

git clone https://github.com/eucalyptus/eucalyptus-rpmspec.git
./configure --with-wsdl2c-sh=./eucalyptus-rpmspec/euca-WSDL2C.sh

...got errors on libvirt, which I didn't recover from


### 2) THE DIRTY HACK

cd clc
export ANT=/usr/bin/ant
make 

#it works! ;)
