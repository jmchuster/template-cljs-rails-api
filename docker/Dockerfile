# Use phusion/passenger-full as base image. To make your builds reproducible, make
# sure you lock down to a specific version, not to `latest`!
# See https://github.com/phusion/passenger-docker/blob/master/Changelog.md for
# a list of version numbers.
FROM phusion/passenger-ruby22:0.9.18

# Set correct environment variables.
ENV HOME /root

# Use baseimage-docker's init process.
CMD ["/sbin/my_init"]

# Install packages
RUN apt-get update
RUN apt-get -y install openjdk-8-jdk

# Clean up APT when done.
RUN apt-get clean && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

# write our own nginx.conf
ADD nginx/nginx.conf /etc/nginx/nginx.conf
# add environment variables to nginx
ADD nginx/main.d/env.conf /etc/nginx/main.d/env.conf
# remove the default site
RUN rm /etc/nginx/sites-enabled/default
# set my default site
ADD nginx/sites-enabled/server.conf /etc/nginx/sites-enabled/server.conf
# add our ssl certs
ADD ssl /etc/ssl

# forward request and error logs to docker log collector
RUN ln -sf /dev/stdout /var/log/nginx/access.log
RUN ln -sf /dev/stderr /var/log/nginx/error.log

# enable nginx
RUN rm -f /etc/service/nginx/down

# add boot
ADD bin/boot /usr/local/bin/boot
ADD bin/lein /usr/local/bin/lein

# boot options
ENV BOOT_JVM_OPTIONS -Xmx2g -client -XX:+TieredCompilation -XX:TieredStopAtLevel=1 -XX:+UseConcMarkSweepGC -XX:+CMSClassUnloadingEnabled -Xverify:none
ENV BOOT_HOME /home/app/web/.boot

# startup scripts that can be passed to /sbin/my_init -- COMMAND ARGUMENTS...
# exit 0 if you want to just run it and then shut it down
ADD my_startup /etc/my_startup

# for webserver to use
RUN mkdir -p /tmp/pids
RUN mkdir -p /tmp/sockets

WORKDIR /home/app
