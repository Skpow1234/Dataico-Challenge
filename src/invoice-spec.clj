(ns invoice-spec
  (:require [clojure.spec.alpha :as s] [clojure.data.json :as j]))
(use 'clojure.walk)

(s/def :customer/name string?)
(s/def :customer/email string?)
(s/def :invoice/customer (s/keys :req [:customer/name
                                       :customer/email]))

(s/def :tax/rate double?)
(s/def :tax/category #{:iva})
(s/def ::tax (s/keys :req [:tax/category
                           :tax/rate]))
(s/def :invoice-item/taxes (s/coll-of ::tax :kind vector? :min-count 1))

(s/def :invoice-item/price double?)
(s/def :invoice-item/quantity double?)
(s/def :invoice-item/sku string?)

(s/def ::invoice-item
  (s/keys :req [:invoice-item/price
                :invoice-item/quantity
                :invoice-item/sku
                :invoice-item/taxes]))

(s/def :invoice/issue-date inst?)
(s/def :invoice/items (s/coll-of ::invoice-item :kind vector? :min-count 1))

(s/def ::invoice
  (s/keys :req [:invoice/issue-date
                :invoice/customer
                :invoice/items]))

(defn reader [key value]
  (case key
    :issue_date (.parse
                 (java.text.SimpleDateFormat. "dd/MM/yyyy")
                 value)
    :payment_date (.parse
                   (java.text.SimpleDateFormat. "dd/MM/yyyy")
                   value)
    :tax_category :iva
    :tax_rate (double value)
    value))

(defn jsonFormatter [filename] 
  (let [jsonFile (j/read-str (slurp filename) 
 :key-fn keyword :value-fn reader)] 
    (->> (postwalk-replace {
     :issue_date :invoice/issue-date 
     :customer :invoice/customer
      :items :invoice/items
      :company_name :customer/name
      :email :customer/email
      :price :invoice-item/price 
       :quantity :invoice-item/quantity
      :sku :invoice-item/sku
     :taxes :invoice-item/taxes
     :tax_category :tax/category
     :tax_rate  :tax/rate} jsonFile) (:invoice))))

(s/valid? ::invoice (jsonFormatter "src/invoice.json"))