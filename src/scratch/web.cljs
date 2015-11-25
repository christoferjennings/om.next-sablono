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

(def controls (om/factory Controls))

(defui BasicDiv
  Object
  (render [this]
          (html [:div#basic-div "This is a basic om div with no nesting."
                 [:hr]])))

(def basic-div (om/factory BasicDiv))

(defui NestingDiv
  Object
  (render [this]
          (html [:div#nesting-div "This is a nesting om div."
                 [:div.nested-div "This is in the nested div."]
                 [:div.nested-div "This is in the nested div."]
                 [:hr]])))

(def nesting-div (om/factory NestingDiv))

(defui NestingWithFor
  Object
  (render [this]
          (html [:div#nesting-with-x "This is a nesting om div."
                 (for [x (range 2)]
                   [:button.nested-button {:key x} "This is in the nested div."])
                 [:hr]])))

(def nesting-with-for (om/factory NestingWithFor))

(defui Top
  Object
  (render [this]
          (let [p (om/props this)]
            (html [:div#top
                   (controls p)
                   (basic-div)
                   (nesting-div)
                   (nesting-with-for)
                   ]))))

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
   Top
   (gdom/getElement "app")))
