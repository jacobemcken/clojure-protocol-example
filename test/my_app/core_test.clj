(ns my-app.core-test
  (:require [clojure.test :refer [deftest is testing]]
            [my-app.core :as sut] ; System Under Testing
            [my-app.service.impl.in-memory-entity-store :as in-memory-entity-store]))

(deftest apply-business-logic
  (testing "Normal entity"
    (with-redefs [my-app.core/entity-store
                  (in-memory-entity-store/new-store
                    (atom {"123" {:name "John Doe"}}))]
      (is (= {:name "John Doe"} (sut/apply-business-logic {:entity-id "123"})))))
  (testing "Bad entity"
    (let [store-atom (atom {"123" {:name "Donald Duck"}})]
      (with-redefs [my-app.core/entity-store
                    (in-memory-entity-store/new-store store-atom)]
        (is (contains? @store-atom "123"))
        (is (nil? (sut/apply-business-logic {:entity-id "123"})))
        (is (not (contains? @store-atom "123"))))))
  (testing "Unknown entity"
    (with-redefs [my-app.core/entity-store
                  (in-memory-entity-store/new-store (atom {}))]
      (is (nil? (sut/apply-business-logic {:entity-id "non-existing"}))))))
