(ns hackernews.routes
  (:gen-class)
  (:use compojure.core [hiccup.middleware :only (wrap-base-url)])
  (:require [compojure.route           :as route]
            [compojure.handler         :as handler]
            [compojure.response        :as response]
            [ring.adapter.jetty        :as jetty]
            [environ.core              :refer [env]]
            [hackernews.controllers.stories :as stories]
            [hackernews.controllers.users   :as users]))

(defroutes main-routes
  (GET "/" [limit] (stories/index limit))
  (GET "/stories/:id" [id] (stories/show id))
  (GET "/users/:username" [username] (users/show username))

  (route/resources "/")
  (route/not-found "Page not found."))

(defn app
  "This is the handler for our routes. We wrap the base url to
  Use Hiccup."
  []
  (-> (handler/site main-routes)
      (wrap-base-url)))

(defn -main
  "This is the entry point into the application. It runs the server."
  [& [port]]
  (let [chosen-port (or port (env :port) "3000")
        parse-int #(Integer/parseInt (re-find #"\A-?\d+" %))]
    (jetty/run-jetty (app) {:port (parse-int chosen-port) :join? false})))
