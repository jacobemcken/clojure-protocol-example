(ns my-app.core
  (:require [my-app.service.entity-store :as entity-store-service]
            [my-app.service.impl.mongo-entity-store :as mongo-entity-store]))

(def entity-store
  (mongo-entity-store/new-store "mongodb://admin:secret@172.21.0.2/customer1"))

(defn apply-business-logic
  [{:keys [entity-id] :as _event}]
  (when-let [entity (entity-store-service/fetch entity-store entity-id)]
    (if-not (= (:name entity) "Donald Duck")
      entity
      (do ; Someone have been testing (again) - cleanup
        (entity-store-service/delete entity-store entity-id)
        nil))))