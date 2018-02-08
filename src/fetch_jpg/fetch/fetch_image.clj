(ns fetch-jpg.fetch.fetch-image
  (:require [clojure.java.io :as io]
            [net.cgrand.enlive-html :as html]
            [clojure.string :as str]
            [clj-http.client :as client]))

(def file "D:/douban/")

(def url-1024 "http://www.t66y.com/thread0806.php?fid=16&search=&page=")

(defn copy-uri-to-file [file uri]
  (with-open [in (clojure.java.io/input-stream uri)
              out (clojure.java.io/output-stream (str file (System/currentTimeMillis) "." (last (str/split (last (str/split uri #"/")) #"\."))))]
    (clojure.java.io/copy in out)))


(defn parse-a
  [a]
  (-> (get-in a [:attrs])
      :href
      (->> (re-find #"^[htm_data].*[html]$"))))

(defn fetch-jpg-uri
  [num]
  (try
    (let [{:keys [status body]} (client/get (str url-1024 num) {:proxy-host "127.0.0.1"
                                                                  :proxy-port 1080})]
       (if (= 200 status)
           (let [resp (-> (html/html-snippet body)
                          (html/select [:.tr3.t_one.tac :td :a]))]
             (->> (map parse-a resp)
                  (filter #(if (nil? %) false true))))))
    (catch Exception e)))

(defn fetch-price
  [url-str]
  (let [url-jpg (str "http://www.t66y.com/" url-str)
        {:keys [status body]} (client/get (str url-1024 num) {:proxy-host "127.0.0.1"
                                                               :proxy-port 1080
                                                               :headers {"Host" "www.t66y.com"
                                                                         "Referer" (str "http://www.t66y.com/" url-str)
                                                                         "User-Agent" "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36"}
                                                               :as "GBK"})]
    (if (= 200 status)
        (let [uri-list (-> (html/html-snippet body))]
                           ;(html/select  [:table :div]))]
          body))))

(comment
  (fetch-price "htm_data/16/1802/2942381.html")
  (fetch-jpg-uri 1))