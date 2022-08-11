(ns my-app.service.entity-store)

(defprotocol EntityStore
  "All operations to the store are atomic (e.g. a DB implementation
   would use transactions or something similar)."
  (create [this id] [this id initial-data]
    "Creates a new entity in the store, and returns a map representing
     the new entity.")
  (fetch [this id]
    "Fetches (reads) an entity from the store or returns nil if it
     doesn't exist.")
  (save [this id data]
    "Saves (updates) an entity with the id `id` overwriting its data,
     returns a map representing the updated entity.")
  (delete [this id]
    "Deletes an entity with the id `id` from the store and returns
     nil."))
