# DevOps

This section describes the steps required to automate the creation of the development environment images.

## Prerequisites
The following tools are required:

- [Oracle VM Virtual Box](https://www.virtualbox.org): the virtualisation provider running on the development machine. Creates virtual machines containing the Operating System, the required middleware components and the application services. As Docker is not supported if the host is not linux, it allows to boot up a linux box and run docker inside it.
- [Ansible](http://docs.ansible.com/intro_installation.html#getting-ansible): the application stack provisioning toolset.
- [Vagrant](http://docs.vagrantup.com/v2/installation/): the virtual machine management tool used to develop and test the environment automation (e.g. the provisioning scripts using ansible).
- [Vagrant Virtual Box Guest Plugin](https://github.com/dotless-de/vagrant-vbguest): to install / re-install virtual box guest additions if required. This is optional.
- [Packer](https://www.packer.io/): the tool used to create multiplatform images of the environment.

## Getting started

1. Ensure an internet connection exists
2. Open a console and navigate to the root of the [adam.platform](./) folder.
3. Run the command **$ sh build.sh**
4. Navigate to the [image/output](image/output) folder
5. Run the command **vagrant box add --name adam-dev adam-dev-centos71-virtualbox.box**
6. Check the box has been added by running **vagrant box list**
7. Navigate to the [adam.platform](./) folder
8. Run the command **vagrant up**
9. The ADAM's environment should be available on IP = 192.168.50.10
10. Add an entry in your host file as follows: "**adam   192.168.50.10**"
11. Wildfly can be accessed at [http://adam:8080](http://adam:8080)
12. ActiveMQ can be accessed at [http://adam:8161](http://adam:8161) 
13. MariaDb can be accessed at adam:3306

## Project files

Key files are:

- [Vagrantfile for application development](./Vagrantfile): use it to spin up the development environment once the base box has been imported
- [Vagrantfile for environment development](./provision/Vagrantfile): use it to spin up a box for development of the environment automation (e.g. ansible provisioning scripts)
- [Image](./image) folder: contains the packer [template](./image/adam-dev-centos71-vb.json) and the linux [kickstart](./image/centos71-ks.cfg) file required to create the Vagrant base box.
- [Provision](./provision) folder: contains the ansible and shell provisioning scripts.









