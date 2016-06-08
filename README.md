# ClojureScript / Rails-API application template

This is the basic template for a Docker-based ClojureScript frontend with a Rails-API backend, for development on a Mac.

Nginx is responsible for serving the precompiled static js and css assets.  In development mode, `boot` can provide a hot-reload and repl environment.

## Features

- [Rails-API](https://github.com/rails-api/rails-api) for a lighter, trimmer backend than traditional Rails, running the latest Rails 5 RC1
- serving and reading JSON requests in the [{json:api}](http://jsonapi.org) style
- ClojureScript frontend built utilizing [Reagent](https://github.com/reagent-project/reagent) and [Secretary](https://github.com/gf3/secretary)
- watching, building, and hot-reloading (JS, CSS, and HTML) powered by [Boot](https://github.com/boot-clj/boot)
- static assets and api served via an Nginx reverse proxy without requiring subdomains
- easily swap between SSL-optional and SSL-required via Nginx
- [JWT](https://jwt.io/) for stateless authentication and authorization
- assets, api, and proxy packaged in a single Docker container built on Phusion's [Baseimage-docker](https://github.com/phusion/baseimage-docker)
- helper scripts for a "one-click" creation of the local Docker environment from scratch, including Postgres and Redis
- tiered clean reloads, each in < 5s
  - hot-reload to destroy lexical state
  - refresh to destroy browser state
  - restart to destroy server state
  - rebuild to destroy container state

## Get started

To get started, you'll need to do the following:

### 1. Create the project folder within `~/`

The folder needs to be within `~/` in order to support NFS volume sharing with the container.

The project folder contains three folders
- `/docker`, which contains helper scripts and server config files
- `/api`, which contains the rails-api backend
- `/web`, which contains the cljs frontend

### 2. Install Docker and Dinghy

This is how we access Docker via a Mac.  Since we can't directly map Docker onto Mac, but need to run it in a VM, Dinghy wraps that abstraction layer to give us nicer, faster containers (mainly through NFS support).

If you already have Docker installed, it's preferable to first delete any existing Docker machines, such that you're only running one VM.  Having more than one causes SSL problems depending on the order of machine startup (https://github.com/docker/machine/issues/531).

The basic sets of commands to install Docker and Dinghy are

    $ brew update
    $ brew install docker
    $ brew install docker-machine
    $ brew tap codekitchen/dinghy
    $ brew install dinghy
    $ eval $(dinghy env)

To save yourself the effort of having to run `dinghy env` every time you open a terminal, you can put its output into `~/.bash_profile`.

### 3. Start the container / app

1. Navigate to your project directory
2. Build the container with `docker/build`.  This will take a while the very first time as it needs to download the containers' base images, install applications from apt, install rubygems, and build your semantic.css file.  These files are then cached for future builds.
3. Once `docker/build` completes successfully, it will automatically place you into the container's bash prompt
4. Start the rails-api server with `api/server`.  We'll refer to this terminal tab as the *rails-api terminal tab*.
5. Open up a new terminal tab and connect to the container with `docker/bash`.  Start the cljs hot-reloader with `web/watch`.  We'll refer to this terminal tab as the *cljs terminal tab*.
6. Visit it at [http://my_app.docker](http://my_app.docker)

### 4. Restart the rails-api server

1. Go to your rails-api terminal tab
2. Stop your rails-api server with `Ctrl-C`
3. Start your rails-api server with `api/server`
4. Visit it at [http://my_app.docker/api](http://my_app.docker/api)

### 5. Restart the cljs hot-reloader

1. Go to your cljs terminal tab
2. Stop your cljs hot-reloader with `Ctrl-C`. You can still visit the site, but it will not reflect any changes you make to the code.
3. Start your cljs hot-reloader with `web/watch`
4. Visit it at [http://my_app.docker](http://my_app.docker)

### 6. Restart the container

1. Go to either your rails-api terminal tab or your cljs terminal tab
2. Stop your app with `Ctrl-C`
3. Exit the container with `exit`
4. Rebuild the container with `docker/build`
5. This will terminate the other tab's docker connection.  Connect to the rebuilt container with `docker/bash`.
6. In one tab, start your rails-api server with `api/server`
7. In the other tab, start your cljs server with `web/watch`
8. Visit it at http://my_app.docker

### 7. Connect to the cljs REPL

1. Open up a new terminal tab and connect to the container with `docker/bash`.  Start the repl with `web/repl`.  We'll refer to this terminal tab as the *repl terminal tab*.
2. From the repl, execute `(start-repl)`.  Your browser javascript console should now say `Opened Websocket REPL connection`.
3. You can now execute code in your repl and see it reflected in the browser, such as `(js/alert "Hello world")`
4. Before you exit your repl terminal tab, make sure to close the repl connection with `:cljs/quit`
5. If you neglect to close the repl connection, the next time you try to `(start-repl)`, it will complain `Address already in use`.  You'll need to run `:cljs/quit`, then `(start-repl)`, and then refresh the browser.

### 7. Deploy to production

1. From the project directory, build your Base container with

   `docker build -t local/my_app:latest docker`

2. Build your Production container with

   `docker build -t local/my_app:latest -f docker/Dockerfile.prod .`

3. Run your Production container with the following environment variables

  * RAILS_ENV=production
  * DATABASE_URL
  * RAILS_RESQUE_REDIS
  * SECRET_KEY_BASE

You can generate a SECRET_KEY_BASE with

    require 'securerandom'
    SecureRandom.hex(64)

The Production container's launch command should be

    /sbin/my_init --sh -c '/etc/my_startup/start_server.sh'

As in development, nginx will launch automatically, while `start_server.sh` will launch the Rails-API server.  There is no need to launch anything for the ClojureScript JS and CSS, as they will be served via nginx as static assets.

## Other notes

### SSL

This application template comes with the default self-signed certificate and key that was generated by running

    $ openssl genrsa -des3 -passout pass:x -out server.pass.key 2048
    $ openssl rsa -passin pass:x -in server.pass.key -out server.key
    $ openssl req -new -key server.key -out server.csr
    $ openssl x509 -req -days 365 -in server.csr -signkey server.key -out server.crt

You might choose to replace these files in your lower environments, but at the very least, the Production container will remove these files.

The preferred method is to map a local volume on your Production server to `/etc/ssl` on your Production container, containing `server.crt` and `server.key`.

If you want to force ALL requests to go through SSL, you can uncomment the option in `application.rb`

    # config.force_ssl = true

or specify it in each of `development.rb`, `test.rb`, and `production.rb`.

### Postgres and Redis, in Development

On Postgres, in development, we automatically map `$APP` and `$APP_test` as our database names.  This makes it relatively easy to run multiples of applications, as long we name the applications differently to begin with.

On Redis, we DO NOT have a corresponding mapping to separate our applications.  Redis does not have database names, but rather has database numbers (from 0 to 15, by default).  Since there is no obvious hasing from $APP to an integer, we set the database number to 0, and leave it as an exercise to the user to change it for each application.

### Building Semantic UI

Semantic UI has its own build commands `gulp build` and `gulp watch`.  The `docker/build` script is responsible for executing `gulp build` when it detects there is no existing `dist/semantic.css` file.  The boot task `gulp-watch` is responsible for executing `gulp watch` as part of `boot dev`.

If you make changes to the `semantic` folder while it is not under watch, you'll need to manually rebuild it with `cd web && gulp build`.

To update Semantic UI, you can run

    $ cd web
    $ npm update semantic-ui

Because npm likes to aggressively chown, you may need to run these commands from your Mac, rather than within the Docker container.

### Server restarting and reloading

Once cached, the Docker container takes around 5 seconds to rebuild.  Changes to configuration files on the container, such as the nginx server.conf or to the Dockerfile itself will require a container build.  Since all the container's contents are destroyed with each build, we utilize many cache folders that are mapped to local volumes, such as `vendor/bundle`, `.boot`, `.m2`, `semantic/dist`, and `target`.

The rails-api server takes around 5 seconds to start.  Changes to the `app` folder are synchronously loaded with each request.  Changes to the application configuration or to dependencies will require a server restart.

The static assets are immediately available upon the container starting, but the hot-reloader takes around 40 seconds to start.  Once started, changes to the `css`, `src`, and `resources` folders are asynchronously loaded via the file watcher.  These changes are compiled and pushed to the nginx static folder as well as up to the browser via websocket for hot-reloading, taking roughly 5 seconds in total.  Changes to the build task or to dependencies will require a hot-reloader restart.
