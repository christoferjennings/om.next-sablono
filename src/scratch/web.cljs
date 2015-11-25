(ns scratch.web
  (:require
   [cljs.pprint    :refer [pprint]]
   [goog.dom       :as gdom]
   [om.core        :as _]
   [om.next        :as om    :refer-macros [defui]]
   [sablono.core   :as html  :refer-macros [html]]))

;;(enable-console-print!)

(declare reconciler)

;; -----------------------------------------------------------------------------
;; Parsing

(defmulti mutate om/dispatch)

(defmethod mutate 'game/move
  [{:keys [state] :as env} key params]
  (let [{:keys [dir dis]} params
        state' { :distance dis}]
    {:action #(reset! state state')}))

;; -----------------------------------------------------------------------------
;; Components

(defui Controls
  Object
  (render [this]
          (pprint "Controls.render")
          (html [:div.controls
                 {:key "controls"}
                 [:select#distance
                  {:key "distance"}
                  (for [x (range 1 9)]
                    [:option x])]
                 [:button
                  {:key "gobtn"
                   :on-click (fn [e]
                               (let [dis (int (.-value (gdom/getElement "distance")))]
                                 (.log js/console "before transact!: " (:distance @reconciler))
                                 (om/transact! reconciler
                                               `[(game/move {:dis ~dis})]))
                               (.preventDefault e))}
                  "go"]])))


(def parser
  (om/parser {:mutate mutate}))

(def reconciler
  (om/reconciler
   {:state {:distance 0}
    :parser parser}))

(defn main []
  (.log js/console "main ---------------------------------")
  (om/add-root!
   reconciler
   Controls
   (gdom/getElement "app")))
