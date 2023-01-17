(ns solucion-1 
  (:require [clojure.edn :as clojure.edn]))

(def invoice (clojure.edn/read-string (slurp "src/invoice.edn")))


(defn problem [invoice]
  (->> (get invoice :invoice/items)
       (filter (fn [item]
                 (not (and (item :taxable/taxes) (item :retentionable/retentions)))))
       (filter (fn [lazseq]
                 (or (= 19 (get (first (get lazseq :taxable/taxes)) :tax/rate))
                     (= 1 (get (first (get lazseq :retentionable/retentions)) :retention/rate)))))))
(print (problem invoice))