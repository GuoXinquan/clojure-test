(ns fetch-jpg.fetch.fetch-dbmz
  (:require [clojure.java.io :as io]
            [net.cgrand.enlive-html :as html]
            [org.httpkit.client :as http]
            [clojure.string :as str])
  (:import (java.util Random)))

(def file "D:/douban/")

(defn copy-uri-to-file [file uri]
  (with-open [in (clojure.java.io/input-stream uri)
              out (clojure.java.io/output-stream (str file (System/currentTimeMillis) "." (last (str/split (last (str/split uri #"/")) #"\."))))]
    (clojure.java.io/copy in out)))

(defn fetch-jpg-uri
  [num]
  (Thread/sleep (rand-int 250))
  (let [resp (-> @(http/get (str "https://www.dbmeinv.com/dbgroup/" num)))]
    (if (= 200 (:status resp))
      (let [uri (some-> (html/html-snippet resp)
                        (html/select [:img])
                        second
                        (get-in [:attrs :src])
                        (str/split #"\"")
                        second
                        (str/split #"\\")
                        first)]
        (if-not (nil? uri)
          (do
            (println "当前id " num " 成功抓取图片 " uri)
            (copy-uri-to-file file uri))))
      (println "当前id " num " 失败"))))

(comment
  (fetch-jpg-uri 524)
  (map fetch-jpg-uri (range 1 1000)))