(ns my-app.service.impl.mongo-entity-store
  (:require [monger.collection :as mongo-document]
            [monger.core :as mongo]
            [my-app.service.entity-store :as entity-store-service])
  (:import [org.bson.types ObjectId]))

; Shamelessly copied from https://stackoverflow.com/questions/10062967/clojures-equivalent-to-pythons-encodehex-and-decodehex
(defn hexify
  "Convert byte sequence to hex string"
  [coll]
  (let [hex [\0 \1 \2 \3 \4 \5 \6 \7 \8 \9 \a \b \c \d \e \f]]
    (letfn [(hexify-byte [b]
              (let [v (bit-and b 0xFF)]
                [(hex (bit-shift-right v 4)) (hex (bit-and v 0x0F))]))]
      (apply str (mapcat hexify-byte coll)))))

;; Strongly inspired by https://stackoverflow.com/questions/27262268/idiom-for-padding-sequences
(defn pad
  [n val coll]
  (take n (concat coll (repeat val))))

(defn s->oid
  [^String s]
  (->> (.getBytes s)
       (pad 12 0xFF)
       (hexify)
       (ObjectId.)))

(def coll
  "Collection in which entities are stored in MongoDB."
  "entities")

(defn create
  ([db id]
   (create db id {}))
  ([db id data]
   (mongo-document/insert-and-return db coll (assoc data :_id (s->oid id)))))

(defn fetch
  [db id]
  (mongo-document/find-map-by-id db coll (s->oid id)))

(defn save
  [db id data]
  (mongo-document/update-by-id db coll (s->oid id) data))

(defn delete
  [db id]
  (mongo-document/remove-by-id db coll (s->oid id)))

(deftype MongoEntityStore [db]
  entity-store-service/EntityStore
  (create [_this id] (create db id))
  (create [_this id data] (create db id data))
  (fetch [_this id] (fetch db id))
  (save [_this id data] (save db id data))
  (delete [_this id] (delete db id)))

(defn new-store
  "Convenience function for creating a NoSQL entity store."
  [uri]
  (let [{:keys [db]} (mongo/connect-via-uri uri)]
    (MongoEntityStore. db)))
