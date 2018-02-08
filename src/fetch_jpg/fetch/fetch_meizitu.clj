(ns fetch-jpg.fetch.fetch-meizitu
  (:require [clojure.java.io :as io]
    [net.cgrand.enlive-html :as html]
    [clojure.string :as str]
    [org.httpkit.client :as http]
    [clj-http.client :as client]))

(defn url [num] (str "http://www.meizitu.com/a/" num ".html"))

(defn fetch-img
  [num]
  (try
    (let [{:keys [status body]} (client/get (url num))]
      (Thread/sleep 1000)
      (if (= 200 status)
        (let [uri-list (-> (html/html-snippet body))])))
                           ;(html/select [:.postContent]))])))
    (catch Exception e
      (Thread/sleep 1000)
      (fetch-img num))))



(comment
  @(http/get "http://www.meizitu.com/")
  (url 5591)
  (client/get "http://www.meizitu.com/a/5591.html")
  (fetch-img 5591))