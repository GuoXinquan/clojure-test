(ns fetch-jpg.fetch.fetch-csdn.fetch-csdn-doc
  (:require [net.cgrand.enlive-html :as html]
            [org.httpkit.client :as http]
            [clojure.string :as str]))

(def main-url "http://blog.csdn.net/xiejianming")

(def url "http://blog.csdn.net/xiejianming/article/details/2545245")

(defn resp [url] @(http/get url))

(defn get-url-list
  []
  (let [{:keys [body status]} (resp main-url)]
    (if (= status 200)
        (-> (html/html-snippet body)
            (html/select [:.blog-unit :a])
            (->> (map #(:href (:attrs (select-keys % [:attrs])))))))))

(defn fetch-doc
  [url]
  (let [{:keys [status body]} (resp url)]
    (if (= 200 status)
      (-> (html/html-snippet body)
          (html/select [:.htmledit_views :p])
          (html/texts)
          (->> (map #(if (= "" %) nil %))
               (filter #(if (nil? %) false true)))))))
(comment
  (do (println "who are you")
      (println "hello," (read-line)))
  (fetch-doc url)
  (pmap #(fetch-doc %) (get-url-list)))
