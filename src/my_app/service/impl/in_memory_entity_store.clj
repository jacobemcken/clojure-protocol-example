(ns my-app.service.impl.in-memory-entity-store
  (:require [my-app.service.entity-store :as entity-store-service]))

(defn create
  ([store-atom id]
   (create store-atom id {}))
  ([store-atom id data]
   (swap! store-atom assoc id data)))

(defn fetch
  [store-atom id]
  (get @store-atom id))

(defn save
  [store-atom id data]
  (swap! store-atom assoc id data))

(defn delete
  [store-atom id]
  (swap! store-atom dissoc id))

(deftype InMemoryEntityStore [store-atom]
  entity-store-service/EntityStore
  (create [_this id] (create store-atom id))
  (create [_this id data] (create store-atom id data))
  (fetch [_this id] (fetch store-atom id))
  (save [_this id data] (save store-atom id data))
  (delete [_this id] (delete store-atom id)))

(defn new-store
  "Convenience function for creating an in memory entity store."
  [store-atom]
  (InMemoryEntityStore. store-atom))