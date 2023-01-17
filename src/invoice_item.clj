(ns invoice-item)
(use 'clojure.test)

(defn- discount-factor [{:invoice-item/keys [discount-rate]
                         :or                {discount-rate 0}}]
  (- 1 (/ discount-rate 100.0)))

(defn subtotal
  [{:invoice-item/keys [precise-quantity precise-price discount-rate]
    :as                item
    :or                {discount-rate 0}}]
  (* precise-price precise-quantity (discount-factor item)))



(defn Item [id quantity price discount-rate]
  {:invoice-item/id                id
   :invoice-item/precise-quantity  quantity
   :invoice-item/precise-price     price
   :invoice-item/discount-rate     discount-rate})



(deftest itemsDiscount
  (let [item1 (Item 1 5 29.99 5)
        item2 (Item 2 3 15.00 10)
        item3 (Item 3 7 10.99 50)]
    (is (= (subtotal item1) 142.4525))
    (is (= (subtotal item2) 40.5))
    (is (= (subtotal item3) 38.465))))


(deftest noDiscount
  (let [item1 (Item 3 8 5.99 0)
        item2 (Item 4 1 50.99 0)
        item3 (Item 5 2 4.99 0)]
    (is (= (subtotal item1) 47.92))
    (is (= (subtotal item2) 50.99))
    (is (= (subtotal item3) 9.98))))



(run-tests)

