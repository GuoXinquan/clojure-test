(ns fetch-jpg.h2.h2-test
  (:require [clojure.java.jdbc  :as jdbc]
            [clojure.java.shell :refer :all]))
(def demo-settings
  {:classname   "org.h2.Driver"
   :subprotocol "h2:mem"
   :subname     "demo;DB_CLOSE_DELAY=-1"
   :user        "sa"
   :password    ""})

(jdbc/db-do-commands demo-settings
  (jdbc/create-table-ddl :filetable
    [:name "varchar(3200)"]
    [:path "varchar(3200)"]
    [:origname "varchar(3200)"]))

(def demo-settings
  {
   :classname   "org.h2.Driver"
   :subprotocol "h2:file"
   :subname     (str (System/getProperty "user.dir") "/" "demo")
   :user        "sa"
   :password    ""})

(def file-store-location
  ["D:\\Clojure"])

(def file-ext-watch
  #{"txt" "mp3"})

(defn get-extension [file]
  (let [re #"\.[^.\\]*$"]
    (re-find re file)))

(def to-lower clojure.string/lower-case)

(defn filter-by-ext [file]
  (if (= nil  (get-extension file))
    nil
    (contains? file-ext-watch
               (clojure.string/replace
                 (to-lower (get-extension file)) "." ""))))

(defn list-files [store]
  (filterv #(filter-by-ext %) (sh "ls" store)))

(defn insert-file [fileloc file origname]
  (jdbc/insert! demo-settings :filetable {}
                                       :name file
                                       :path fileloc
                                       :origname origname))

(def files-in-location (mapv #(list-files %) file-store-location))

(defn add-to-db []
  (let [mapped (map #(vec (list %1 %2)) file-store-location files-in-location)]
    (for [ [fileloc filelist] mapped]
      (for [file filelist]
        (insert-file fileloc (to-lower file) file)))))

(add-to-db)

(jdbc/query demo-settings ["select * from filetable limit 10"])

(defn sql-like [s]
  (str "select * from filetable where name like '%" s  "%'"))

(defn pretty-record [i rec]
  (println (str i ":"  (:path rec) "\\" (:name rec))))


(defn open-record [i rec target]
  (if (= i target)
    (sh "explorer.exe" (str "\"" (:path rec) "\\" (:name rec) "\""))))



(defn pretty-query [sql record-handler & {:keys [target] :or {target -1}}]
  (let [result (jdbc/query demo-settings [sql])]
    (loop [rest-result result i 0]
      (when (not (empty? rest-result))
        (if (= target -1)
          (record-handler i (first rest-result))
          (record-handler i (first rest-result) target))
        (recur (rest rest-result) (inc i))))))

(defn only-show [sql]
  (pretty-query sql pretty-record))

(defn only-open [sql target]
  (if (= target -1)
    (println "wrong target")
    (pretty-query sql open-record :target target)))

(defn match-substr
  ([s] (only-show (sql-like s)))
  ([s target] (only-open (sql-like s) target)))

(match-substr "big")

(match-substr "big" 0)